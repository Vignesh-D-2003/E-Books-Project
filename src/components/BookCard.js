"use client";

import Link from "next/link";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import bookService from "@/services/bookService";

export function BookCard({ book, isAdmin }) {
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleted, setDeleted] = useState(false);

  const imageUrl = "/book.svg";

  const handleReadBook = () => {
    if (book.file_url) {
      window.open(book.file_url, "_blank");
    }
  };

  const handleDelete = async () => {
    if (window.confirm(`Are you sure you want to delete "${book.title}"?`)) {
      setIsDeleting(true);
      try {
        const result = await bookService.deleteBook(book.book_id);
        if (result.success) {
          setDeleted(true);
        } else {
          alert(result.message || "Failed to delete book");
        }
      } catch (error) {
        console.error("Error deleting book:", error);
        alert("An error occurred while deleting the book");
      } finally {
        setIsDeleting(false);
      }
    }
  };

  if (deleted) {
    return null;
  }

  return (
    <Card>
      <CardContent className="p-4">
        <div className="flex justify-center items-center">
          <img
            src={imageUrl}
            alt={book.title}
            className="w-20 h-32 object-cover mb-4 rounded-md"
          />

        </div>
        <h3 className="text-lg font-semibold mb-1">{book.title}</h3>
        <p className="text-sm text-gray-500 mb-4">{book.author}</p>
        <Button className="w-full" onClick={handleReadBook}>
          Read Book
        </Button>
        {isAdmin && (
          <div className="mt-4 flex justify-between">
            <Link href={`/admin/edit-book/${book.book_id}`}>
              <Button variant="outline" size="sm">
                Edit
              </Button>
            </Link>
            <Button
              variant="destructive"
              size="sm"
              onClick={handleDelete}
              disabled={isDeleting}
            >
              {isDeleting ? "Deleting..." : "Delete"}
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
