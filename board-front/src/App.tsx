import { Link, Route, Routes } from "react-router-dom";
import { useAuthStore } from "./stores/auth.store";
import { useEffect } from "react";
import { userApi } from "./apis/user/user.api";
import Layout from "./components/layout/Layout";
import { GlobalStyle } from "./styles/GlobalStyle";
import AuthRouter from "./pages/auth/AuthRouter";
import MainRouter from "./pages/MainRouter";

export default function App() {
  const { isInitialized, accessToken, user, setUser } = useAuthStore();

  useEffect(() => {
    console.log(isInitialized);
    if (!isInitialized) return;
    if (!accessToken) return;
    if (user) return;

    (async () => {
      if (accessToken && !user) {
        const me = await userApi.me();
        if (me.success && me.data) {
          setUser(me.data);
        }
      }
    })();

  }, [isInitialized, accessToken]);

  if (!isInitialized) {
    return <div>로딩중</div>
  }

  const isLoggedIn = Boolean(accessToken && user);

  return (
    <>
      <GlobalStyle />
      {isLoggedIn ? (
        <Layout>
          <MainRouter />
        </Layout>
      ) : (
        <AuthRouter />
      )}
    </>
  );
}
