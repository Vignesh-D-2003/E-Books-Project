"use client";

import { Inter } from "next/font/google";
import "./globals.css";
import { Header } from "@/components/Header";
import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import AuthGuard from "@/components/AuthGuard";
import { AuthProvider } from "@/context/AuthContext";

const inter = Inter({ subsets: ["latin"] });

// Public routes that don't require authentication
const publicRoutes = ["/login", "/signup"];

export default function RootLayout({ children }) {
  const pathname = usePathname();
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  const isPublicRoute = publicRoutes.includes(pathname);

  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider>
          <Header />
          <main>
            {isClient ? (
              isPublicRoute ? (
                children
              ) : (
                <AuthGuard>{children}</AuthGuard>
              )
            ) : (
              <div className="flex justify-center items-center h-screen">
                <p>Loading...</p>
              </div>
            )}
          </main>
        </AuthProvider>
      </body>
    </html>
  );
}
