import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";

export default function HomePage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-r from-orange-50 via-pink-50 to-purple-50">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle className="text-center text-4xl">Welcome to the E-Library</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="mt-6 text-lg leading-8 text-pink-600 text-center">
            Your open source solution for accessing a world of books.
          </p>
          <div className="mt-10 flex items-center justify-center gap-x-6">
            <Link href="/books">
              <Button size="lg">Browse Books</Button>
            </Link>
            <Link href="/login">
              <Button size="lg" variant="outline">
                Login or Sign Up
              </Button>
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}