import { QueryClient } from "@tanstack/react-query";

// 리액트 쿼리 전체 공통 설정
// QueryClient를 만들면서 모든 useQuery 의 기본 옵션 설정
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 30,
      refetchOnWindowFocus: false, // 창으로 돌아왔을 때 자동 refetch 할지 
      retry: 1 // 실패시 몇 번 재시도 할 지
    }
  }
})