// React Query
// : 서버로부터 가져오는 데이터(fetch)를 자동으로 캐싱(저장/기록)해주고 로딩/에러 상태도 관리
// - 새로고침할 필요없이 자동으로 최신화해주는 데이터 관리 라이브러리

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

// react-query 설치 방법
// npm install @tanstack/react-query

// zustand 와의 차이점
// : React Query = 서버 상태(Server State) 관리 라이브러리
// : Zustand = 클라이언트 상태 관리 라이브러리(전역 상태 관리) +) 서버와 상관없이 앱에서 사용하는 임의의 상태를 관리하는 도구
//              ex) 로그인 여부, 모달 열림/닫힘, 현재 선택된 앱, 폼 임시 값, UI 모드(다크/라이트), 장바구니 상태 등

/*
React Query 사용 목적
Ex) 서버에서 데이터를 가져올 때 하는 일
1) useEffect + fetch/axios 호출
2) useState로 data, loading, error 관리
3) 새로고침/탭 포커스 시 리페치 로직 직접 작성 및 관리
4) 캐싱하고 싶으면 직접 전역에 저장
5) 페이지 이동 후 다시 돌아올 경우 fetch 부터 재시작
  >> 1~5번까지 다 해주는것이 React Query 의 역할

React Query 의 서버 상태
1) 진짜 소스는 서버(DB)에 있고, 프론트는 복사본을 가짐
2) 여러 사용자, 여러 클라이언트에서 동시 수정 가능
3) 프론트에서 실제 데이터를 100% 통제 불각능
4) 항상 서버에서 다시  fetch 해서 맞춰줘야함

*/
// React Query 의 핵심 개념
// 1. Query(useQuery) - 읽기 (GET)

const fetchTodos = () => {}

const { data, isLoading, isError, isSuccess, status, error } = useQuery({
  queryKey: ["todos"], // 해당 데이터를 가리키는 이름표
  queryFn: fetchTodos,  // 실제로 서버에서 todos를 가져오는 함수 - Promise 반환
  staleTime: 1000 * 60 // 데이터가 신선하다고 보는 시간(옵션)
  // : 1분동안은 새로고침/포커스 이동 시에도 refetch 안함
});

console.log(data);
// 리턴값
// - data: 서버에서 가져온 실제 데이터
// - isLoading, isError, isSuccess, status: 상태 플래그
// - error: 에러 정보

// 2. Mutation(useMutation) - 쓰기 (POST, PUT, DELETE)
const createTodo = () => {
  return fetch("/todo");
}
const queryClient = useQueryClient();

const { mutate, isPending } = useMutation({
  mutationFn: createTodo, // POST/PUT/DELETE 등
  onSuccess: () => {
    // 성공 후 캐시된 todos 목록을 다시 불러오고 싶을 때
    queryClient.invalidateQueries({
      queryKey: ["todos"],
    })
  }
});

console.log(mutate);