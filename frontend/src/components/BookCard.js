import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export function BookCard({ book, isAdmin }) {
  return (
    <Card>
      <CardContent className="p-4">
        <img src={book.imageUrl} alt={book.title} className="w-full h-48 object-cover mb-4 rounded-md" />
        <h3 className="text-lg font-semibold mb-1">{book.title}</h3>
        <p className="text-sm text-gray-500 mb-4">{book.author}</p>
        <Button className="w-full">Read Book</Button>
        {isAdmin && (
          <div className="mt-4 flex justify-between">
            <Link href={`/admin/edit-book/${book.id}`}>
              <Button variant="outline" size="sm">Edit</Button>
            </Link>
            <Button variant="destructive" size="sm">Delete</Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}