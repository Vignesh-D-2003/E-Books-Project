"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import authService from "@/services/authService";

export default function HomePage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    setIsAuthenticated(authService.isAuthenticated());
    setIsAdmin(authService.isAdmin());
  }, []);

  if (!mounted) {
    return null;
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-r from-orange-50 via-pink-50 to-purple-50">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle className="text-center text-4xl">
            Welcome to the E-Library
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="mt-6 text-lg leading-8 text-pink-600 text-center">
            Your open source solution for accessing a world of books.
          </p>
          <div className="mt-10 flex items-center justify-center gap-x-6">
            {isAuthenticated ? (
              <>
                <Link href="/books">
                  <Button size="lg">Browse Books</Button>
                </Link>
                {isAdmin && (
                  <Link href="/admin/dashboard">
                    <Button size="lg" variant="outline">
                      Admin Dashboard
                    </Button>
                  </Link>
                )}
              </>
            ) : (
              <>
                <Link href="/login">
                  <Button size="lg">Login</Button>
                </Link>
                <Link href="/signup">
                  <Button size="lg" variant="outline">
                    Sign Up
                  </Button>
                </Link>
              </>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
