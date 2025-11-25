// src/store/auth.store.ts
import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { MeResponse } from "@/types/user/user.dto";

// 상태 관리 데이터
type AuthState = {
  accessToken: string | null; // 로그인하면 서버에서 받은 JWT 토큰
  user: MeResponse | null;  // 로그인한 사용자 정보
  isInitialized: boolean;     // 스토어가 localStorage에서 복원 작업까지 끝냈는지 여부
}

// 이 스토어가 제공하는 함수들
type AuthActions = {
  // 엑세스 토큰 설정 함수 (cf. persist: 로컬 스토리지 저장 여부 결정)
  setAccessToken: (token: string | null) => void; // accessToken 값을 바꾸는 함수 
  setUser: (user: MeResponse | null) => void; // 유저 정보를 바꾸는 함수
  clearAuth: () => void; // 로그아웃(토큰 + 유저 정보 초기화)

  // “localStorage에서 복원이 끝났다”는 걸 표시하는 함수
  hydrateFromStorage: () => void; // 
}
// Zustand 의 setter 함수는 반환값이 필요없기 떄문에 다 void 반환임

// 로컬스토리지에 사용할 엑세스 토큰 키 이름 상수
// 브라우저 개발자도구 → Application → Local Storage 에 가면 auth-storage 라는 키로 JSON 이 저장
const AUTH_STORAGE = "auth-storage"; 

// 전역 인증 스토어를 만드는 코드
// - 라이브러리: zustand, 미들웨어: persist
export const useAuthStore = create(
  persist<AuthState & AuthActions>( // 이 스토어안에 들어갈 상태 + 액션의 타입을 합친것 (상태: AuthState, 액션: AuthActions)
    (set, get) => ({
      accessToken: null,
      user: null,
      isInitialized: false,

      // zustand 의 set()은 객체를 받아서 기존 상태에 병합하는 함수이기에 꼭 객체 형태로 넣어야함
      // set(partialState: Partial<YourState>)
      setAccessToken: (token) => set({ accessToken: token }),
      setUser: (user) => set({ user }),
      clearAuth: () => set({ accessToken: null, user: null }),

      // persist 초기화 완료 여부 플래그 설정
      // : “이제 복원 끝났어!”라고 표시하는 용도
      hydrateFromStorage: () => {
        set({ isInitialized: true });
      },
    }),

    //! persist 옵션
    // : 모든 localStorage 작업을 자동 처리
    // - 키 이름: AUTH_STORAGE (auth-storage)
    {
      name: AUTH_STORAGE, // 로컬 스토리지 키 , 결과적으로 localStorage["auth-storage"] = {...} 이런 형태로 저장됨
      onRehydrateStorage: () => (state) => {
        // persist가 localStorage 값 복원 완료 후 실행
        if (state) {
          state.isInitialized = true;
          /*
            Zustand persist 흐름:
            - 앱 처음 로드
            - create(persist(...)) 실행
            - persist가 localStorage에서 "auth-storage" 값을 읽어옴
            - 그 값을 Zustand 스토어에 넣음
            - 그 작업이 끝난 뒤에 onRehydrateStorage 콜백 실행
            - 그래서 여기서:
            state.isInitialized = true;
            → “아, 이제 복원 끝났구나” 라고 직접 표시해주는 것
            💡 여기서는 set() 대신 state.isInitialized = true 이렇게 직접 수정하고 있음.
            onRehydrateStorage는 rehydrate 직후 호출되면서 state 객체를 직접 건드릴 수 있는 훅임
          */
        }
      }
    }
  )
);

/*
  ✅ 1. 복원(Rehydrate)
  - Zustand 의 persist 미들웨어는 로그인 상태를 localStorage 에 저장함
  예)
    localStorage["auth-storage"] = {
      "state": {
        "accessToken": "abc123",
        "user": { ... }
      }
    }
  ✔ 앱을 새로고침하면?
  - Zustand 스토어는 일단 기본값으로 시작함(accessToken: null, user: null...)
  - 그 다음에, persist 가 localStorage에서 값을 읽어서 stat를 덮어씌움
  ==> 이 과정이 hydrate(복원)

  ❗이때 이 “복원”은 비동기로 일어남
  즉,
  1️⃣ 앱 시작 → create(persist(...)) 실행
  2️⃣ Zustand는 우선 다음 초기 상태로 시작:
    accessToken: null
    user: null
    isInitialized: false
  3️⃣ 이후 조금 있다가(비동기) - localStorage에서 값을 읽고 다음 상태로 덮어씀:
    accessToken: "ABC123"
    user: { ... }
    isInitialized: true

  ❗이 짧은 틈 때문에 버그가 생김
  - 앱이 처음 렌더링될 떄 accessToken == null, user == null 인 상태가 잠시라도 존재하게 되는데 이때 UI 는 "이사람 로그인 안했네? -> 로그인 페이지로 이동시켜야지" 라고 판단함
  - 근데 실제로는 localStorage 안에 토큰이 있음
  - 단지 아직 복원되기 전이라 null 상태인 것 뿐!!
  => 그래서 등장한 값이 isInitialized
    >> isInitailzied는 초기 null 값 때문에 발생하는 버그를 막기 위한 박패

*/