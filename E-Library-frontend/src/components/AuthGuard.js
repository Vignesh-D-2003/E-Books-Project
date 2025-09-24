"use client";

import { useState, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function AuthGuard({ children }) {
  const router = useRouter();
  const pathname = usePathname();
  const [loading, setLoading] = useState(true);
  const { isAuthenticated, isAdmin, loading: authLoading } = useAuth();

  useEffect(() => {
    if (authLoading) {
      return;
    }
    const isAdminRoute = pathname.startsWith("/admin");

    console.log("AuthGuard check:", { isAuthenticated, isAdmin, pathname });

    if (!isAuthenticated) {
      console.log("Not authenticated, redirecting to login");
      router.push("/login");
      return;
    }

    if (isAdminRoute && !isAdmin) {
      console.log(
        "Not admin but trying to access admin route, redirecting to books"
      );
      router.push("/books");
      return;
    }

    console.log("Auth check passed");
    setLoading(false);
  }, [pathname, router, isAuthenticated, isAdmin, authLoading]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <p>Loading...</p>
      </div>
    );
  }

  return children;
}
