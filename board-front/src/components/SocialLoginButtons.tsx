import React from 'react'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
const FRONT_REDIRECT_URL =
  import.meta.env.VITE_OAUTH2_REDIRECT_URL ??
  "http://localhost:5173/oauth2/callback";

/**
 * 소셜 로그인 버튼 컴포넌트
 * - 클릭 시 백엔드의 Spring Security OAuth2 엔드포인트로 리다이렉트
 * - 백엔드 기본 OAuth2 경로: /oauth2/authorization/{provider}
 */
export const SocialLoginButtons: React.FC = () => {
  // == 이벤트 핸들러 == //
  // : 소셜 로그인 요청 함수
  const handleSocialLogin = (provider: "google" | "kakao" | "naver") => {
    
    // 실제 로그인 요청 URL 구성
    // /oauth2/authorization/google?redirect_uri/http://localhost:5173/oauth2/callback
    // : 실제 경로값을 URL 에 포함할 경우 반드시 encodeURIComponent로 URI 값 변환
    const authUrl = `${API_BASE_URL}/oauth2/authorization/${provider}?redirect_uri=${encodeURIComponent(
      FRONT_REDIRECT_URL
    )}`;

    // 위 URL 로 브라우저 이동 -> OAuth2 인증 시작
    window.location.href = authUrl;
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 8, marginTop: 16 }}>
      <button onClick={() => handleSocialLogin("google")}>Google로 로그인</button>
      <button onClick={() => handleSocialLogin("kakao")}>Kakao로 로그인</button>
      <button onClick={() => handleSocialLogin("naver")}>Naver로 로그인</button>
    </div>
  );
};