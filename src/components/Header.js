"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { useState, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export function Header() {
  const [mounted, setMounted] = useState(false);
  const router = useRouter();
  const pathname = usePathname();
  const { isAuthenticated, isAdmin, logout } = useAuth();

  useEffect(() => {
    setMounted(true);
  }, []);

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  if (!mounted) {
    return null;
  }

  return (
    <header className="bg-white shadow-sm">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <Link href="/" className="text-2xl font-bold">
          E-Library
        </Link>
        <nav className="flex items-center space-x-4">
          {isAuthenticated && (
            <Link href="/books">
              <Button variant="ghost">All Books</Button>
            </Link>
          )}
          {isAuthenticated && isAdmin && (
            <Link href="/admin/dashboard">
              <Button variant="ghost">Admin Dashboard</Button>
            </Link>
          )}
          {!isAuthenticated && (
            <>
              <Link href="/login">
                <Button>Login</Button>
              </Link>
              <Link href="/signup">
                <Button variant="outline">Sign Up</Button>
              </Link>
            </>
          )}
          {isAuthenticated && (
            <Button variant="outline" onClick={handleLogout}>
              Logout
            </Button>
          )}
        </nav>
      </div>
    </header>
  );
}
