"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { useState, useEffect, useRef } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { 
  BookOpen, 
  User, 
  Settings, 
  LogOut, 
  Menu, 
  X, 
  Home,
  Library,
  ChevronDown,
  Sparkles,
  Crown
} from "lucide-react";

export function Header() {
  const [mounted, setMounted] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  
  const router = useRouter();
  const pathname = usePathname();
  const { isAuthenticated, isAdmin, logout, loading } = useAuth();
  
  const mobileMenuRef = useRef(null);
  const userMenuRef = useRef(null);

  useEffect(() => {
    setMounted(true);
  }, []);

  // Close menus when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (mobileMenuRef.current && !mobileMenuRef.current.contains(event.target)) {
        setIsMobileMenuOpen(false);
      }
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setIsUserMenuOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Close mobile menu when route changes
  useEffect(() => {
    setIsMobileMenuOpen(false);
    setIsUserMenuOpen(false);
  }, [pathname]);

  // Handle logout
  const handleLogout = () => {
    logout();
    setIsUserMenuOpen(false);
    router.push("/login");
  };

  // Get user info
  const getUserInfo = () => {
    try {
      const userStr = localStorage.getItem("user");
      return userStr ? JSON.parse(userStr) : null;
    } catch (error) {
      console.error("Error parsing user info:", error);
      return null;
    }
  };

  const user = getUserInfo();

  if (!mounted || loading) {
    return (
      <header className="bg-white/80 backdrop-blur-sm border-b border-white/20 shadow-sm sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <div className="w-32 h-8 bg-gray-200 rounded animate-pulse"></div>
            <div className="flex space-x-2">
              <div className="w-16 h-8 bg-gray-200 rounded animate-pulse"></div>
              <div className="w-16 h-8 bg-gray-200 rounded animate-pulse"></div>
            </div>
          </div>
        </div>
      </header>
    );
  }

  return (
    <header className="bg-white/80 backdrop-blur-sm border-b border-white/20 shadow-sm sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4">
        <div className="flex justify-between items-center">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-3 group">
            <div className="w-10 h-10 bg-gradient-to-br from-orange-500 to-pink-500 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
              <BookOpen className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold bg-gradient-to-r from-orange-600 to-pink-600 bg-clip-text text-transparent">
                E-Library
              </h1>
              <p className="text-xs text-gray-500 hidden sm:block">Digital Library</p>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden lg:flex items-center space-x-1">
            {/* Home */}
            <Link href="/">
              <Button 
                variant={pathname === "/" ? "default" : "ghost"}
                className={`flex items-center gap-2 ${pathname === "/" ? "bg-gradient-to-r from-orange-500 to-pink-500 text-white" : "hover:bg-orange-50 hover:text-orange-600"}`}
              >
                <Home className="w-4 h-4" />
                Home
              </Button>
            </Link>

            {/* Books - Only show if authenticated */}
            {isAuthenticated && (
              <Link href="/books">
                <Button 
                  variant={pathname === "/books" ? "default" : "ghost"}
                  className={`flex items-center gap-2 ${pathname === "/books" ? "bg-gradient-to-r from-orange-500 to-pink-500 text-white" : "hover:bg-orange-50 hover:text-orange-600"}`}
                >
                  <Library className="w-4 h-4" />
                  Books
                </Button>
              </Link>
            )}

            {/* Admin Dashboard - Only show if admin */}
            {isAuthenticated && isAdmin && (
              <Link href="/admin/dashboard">
                <Button 
                  variant={pathname.startsWith("/admin") ? "default" : "ghost"}
                  className={`flex items-center gap-2 ${pathname.startsWith("/admin") ? "bg-gradient-to-r from-purple-500 to-pink-500 text-white" : "hover:bg-purple-50 hover:text-purple-600"}`}
                >
                  <Crown className="w-4 h-4" />
                  Admin
                </Button>
              </Link>
            )}
          </nav>

          {/* Right Side Actions */}
          <div className="flex items-center space-x-2">
            {/* User Menu / Auth Buttons */}
            {isAuthenticated ? (
              <div className="relative" ref={userMenuRef}>
                <Button
                  variant="ghost"
                  onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
                  className="flex items-center gap-2 hover:bg-orange-50 hover:text-orange-600"
                >
                  <div className="w-8 h-8 bg-gradient-to-br from-orange-500 to-pink-500 rounded-full flex items-center justify-center">
                    <User className="w-4 h-4 text-white" />
                  </div>
                  <span className="hidden sm:block text-sm font-medium">
                    {user?.username || "User"}
                  </span>
                  <ChevronDown className="w-3 h-3" />
                </Button>

                {/* User Dropdown Menu */}
                {isUserMenuOpen && (
                  <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-200 py-2 z-50">
                    {/* User Info */}
                    <div className="px-4 py-3 border-b border-gray-100">
                      <p className="text-sm font-medium text-gray-900">
                        {user?.username || "User"}
                      </p>
                      <p className="text-xs text-gray-500">
                        {user?.email || "user@example.com"}
                      </p>
                      {isAdmin && (
                        <span className="inline-flex items-center gap-1 mt-1 px-2 py-1 bg-purple-100 text-purple-700 text-xs rounded-full">
                          <Crown className="w-3 h-3" />
                          Admin
                        </span>
                      )}
                    </div>

                    {/* Menu Items */}
                    <div className="py-1">
                      <Link href="/profile" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                        <div className="flex items-center gap-2">
                          <User className="w-4 h-4" />
                          Profile
                        </div>
                      </Link>
                      
                      {isAdmin && (
                        <Link href="/admin/settings" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                          <div className="flex items-center gap-2">
                            <Settings className="w-4 h-4" />
                            Settings
                          </div>
                        </Link>
                      )}
                      
                      <button
                        onClick={handleLogout}
                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                      >
                        <div className="flex items-center gap-2">
                          <LogOut className="w-4 h-4" />
                          Logout
                        </div>
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              /* Auth Buttons for non-authenticated users */
              <div className="flex items-center space-x-2">
                <Link href="/login">
                  <Button 
                    variant="ghost"
                    className="hover:bg-orange-50 hover:text-orange-600"
                  >
                    Sign In
                  </Button>
                </Link>
                <Link href="/signup">
                  <Button 
                    className="bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white"
                  >
                    <Sparkles className="w-4 h-4 mr-1" />
                    Get Started
                  </Button>
                </Link>
              </div>
            )}

            {/* Mobile Menu Button */}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="lg:hidden hover:bg-orange-50 hover:text-orange-600"
            >
              {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </Button>
          </div>
        </div>

        {/* Mobile Menu */}
        {isMobileMenuOpen && (
          <div className="lg:hidden mt-4 pb-4 border-t border-gray-200" ref={mobileMenuRef}>
            <div className="pt-4 space-y-2">
              <Link href="/">
                <Button 
                  variant={pathname === "/" ? "default" : "ghost"}
                  className={`w-full justify-start ${pathname === "/" ? "bg-gradient-to-r from-orange-500 to-pink-500 text-white" : "hover:bg-orange-50 hover:text-orange-600"}`}
                >
                  <Home className="w-4 h-4 mr-2" />
                  Home
                </Button>
              </Link>

              {isAuthenticated && (
                <Link href="/books">
                  <Button 
                    variant={pathname === "/books" ? "default" : "ghost"}
                    className={`w-full justify-start ${pathname === "/books" ? "bg-gradient-to-r from-orange-500 to-pink-500 text-white" : "hover:bg-orange-50 hover:text-orange-600"}`}
                  >
                    <Library className="w-4 h-4 mr-2" />
                    Books
                  </Button>
                </Link>
              )}

              {isAuthenticated && isAdmin && (
                <Link href="/admin/dashboard">
                  <Button 
                    variant={pathname.startsWith("/admin") ? "default" : "ghost"}
                    className={`w-full justify-start ${pathname.startsWith("/admin") ? "bg-gradient-to-r from-purple-500 to-pink-500 text-white" : "hover:bg-purple-50 hover:text-purple-600"}`}
                  >
                    <Crown className="w-4 h-4 mr-2" />
                    Admin Dashboard
                  </Button>
                </Link>
              )}

              {!isAuthenticated && (
                <div className="pt-2 space-y-2">
                  <Link href="/login">
                    <Button variant="ghost" className="w-full justify-start hover:bg-orange-50 hover:text-orange-600">
                      Sign In
                    </Button>
                  </Link>
                  <Link href="/signup">
                    <Button className="w-full bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white">
                      <Sparkles className="w-4 h-4 mr-2" />
                      Get Started
                    </Button>
                  </Link>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </header>
  );
}
