package org.example.boardback.security.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.common.enums.user.AuthProvider;
import org.example.boardback.common.enums.user.RoleType;
import org.example.boardback.entity.user.Role;
import org.example.boardback.entity.user.User;
import org.example.boardback.repository.user.RoleRepository;
import org.example.boardback.repository.user.UserRepository;
import org.example.boardback.security.oauth2.user.GoogleOAuth2UserInfo;
import org.example.boardback.security.oauth2.user.KakaoOAuth2UserInfo;
import org.example.boardback.security.oauth2.user.NaverOAuth2UserInfo;
import org.example.boardback.security.oauth2.user.OAuth2UserInfo;
import org.example.boardback.security.user.UserPrincipalMapper;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

// 엔티티별 service 폴더에 묶지 않고 security 폴더 내부에 넣는게 일반적임
// DB 사용자 생성 / 업데이트
@Slf4j // Simple Logging Facade for Java: 로깅 기능을 간편하게 하는 Lombok 어노테이션
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    // User 엔티티를 Spring Security에서 사용하는 UserPrincipal로 변환하는 매퍼
    private final UserPrincipalMapper userPrincipalMapper;

    /**
     * OAuth2 로그인 시 provider(google, kakao, naver)로부터 사용자 정보를 가져와
     *  , 프로젝트 서비스의 User 엔티티로 저장/업데이트 한 뒤
     *  >> 최종적으로, Spring Security가 이해할 수 있는 OAuth2User(UserPrincipal) 형태로 리턴
     * */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. provider로 부터 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        /*
        *  Spring Security OAuth2 Client 의 역할
        * - Authorization Code → Access Token 교환
        * - Access Token → UserInfo API 호출
        * - 응답 JSON → OAuth2User로 변환
        * - provider 마다 attribute key가 달라도 통일된 포맷으로 제공
        *
        * => 개발자는 Spring 이 가져온 사용자 정보를 DB의 User 엔티티에 upsert, UserPrincipal 로 변환하면 됨
        * */

        // 2. registrationId로 provider 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = mapProvider(registrationId);

        // 3. provider 별로 attributes를 공통 포맷으로 변환
        OAuth2UserInfo userInfo = convertToUserInfo(provider, oAuth2User.getAttributes());

        // 4. DB에 사용자 생성/수정
        User user = upsertUser(provider, userInfo);

        // 5. 프로젝트 서비스에서 사용하는 UserPrincipal로 래핑
        return userPrincipalMapper.toPrincipal(user.getUsername());
    }

    private AuthProvider mapProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "kakao" -> AuthProvider.KAKAO;
            case "naver" -> AuthProvider.NAVER;
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + registrationId);
        };
    }

    private OAuth2UserInfo convertToUserInfo(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
        };
    }

    /**
     * provider + providerId를 기준으로 User를 조회하여
     *
     * 1) 이미 존재하면: 이름/이메일 등 프로필 정보를 최신 상태로 업데이트
     * 2) 존재하지 않으면: 신규 소셜 로그인 User를 생성한 뒤 USER 권한 부여
     *
     * 최종적으로 User 엔티티 반환
     * */
    @Transactional
    protected User upsertUser(AuthProvider provider, OAuth2UserInfo userInfo) {
        // 소셜 서비스에서 제공하는 고유 사용자 ID (구글 sub, 카카오 id, 네이버 id 등)
        String providerId = userInfo.getId();

        // 소셜에서 가져온 데이터 저장
        String email = userInfo.getEmail();
        String name = userInfo.getName();

        // provider + providerId 조합(유니크 설정)으로 User 조회
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(user -> {
                    // 1) 이미 가입된 유저인 경우
                    //      : 이름, 이메일 같은 프로필 정보를 최신값으로 갱신
                    user.updateOauthProfile(name, email);
                    return user;
                })
                .orElseGet(() -> {
                    // 2) 처음 로그인하는 유저인 경우 -> 새 User 엔티티 생성
                    User newUser = User.createOauthUser(
                        provider,
                        providerId,
                        email,
                        name
                    );

                    // 기본 권한 USER 를 DB 에서 조회
                    Role userRole = roleRepository
                            .findById(RoleType.USER)
                            .orElseThrow(() -> new IllegalArgumentException("USER 권한이 DB에 없습니다."));

                    // 생성된 유저에게 USER 권한 부여
                    newUser.grantRole(userRole);

                    return userRepository.save(newUser);
                });
    }
}
