// =======================================
// Auth DTO - Backend 기준 자동 변환
// =======================================

// 로그인 요청
export interface LoginRequest {
  username: string;
  password: string;
}

// 로그인 응답
export interface LoginResponse {
  accessToken: string;
  accessTokenExpiresInMillis: number;
}

type Gender = "M" | "F" | "N";

// 회원가입 요청
export interface SignupRequest {
  username: string;
  password: string;
  confirmPassword: string;
  nickname: string;
  email: string; 
  gender: Gender;
  provider: string;
}


export interface PasswordResetRequestDto {
  token: string;
  newPassword: string;
  confirmPassword: string;
}