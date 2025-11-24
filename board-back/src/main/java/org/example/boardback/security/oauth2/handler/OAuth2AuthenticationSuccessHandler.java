package org.example.boardback.security.oauth2.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 로그인 성공 시
 * - JWT Access / Refresh 발급
 * - 프론트엔드 콜백 URL로 리다이렉트하면서 토큰 전달
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
}
