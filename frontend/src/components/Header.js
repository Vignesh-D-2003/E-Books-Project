import Link from "next/link";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";

export function Header() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [role, setRole] = useState("");

  useEffect(() => {
    if (typeof window !== "undefined") {
      const token = localStorage.getItem("authToken");
      setIsAuthenticated(!!token);
      const userRole = localStorage.getItem("userRole");
      setRole(userRole || "");
    }
  }, []);

  return (
    <header className="bg-white shadow-sm">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <Link href="/" className="text-2xl font-bold">
          E-Library
        </Link>
        <nav className="flex items-center space-x-4">
          <Link href="/books">
            <Button variant="ghost">All Books</Button>
          </Link>
          {isAuthenticated && role === "admin" && (
            <Link href="/admin/dashboard">
              <Button variant="ghost">Admin</Button>
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
            <Button variant="outline" onClick={() => {
              localStorage.removeItem("authToken");
              localStorage.removeItem("userRole");
              window.location.href = "/login";
            }}>Logout</Button>
          )}
        </nav>
      </div>
    </header>
  );
}