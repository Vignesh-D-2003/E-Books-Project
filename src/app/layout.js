import { Inter } from "next/font/google";
import "./globals.css";
import { Header } from "@/components/Header";
import { useEffect } from "react";
import { useRouter } from "next/navigation";

const inter = Inter({ subsets: ["latin"] });

export const metadata = {
  title: "E-Library",
  description: "An open source e-library",
};

export default function RootLayout({ children }) {
  const router = useRouter();
  useEffect(() => {
    const token = typeof window !== "undefined" ? localStorage.getItem("authToken") : null;
    if (!token && window.location.pathname !== "/login" && window.location.pathname !== "/signup") {
      router.replace("/login");
    }
  }, []);
  return (
    <html lang="en">
      <body className={inter.className}>
        <Header />
        <main>{children}</main>
      </body>
    </html>
  );
}