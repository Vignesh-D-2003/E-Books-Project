"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import authService from "@/services/authService";

export default function AdminDashboard() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is admin
    const checkAdmin = async () => {
      const isAdmin = authService.isAdmin();
      if (!isAdmin) {
        // Redirect to login if not admin
        router.push("/login");
      } else {
        setLoading(false);
      }
    };

    checkAdmin();
  }, [router]);

  if (loading) {
    return (
      <div className="container mx-auto py-8 flex justify-center items-center">
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Admin Dashboard</h1>
        <Button
          variant="outline"
          onClick={() => {
            authService.logout();
            router.push("/login");
          }}
        >
          Logout
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <Card>
          <CardHeader>
            <CardTitle>Manage Books</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="mb-4">
              Add, update, or delete books from the library.
            </p>
            <Link href="/admin/add-book">
              <Button>Add New Book</Button>
            </Link>
            <Link href="/books" className="ml-4">
              <Button variant="outline">View All Books</Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
