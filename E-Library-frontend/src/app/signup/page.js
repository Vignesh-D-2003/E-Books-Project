"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import authService from "@/services/authService";
import { Eye, EyeOff, Mail, Lock, User, BookOpen, ArrowRight, CheckCircle, AlertCircle, Shield } from "lucide-react";
import Link from "next/link";

export default function Signup() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isSuccess, setIsSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isFocused, setIsFocused] = useState({ 
    username: false, 
    email: false, 
    password: false, 
    confirmPassword: false 
  });
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

    if (password.length < 6) {
      setMessage("Password must be at least 6 characters long");
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

  const isFormValid = username.trim() && email.trim() && password.trim() && confirmPassword.trim() && password === confirmPassword && password.length >= 6;

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 via-pink-50 to-purple-50 p-4">
      {/* Background decoration */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-br from-orange-200 to-pink-200 rounded-full opacity-20 blur-3xl"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-gradient-to-tr from-purple-200 to-pink-200 rounded-full opacity-20 blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-60 h-60 bg-gradient-to-r from-pink-200 to-purple-200 rounded-full opacity-10 blur-2xl"></div>
      </div>

      <div className="relative w-full max-w-md">
        {/* Logo and Welcome Section */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-orange-500 to-pink-500 rounded-2xl mb-4 shadow-lg">
            <BookOpen className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold bg-gradient-to-r from-orange-600 to-pink-600 bg-clip-text text-transparent">
            Join Our Library
          </h1>
          <p className="text-gray-600 mt-2">Create your account and start reading today</p>
        </div>

        {/* Signup Form Card */}
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-8">
          <form onSubmit={handleSignup} className="space-y-6">
            {/* Username Input */}
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-700 flex items-center gap-2">
                <User className="w-4 h-4" />
                Full Name
              </label>
              <div className={`relative transition-all duration-300 ${
                isFocused.username ? 'ring-2 ring-orange-500 ring-opacity-50' : ''
              }`}>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  onFocus={() => setIsFocused({ ...isFocused, username: true })}
                  onBlur={() => setIsFocused({ ...isFocused, username: false })}
                  required
                  className="w-full px-4 py-3 pl-12 border border-gray-200 rounded-xl focus:outline-none focus:border-orange-500 transition-all duration-300 bg-white/50 backdrop-blur-sm"
                  placeholder="Enter your full name"
                />
                <User className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
              </div>
            </div>

            {/* Email Input */}
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-700 flex items-center gap-2">
                <Mail className="w-4 h-4" />
                Email Address
              </label>
              <div className={`relative transition-all duration-300 ${
                isFocused.email ? 'ring-2 ring-orange-500 ring-opacity-50' : ''
              }`}>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  onFocus={() => setIsFocused({ ...isFocused, email: true })}
                  onBlur={() => setIsFocused({ ...isFocused, email: false })}
                  required
                  className="w-full px-4 py-3 pl-12 border border-gray-200 rounded-xl focus:outline-none focus:border-orange-500 transition-all duration-300 bg-white/50 backdrop-blur-sm"
                  placeholder="Enter your email"
                />
                <Mail className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
              </div>
            </div>

            {/* Password Input */}
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-700 flex items-center gap-2">
                <Lock className="w-4 h-4" />
                Password
              </label>
              <div className={`relative transition-all duration-300 ${
                isFocused.password ? 'ring-2 ring-orange-500 ring-opacity-50' : ''
              }`}>
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  onFocus={() => setIsFocused({ ...isFocused, password: true })}
                  onBlur={() => setIsFocused({ ...isFocused, password: false })}
                  required
                  className="w-full px-4 py-3 pl-12 pr-12 border border-gray-200 rounded-xl focus:outline-none focus:border-orange-500 transition-all duration-300 bg-white/50 backdrop-blur-sm"
                  placeholder="Create a password"
                />
                <Lock className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              {password && password.length < 6 && (
                <p className="text-xs text-red-500">Password must be at least 6 characters</p>
              )}
            </div>

            {/* Confirm Password Input */}
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-700 flex items-center gap-2">
                <Shield className="w-4 h-4" />
                Confirm Password
              </label>
              <div className={`relative transition-all duration-300 ${
                isFocused.confirmPassword ? 'ring-2 ring-orange-500 ring-opacity-50' : ''
              }`}>
                <input
                  type={showConfirmPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  onFocus={() => setIsFocused({ ...isFocused, confirmPassword: true })}
                  onBlur={() => setIsFocused({ ...isFocused, confirmPassword: false })}
                  required
                  className="w-full px-4 py-3 pl-12 pr-12 border border-gray-200 rounded-xl focus:outline-none focus:border-orange-500 transition-all duration-300 bg-white/50 backdrop-blur-sm"
                  placeholder="Confirm your password"
                />
                <Shield className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                >
                  {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              {confirmPassword && password !== confirmPassword && (
                <p className="text-xs text-red-500">Passwords do not match</p>
              )}
            </div>

            {/* Message Display */}
            {message && (
              <div className={`flex items-center gap-3 p-4 rounded-xl ${
                isSuccess 
                  ? "bg-green-50 border border-green-200 text-green-800" 
                  : "bg-red-50 border border-red-200 text-red-800"
              }`}>
                {isSuccess ? (
                  <CheckCircle className="w-5 h-5 text-green-600" />
                ) : (
                  <AlertCircle className="w-5 h-5 text-red-600" />
                )}
                <span className="text-sm font-medium">{message}</span>
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading || !isFormValid}
              className={`w-full py-3 px-6 rounded-xl font-semibold text-white transition-all duration-300 flex items-center justify-center gap-2 ${
                isLoading || !isFormValid
                  ? "bg-gray-300 cursor-not-allowed"
                  : "bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 transform hover:scale-[1.02] shadow-lg hover:shadow-xl"
              }`}
            >
              {isLoading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  Creating account...
                </>
              ) : (
                <>
                  Create Account
                  <ArrowRight className="w-5 h-5" />
                </>
              )}
            </button>
          </form>

          {/* Divider */}
          <div className="my-6 flex items-center">
            <div className="flex-1 border-t border-gray-200"></div>
            <span className="px-4 text-sm text-gray-500">or</span>
            <div className="flex-1 border-t border-gray-200"></div>
          </div>

          {/* Login Link */}
          <div className="text-center">
            <p className="text-gray-600">
              Already have an account?{" "}
              <Link 
                href="/login" 
                className="text-orange-600 hover:text-orange-700 font-semibold transition-colors duration-200 hover:underline"
              >
                Sign in here
              </Link>
            </p>
          </div>
        </div>

        {/* Footer */}
        <div className="text-center mt-8">
          <p className="text-sm text-gray-500">
            By creating an account, you agree to our{" "}
            <a href="#" className="text-orange-600 hover:underline">Terms of Service</a>
            {" "}and{" "}
            <a href="#" className="text-orange-600 hover:underline">Privacy Policy</a>
          </p>
        </div>
      </div>
    </div>
  );
}
