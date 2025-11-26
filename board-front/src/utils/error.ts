export const getErrorMessage = (err: any, fallback = '오류가 발생했습니다.') => {
  const backendMessge = err?.response.data?.message;
  if(backendMessge) return backendMessge;

  const axiosMessage = err?.message;
  if(axiosMessage) return axiosMessage;

  return fallback;
}