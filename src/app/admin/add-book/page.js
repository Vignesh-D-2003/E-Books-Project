"use client";

import * as Sentry from "@sentry/nextjs";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function AddBookPage() {
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const formData = new FormData(e.target);

      const newBook = {
        title: formData.get("title"),
        author: formData.get("author"),
        category: formData.get("category"),
        coverImage: formData.get("cover-image"),
        pdfFile: formData.get("pdf-file"),
      };

      // Simple validation (example)
      if (!newBook.title || !newBook.author) {
        throw new Error("Title and Author are required fields");
      }

      // Future: Send to backend
      // const res = await fetch("/api/books", {
      //   method: "POST",
      //   body: JSON.stringify(newBook),
      // });
      // if (!res.ok) throw new Error("Failed to save book");

      // Simulate error for testing
      // throw new Error("Database insert failed!");

      console.log("Book submitted:", newBook);

    } catch (err) {
      console.error("AddBookPage error:", err);
      setError("Failed to add book. Please try again.");

      // Send error details to Sentry
      Sentry.captureException(err, {
        tags: { page: "AddBookPage" },
        extra: { form: "AddBookForm" },
      });
    }
  };

  return (
    <div className="container mx-auto py-8 flex justify-center">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Add a New Book</CardTitle>
        </CardHeader>
        <CardContent>
          {error && (
            <p className="text-red-500 mb-4 text-center">
              {error} (We’ve been notified)
            </p>
          )}
          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="space-y-2">
              <Label htmlFor="title">Title</Label>
              <Input id="title" name="title" placeholder="Enter book title" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="author">Author</Label>
              <Input id="author" name="author" placeholder="Enter author's name" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="category">Category</Label>
              <Input id="category" name="category" placeholder="Enter book category" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="cover-image">Cover Image URL</Label>
              <Input id="cover-image" name="cover-image" placeholder="Enter cover image URL" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="pdf-file">Book PDF</Label>
              <Input id="pdf-file" name="pdf-file" type="file" />
            </div>
            <Button type="submit" className="w-full">Add Book</Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
