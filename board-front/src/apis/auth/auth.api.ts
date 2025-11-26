// auth.api.ts

import type { LoginRequest, LoginResponse, PasswordResetRequestDto, SignupRequest } from "@/types/auth/auth.dto";
import { privateApi, publicApi } from "../common/axiosInstance";
import type { ResponseDto } from "@/types/common/ResponseDto";
import { AUTH_PATH } from "./auth.path";

export const authApi = {
  // async 함수의 반환타입: Promise
  loginApi: async (req: LoginRequest) => {
    // axios.메서드<메서드반환타입>();
    const res = await publicApi.post<ResponseDto<LoginResponse>>(
      AUTH_PATH.LOGIN,
      req
    );

    return res.data;
  },

  /** refresh 토큰 재발급 */
  refreshApi: async () => {
    const res = await publicApi.post<ResponseDto<LoginResponse>>(
      AUTH_PATH.REFRESH
    );
    return res.data;
  },

  /** 로그아웃 (서버 API 없어도 프론트에서 동작 가능) */
  logoutApi: async () => {
    const res = await privateApi.post<ResponseDto<void>>(AUTH_PATH.LOGOUT);
    // 서버에 요청할 게 없다면 빈 Promise 반환
    return res.data;
  },

  /** 회원가입 */
  signupApi: async (body: SignupRequest) => {
    const res = await publicApi.post<ResponseDto<void>>(
      AUTH_PATH.SIGNUP,
      body
    );
    return res.data;
  },

  /** 비밀번호 재설정 토큰 유효성 확인 */
  verifyPasswordTokenApi: async (token: string) => {
    const res = await publicApi.get(
      `${AUTH_PATH.PASSWORD_VERIFY}?token=${encodeURIComponent(token)}`
    );
    return res.data;
  },

  /** 비밀번호 재설정 */
  resetPasswordApi: async (body: PasswordResetRequestDto) => {
    const res = await publicApi.post<ResponseDto<void>>(
      AUTH_PATH.PASSWORD_RESET,
      body
    );
    return res.data;
  }
};