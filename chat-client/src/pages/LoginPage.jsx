import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
  const [username, setusername] = useState("user1@gmail.com");
  const [password, setPassword] = useState("user1");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    const res = await fetch("http://localhost:8282/api/auth/login", {
      method: "POST",
      credentials: "include", // ✅ 쿠키 저장
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    });

    if (res.ok) {
      console.log("✅ 로그인 성공");
      navigate("/chat");
    } else {
      alert("❌ 로그인 실패");
    }
  };

  return (
    <div style={{ padding: "2rem", maxWidth: "400px", margin: "auto" }}>
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <input
          type="username"
          placeholder="이메일"
          value={username}
          autoComplete="username"
          onChange={(e) => setusername(e.target.value)}
          style={{ padding: "0.5rem", width: "100%", marginBottom: "1rem" }}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          autoComplete="current-password"
          onChange={(e) => setPassword(e.target.value)}
          style={{ padding: "0.5rem", width: "100%", marginBottom: "1rem" }}
        />
        <button type="submit" style={{ padding: "0.5rem", width: "100%" }}>
          로그인
        </button>
      </form>
    </div>
  );
};

export default LoginPage;
