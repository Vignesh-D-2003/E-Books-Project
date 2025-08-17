"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import bookService from "@/services/bookService";
import categoryService from "@/services/categoryService";

export default function AddBookPage() {
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [coverUrl, setCoverUrl] = useState("");
  const [pdfFile, setPdfFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [categories, setCategories] = useState([]);
  const router = useRouter();

  useEffect(() => {
    // Fetch categories when component mounts
    const fetchCategories = async () => {
      try {
        const result = await categoryService.getAllCategories();
        if (result.success) {
          setCategories(result.data);
        }
      } catch (err) {
        console.error("Failed to fetch categories:", err);
      }
    };

    fetchCategories();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    if (!title || !author || !categoryId || !pdfFile) {
      setError("Please fill all required fields and upload a PDF file");
      setLoading(false);
      return;
    }

    try {
      const bookData = {
        title,
        author,
        category_id: categoryId,
        cover_url: coverUrl || null, // Make it optional
      };

      const result = await bookService.addBook(bookData, pdfFile);

      if (result.success) {
        alert("Book added successfully!");
        router.push("/books");
      } else {
        setError(result.message || "Failed to add book");
      }
    } catch (err) {
      console.error("Error adding book:", err);
      setError("An error occurred while adding the book");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto py-8 flex justify-center">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Add a New Book</CardTitle>
        </CardHeader>
        <CardContent>
          {error && <p className="text-red-500 mb-4">{error}</p>}
          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="space-y-2">
              <Label htmlFor="title">Title *</Label>
              <Input
                id="title"
                placeholder="Enter book title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="author">Author *</Label>
              <Input
                id="author"
                placeholder="Enter author's name"
                value={author}
                onChange={(e) => setAuthor(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="category">Category *</Label>
              <select
                id="category"
                className="w-full p-2 border border-gray-300 rounded-md"
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                required
              >
                <option value="">Select a category</option>
                {categories.map((category) => (
                  <option
                    key={category.category_id}
                    value={category.category_id}
                  >
                    {category.category_name}
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="cover-image">Cover Image URL (optional)</Label>
              <Input
                id="cover-image"
                placeholder="Enter cover image URL"
                value={coverUrl}
                onChange={(e) => setCoverUrl(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="pdf-file">Book PDF *</Label>
              <Input
                id="pdf-file"
                type="file"
                accept=".pdf"
                onChange={(e) => setPdfFile(e.target.files[0])}
                required
              />
            </div>
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Adding Book..." : "Add Book"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
