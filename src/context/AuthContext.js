"use client";

import { createContext, useContext, useState, useEffect } from "react";
import authService from "@/services/authService";

// Create the auth context
const AuthContext = createContext({
  isAuthenticated: false,
  isAdmin: false,
  loading: true,
  checkAuth: () => {},
  login: async () => {},
  logout: () => {},
});

// Create a provider component
export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);

  // Check authentication status
  const checkAuth = () => {
    const authenticated = authService.isAuthenticated();
    const admin = authService.isAdmin();

    console.log("AuthContext check:", { authenticated, admin });

    setIsAuthenticated(authenticated);
    setIsAdmin(admin);
    setLoading(false);
  };

  // Login function
  const login = async (credentials) => {
    try {
      // Use fetch directly for better control
      const response = await fetch("http://localhost:8080/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Login failed");
      }

      const data = await response.json();

      // Store authentication data
      if (data.jwtToken) {
        localStorage.setItem("token", data.jwtToken);
        if (data.user) {
          localStorage.setItem("user", JSON.stringify(data.user));
        }

        // Update state
        checkAuth();

        return { success: true, data };
      } else {
        throw new Error("Invalid response from server");
      }
    } catch (error) {
      console.error("Login error:", error);
      return { success: false, message: error.message };
    }
  };

  // Logout function
  const logout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setIsAdmin(false);
  };

  // Check auth on mount and set up listeners
  useEffect(() => {
    checkAuth();

    // Listen for storage events (for multi-tab support)
    const handleStorageChange = () => {
      checkAuth();
    };

    window.addEventListener("storage", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  const value = {
    isAuthenticated,
    isAdmin,
    loading,
    checkAuth,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Custom hook to use the auth context
export const useAuth = () => useContext(AuthContext);
