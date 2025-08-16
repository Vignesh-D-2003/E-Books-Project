import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";

export default function AdminDashboard() {
  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8">Admin Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <Card>
          <CardHeader>
            <CardTitle>Manage Books</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="mb-4">Add, update, or delete books from the library.</p>
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