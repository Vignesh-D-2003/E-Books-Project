"use client";

import { useState } from "react";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      const text = await response.text();
      setMessage(text);
    } catch (error) {
      console.error("Login error:", error);
      setMessage("Error connecting to server");
    }
  };

  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "100vh", fontFamily: "Arial" }}>
      <div style={{ padding: "40px", borderRadius: "10px", background: "#fff", boxShadow: "0 2px 8px rgba(0,0,0,0.1)", width: "350px" }}>
        <h1 style={{ color: "#ff5722", textAlign: "center" }}>Welcome Back</h1>
        <form style={{ display: "flex", flexDirection: "column", gap: "15px" }} onSubmit={handleLogin}>
          <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
          <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
          <button type="submit" style={{ background: "#ff5722", color: "#fff", border: "none", padding: "10px", borderRadius: "5px", cursor: "pointer" }}>Login</button>
        </form>
        {message && <p style={{ textAlign: "center", marginTop: "15px", color: message.includes("successful") ? "green" : "red" }}>{message}</p>}
        <p style={{ textAlign: "center", marginTop: "15px" }}>Don't have an account? <a href="/signup" style={{ color: "#ff5722" }}>Sign up</a></p>
      </div>
    </div>
  );
}
