package org.example.boardback.로그인OAuth2;

/*
* === OAuth2 ===
* : 사용자가 자신의 비밀번호를 직접 제공하지 않고도
*   , 다른 서비스(클라이언트)에게 자신의 정보에 접근할 수 있도록 권한을 위임하는 표준화 된 권한 부여 프로토콜
* > 비밀번호 제공 없이, (네이버, 구글, 카카오 등의)일부 기능만 사용할 수 있는 임시 토큰을 발급해주는 방식
*
* === OAuth2 사용 흐름 ===
* 1) 프론트에서 "소셜 계정 로그인" 버튼 클릭
*   : 브라우저가 백엔드 /oauth2/authorization/{sns-name} 으로 이동
*
* 2) Spring Security가 자동으로 소셜 로그인 페이지로 리다이렉트
*
* 3) 사용자가 소셜 로그인 완료
*   : 소셜 계정이 우리 서버의 /login/oauth2/code/{sns-name}으로 코드를 보냄
*
* 4) 서버 쪽 CustomOAuth2UserService가 코드를 AccessToken 으로 교환하고
*   , 사용자 정보 (이메일/닉네임 등)를 가져옴
*
* 5) 우리 DB에 해당 이메일의 유저가 있으면 -> 로그인
*                               없으면 -> 자동 회원가입 후 로그인
*
* 6) 로그인에 성공하면 OAuth2AuthenticationSuccessHandler 에서
*   - 프로젝트 서비스용 JWT Access/Refresh Token 생성
*   - 프론트 콜백 URL로 리다이렉트
*       http://localhost:5173/oauth2/callback?accessToken=...&refreshToken=...
*
* ※ 현재 User 엔티티에 OAuth2 사용을 위한 필드 설정 ※
*   : provider - GOOGLE, KAKAO, NAVER, LOCAL 구분
*   : providerId - 각 제공자의 유니크 ID (google sub, kakao id 등)
* */

public class 개요 {
}
/*
 * 🌈 전체 그림
* - OAuth: 구글 / 카카오 / 네이버 등이 로그인 대신 인증해주는 서비스, "구글아, 이 유저가 진짜 본인인지 좀 확인해줘"
* - 그럼 구글이 로그인 페이지를 띄워 주고 로그인 성공하면 우리 서버로 유저 정보를 보내줌
*
* 🧩 전체 큰 흐름을 직관적으로 요약
* 1. 프론트가 "구글로 로그인" 버튼을 누름
* 2. 구글 로그인 창으로 이동함
* 3. 사용자가 구글에 로그인함
* 4. 구글이 우리 서버로 "로그인 성공 정보"를 보냄
* 5. 서버는 해당 유저를 DB에서 찾거나 자동 회원가입함
* 6. 이후 서버는 JWT(Access, Refresh)를 만들어서 프론트에 전달함
* 7. 프론트는 JWT 저장 후 로그인 완료 처리
*
* 1) 프론트에서 소셜 로그인 버튼 클릭
* - 프론트가 다음 URL 로 이동함
* /oauth2/authorization/google
* /oauth2/authorization/kakao
* /oauth2/authorization/naver
* - 이 URL 을 누르면 Spring Security OAuth2 기능이 작동하기 시작ㅎ
* - 프론트가 따로 API 요청하는 것이 아님, 브라우저가 단순히 해당 URL 로 페이지 이동(redirect) 하는 것
*
* 2) Spring Security 가 자동으로 소셜 로그인 페이지로 리다이렉트
* - 스프링이 알아서 구글 로그인 화면으로 보내줌
* - 프론트가 이 과정을 제어할 필요 X
*
* 3) 로그인 후 소셜 서비스가 우리 서버로 코드(code)를 보냄
* - 사용자가 구글 로그인 성공하면 구글이 우리 서버의 아래 URL 로 이동시킴
* /login/oauth2/code/google
* /login/oauth2/code/kakao
* /login/oauth2/code/naver
* - 여기서 code(인가 코드)라는 임시번호를 줌
*
* 4) 서버의 CustomOAuth2UserSerivce 동작
* - 서버는 이 code를 가지고 구글/카카오/네이버에게 Access Token 을 요청함 (코드를 Access Token 과 교환)
* - 이 때의 Access Token 은 OAuth2UserRequest 에 들어있음
* - 그리고 이 토큰으로 사용자 정보(API)를 요청함
* 예)
* GET https://www.googleapis.com/oauth2/v3/userinfo
* GET https://kapi.kakao.com/v2/user/me
* GET https://openapi.naver.com/v1/nid/me
* - 이런 API 를 자동 호출하여 사용자 정보를 가져옴
*
* 5) DB 에 해당 유저가 존재하는 지 확인
*
* 6) 로그인 성공 후 서버가 JWT 토큰 발급
* - OAuth2AuthenticationSuccessHander 에서 Access, RefreshToken 생성
* - 프론트에게 전달해야하므로 redirect 함
*   http://localhost:5173/oauth2/callback?accessToken=AAAAA...&refreshToken=BBBBB...
* - JWT는 우리 서비스가 사용하는 인증 방식이기 때문에 소셜 로그인이든 로컬 로그인이든 최종 결과는 JWT 로그인이 됨
*
* 🎉 전체 흐름 요약 그림
* [프론트]
   ↓ ("구글로 로그인" 버튼 클릭)
 /oauth2/authorization/google

[스프링 시큐리티]
   ↓ (자동 리다이렉트)
[구글 로그인 페이지]

[사용자]
   ↓ 로그인 성공

[구글]
   ↓ code 전달
 /login/oauth2/code/google

[우리 서버]
   ↓ code → access token 교환
   ↓ 사용자 정보 조회
   ↓ DB 회원가입/로그인 처리
   ↓ JWT 생성
   ↓ 프론트로 리다이렉트

[프론트]
   ↓ JWT 저장 → 로그인 완료!

*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
* */
