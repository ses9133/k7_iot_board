// axiosInstance.ts

import { useAuthStore } from "@/stores/auth.store";
import axios, { AxiosError } from "axios";

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';

export const publicApi = axios.create({
  baseURL: API_BASE,
  withCredentials: true,  // refreshToken 쿠키 전달
});

export const privateApi = axios.create({
  baseURL: API_BASE,
  withCredentials: true,
});

/*
10개 API마다 refresh 재발급 요청을 보냄 → 서버 터짐 → 중복 발급 문제
-> 이걸 막기 위해 단 1번만 refresh 요청을 보내도록 하는 장치가 필요

*/
// ============ Request ============
// : Request Interceptor: Access Token 자동 삽입
// : API 요청 보낼 때 자동으로 Authorization 헤더에 Access Token 추가
privateApi.interceptors.request.use((config) => {
  const { accessToken } = useAuthStore.getState();

  if (accessToken && config.headers) {
    config.headers["Authorization"] = `Bearer ${accessToken}`;
  }
  return config;
});

// Refresh 401(만료) 처리
let isRefreshing = false;
// failQueue: 401로 실패한 요청들을 잠시 보관하는 곳
/*
❗ 이유
Access Token이 만료되었을 때
  → 모든 API 요청이 401로 실패함
  → 하지만 refresh 요청은 딱 1번만 해야 함
  → 나머지 API 요청들은 refresh 완료될 때까지 기다려야 함
*/
let failQueue: Array<{
  resolve: (token: string | null) => void;
  reject: (err: unknown) => void;
}> = [];

// refresh 이후 큐에 쌓인 요청들을 모두 처리하는 함수
// : Refresh 요청이 성공하면 큐에 있던 모든 요청을 다시 실행
// : Refresh 실패하면 큐에 있던 요청 모두 실패 처리
const processQueue = (error: unknown, token: string | null) => {
  failQueue.forEach(process => {
    if (error) process.reject(error);
    else process.resolve(token);
  });
  failQueue = [];
}

// ============ Response(자동 갱신/재발급) ============
// privateApi.interceptors.response.use(A, B) 
// 첫 번째 인자: 성공 응답 처리 함수
// 두 번쨰 인자: 실패 응답 처리 함수
privateApi.interceptors.response.use(
  (res) => res, // 성공한 응답은 그대로 반환

  async (error: AxiosError) => {
    const original = error.config as any; // 에러가 발생한 원래 요청 정보

    // 401(만료) + 재시도 안 된 요청만 처리
    if (error.response?.status === 401 && !original._retry) { // _retry: 실패한 요청에 붙이는 프로퍼티
      const { clearAuth, setAccessToken } = useAuthStore.getState();
      // setAccessToken: 새 토큰 저장
      // cleatAuth: refresh 마저 실패하면 완전 로그아웃

      // refresh 중이면 큐에 넣고 기다리기
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failQueue.push({
            resolve: (newToken) => {
              if (newToken) original.headers.Authorization = `Bearer ${newToken}`;
              resolve(privateApi(original)); // 새 토큰으로 다시 요청
            },
            reject, // refresh 실패하면 reject 되어 전체 로그아웃 처리
          });
        });
      }

      // refresh 요청을 처음 시작하는 경우
      original._retry = true;
      isRefreshing = true;

      // 진짜 refresh Token 요청 보내기
      try {
        const { data } = await publicApi.post("/api/v1/auth/refresh");

        const newAccessToken = (data as any).data.accessToken;

        // Zustand에 갱신 저장
        setAccessToken(newAccessToken);

        // 원래 요청 재시도(원래 요청을 새 토큰으로 다시 수행)
        original.headers["Authorization"] = `Bearer ${newAccessToken}`; 
        return privateApi(original);

        // refresh 실패시 -> 전체 큐 reject & 로그아웃
      } catch (refreshError) {
        processQueue(refreshError, null);
        clearAuth(); // 로그인 정보 제거
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);


/*
API 요청 → 401?
       ↓ NO → 정상 요청

       YES
       ↓
isRefreshing === false ?
       ↓
   YES → refresh 요청 시작
          isRefreshing = true
          실패한 요청은 큐에 저장
          refresh 성공 → processQueue(새 토큰)
          refresh 실패 → processQueue(에러)
          isRefreshing = false

       ↓
   NO → (이미 refresh 중이므로)
         실패한 요청을 Queue에 넣고
         refresh 끝날 때까지 기다림


*/