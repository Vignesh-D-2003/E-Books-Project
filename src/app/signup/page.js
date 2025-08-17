"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import authService from "@/services/authService";

export default function Signup() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isSuccess, setIsSuccess] = useState(false); // Track success/failure
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const handleSignup = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    if (password !== confirmPassword) {
      setMessage("Passwords do not match");
      setIsSuccess(false);
      setIsLoading(false);
      return;
    }

    try {
      const result = await authService.register({ username, email, password });

      if (result.success) {
        setMessage("Signup successful! Redirecting to login...");
        setIsSuccess(true);

        setTimeout(() => {
          router.push("/login");
        }, 2000);
      } else {
        setMessage(result.message || "Registration failed");
        setIsSuccess(false);
      }
    } catch (error) {
      console.error("Signup error:", error);
      setMessage("Error connecting to server");
      setIsSuccess(false);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <style>{`input::placeholder { color: lightgray; }`}</style>

      <div
        style={{
          fontFamily: "Arial, sans-serif",
          display: "flex",
          minHeight: "100vh",
        }}
      >
        <div
          style={{
            flex: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            background: "#fff8f0",
          }}
        >
          <div
            style={{
              background: "#fff",
              padding: "40px",
              borderRadius: "10px",
              boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
              width: "350px",
            }}
          >
            <h1
              style={{
                fontSize: "24px",
                marginBottom: "10px",
                color: "#ff5722",
                textAlign: "center",
              }}
            >
              Create Account
            </h1>
            <p
              style={{
                textAlign: "center",
                color: "#666",
                marginBottom: "20px",
              }}
            >
              Join and start reading today
            </p>

            <form
              style={{ display: "flex", flexDirection: "column", gap: "15px" }}
              onSubmit={handleSignup}
            >
              <input
                type="text"
                placeholder="Full Name"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                style={{
                  padding: "10px",
                  borderRadius: "5px",
                  border: "1px solid #ccc",
                }}
              />
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
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
                style={{
                  padding: "10px",
                  borderRadius: "5px",
                  border: "1px solid #ccc",
                }}
              />
              <input
                type="password"
                placeholder="Confirm Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
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
                {isLoading ? "Signing up..." : "Sign Up"}
              </button>
            </form>

            {message && (
              <p
                style={{
                  textAlign: "center",
                  marginTop: "10px",
                  color: isSuccess ? "green" : "red",
                }}
              >
                {message}
              </p>
            )}

            <p
              style={{
                textAlign: "center",
                marginTop: "15px",
                fontSize: "14px",
              }}
            >
              Already have an account?{" "}
              <a href="/login" style={{ color: "#ff5722" }}>
                Login
              </a>
            </p>
          </div>
        </div>

        <div
          style={{
            flex: 1,
            display: "none",
            background: "#eee",
            overflow: "hidden",
          }}
          className="image-section"
        >
          <img
            src="https://picsum.photos/800/901"
            alt="Reading"
            style={{ width: "100%", height: "100%", objectFit: "cover" }}
          />
        </div>
      </div>
    </>
  );
}
