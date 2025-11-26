import React from "react";
import { Link, Route, Routes } from "react-router-dom";
import LoginPage from "./LoginPage";
import RegisterPage from "./RegisterPage";
import OAuth2CallbackPage from "./OAuth2CallbackPage";

function AuthRouter() {
  return (
    <div>
      <h2>로그인이 필요합니다.</h2>
      <Link to="/login">로그인 페이지로 이동</Link>

      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/oauth2/callback/*" element={<OAuth2CallbackPage />} />
        {/* 기본 경로 */}
        <Route path="*" element={<LoginPage />} />
      </Routes>
    </div>
  );
}

export default AuthRouter;