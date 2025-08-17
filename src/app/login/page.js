"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const { login } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage("");

    try {
      const result = await login({ email, password });

      if (result.success) {
        setMessage("Login successful! Redirecting...");

        // Force page reload to update auth state
        setTimeout(() => {
          const user = JSON.parse(localStorage.getItem("user") || "{}");
          console.log("User from localStorage:", user);

          if (user && user.is_admin === true) {
            console.log("Redirecting to admin dashboard");
            window.location.href = "/admin/dashboard";
          } else {
            console.log("Redirecting to books page");
            window.location.href = "/books";
          }
        }, 1500);
      } else {
        setMessage(result.message || "Invalid credentials");
      }
    } catch (error) {
      console.error("Login error:", error);
      setMessage("Error: " + (error.message || "Failed to connect to server"));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        fontFamily: "Arial",
      }}
    >
      <div
        style={{
          padding: "40px",
          borderRadius: "10px",
          background: "#fff",
          boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
          width: "350px",
        }}
      >
        <h1 style={{ color: "#ff5722", textAlign: "center" }}>Welcome Back</h1>
        <form
          style={{ display: "flex", flexDirection: "column", gap: "15px" }}
          onSubmit={handleLogin}
        >
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{
              padding: "10px",
              borderRadius: "5px",
              border: "1px solid #ccc",
            }}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={{
              padding: "10px",
              borderRadius: "5px",
              border: "1px solid #ccc",
            }}
          />
          <button
            type="submit"
            disabled={isLoading}
            style={{
              background: isLoading ? "#ff8a65" : "#ff5722",
              color: "#fff",
              border: "none",
              padding: "10px",
              borderRadius: "5px",
              cursor: isLoading ? "default" : "pointer",
              display: "flex",
              justifyContent: "center",
            }}
          >
            {isLoading ? "Logging in..." : "Login"}
          </button>
        </form>
        {message && (
          <p
            style={{
              textAlign: "center",
              marginTop: "15px",
              color: message.includes("successful") ? "green" : "red",
            }}
          >
            {message}
          </p>
        )}
        <p style={{ textAlign: "center", marginTop: "15px" }}>
          Don't have an account?{" "}
          <a href="/signup" style={{ color: "#ff5722" }}>
            Sign up
          </a>
        </p>
      </div>
    </div>
  );
}
