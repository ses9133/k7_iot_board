/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useNavigate, Link } from "react-router-dom";

import { authApi } from "@/apis/auth/auth.api";
import { getErrorMessage } from "@/utils/error";
import type { SignupRequest } from "@/types/auth/auth.dto";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState<SignupRequest>({
    username: "",
    password: "",
    confirmPassword: "",
    email: "",
    nickname: "",
    gender: "N",
    provider: "LOCAL"
  });

  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  /** 📌 Mutation */
  const signupMutation = useMutation({
    mutationFn: () => authApi.signupApi(form),

    onSuccess: () => {
      alert("회원가입이 완료되었습니다. 로그인해주세요.");
      navigate("/login");
    },

    onError: (err) => {
      setErrorMessage(getErrorMessage(err));
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    setErrorMessage(null);

    if (form.password !== form.confirmPassword) {
      setErrorMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return;
    }

    signupMutation.mutate();
  };

  return (
    <div css={container}>
      <h1 css={title}>회원가입</h1>

      <form css={formStyle} onSubmit={handleSubmit}>
        {/* 아이디 */}
        <div css={inputGroup}>
          <label>아이디 *</label>
          <input 
            name="username" 
            value={form.username} 
            onChange={handleChange} 
            required 
          />
        </div>

        {/* 비밀번호 */}
        <div css={inputGroup}>
          <label>비밀번호 *</label>
          <input 
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            required
          />
        </div>

        {/* 비밀번호 확인 */}
        <div css={inputGroup}>
          <label>비밀번호 확인 *</label>
          <input 
            type="password"
            name="confirmPassword"
            value={form.confirmPassword}
            onChange={handleChange}
            required
          />
        </div>

        {/* 이메일 */}
        <div css={inputGroup}>
          <label>이메일 *</label>
          <input 
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </div>

        {/* 닉네임 */}
        <div css={inputGroup}>
          <label>닉네임 *</label>
          <input 
            name="nickname"
            value={form.nickname}
            onChange={handleChange}
            required
          />
        </div>

        {/* 성별 */}
        <div css={inputGroup}>
          <label>성별</label>
          <select name="gender" value={form.gender} onChange={handleChange}>
            <option value="NONE">선택 안함</option>
            <option value="MALE">남성</option>
            <option value="FEMALE">여성</option>
          </select>
        </div>

        {/* 에러 메시지 */}
        {errorMessage && <p css={errorText}>{errorMessage}</p>}

        <button css={buttonStyle} disabled={signupMutation.isPending}>
          {signupMutation.isPending ? "처리 중..." : "회원가입"}
        </button>
      </form>

      <div css={linkBox}>
        <Link to="/login">이미 계정이 있으신가요? 로그인</Link>
      </div>
    </div>
  );
}

/* ---------------------- CSS ---------------------- */
const container = css`
  max-width: 420px;
  margin: 60px auto;
  padding: 20px;
`;

const title = css`
  text-align: center;
  margin-bottom: 24px;
`;

const formStyle = css`
  display: flex;
  flex-direction: column;
  gap: 18px;
`;

const inputGroup = css`
  display: flex;
  flex-direction: column;
  gap: 4px;

  input,
  select {
    padding: 10px;
    border-radius: 6px;
    border: 1px solid #bbb;
  }
`;

const errorText = css`
  color: red;
  font-size: 0.9rem;
`;

const buttonStyle = css`
  padding: 12px;
  background: #1b73e8;
  color: white;
  border: none;
  border-radius: 6px;
`;

const linkBox = css`
  margin-top: 20px;
  text-align: center;

  a {
    color: #1b73e8;
  }
`;