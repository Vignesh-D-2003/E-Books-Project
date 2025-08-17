"use client";
import React from "react";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import bookService from "@/services/bookService";
import categoryService from "@/services/categoryService";

export default function EditBookPage({ params }) {
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [fileUrl, setfileUrl] = useState("");
  const [uploadedBy, setuploadedBy] = useState(0);
  // const [coverUrl, setCoverUrl] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [categories, setCategories] = useState([]);
  const router = useRouter();
  // const bookId = params.id;
  const unwrappedParams = React.use(params);
  const bookId = unwrappedParams.id;

  useEffect(() => {
    // Fetch book details and categories
    const fetchData = async () => {
      try {
        const [bookResult, categoriesResult] = await Promise.all([
          bookService.getBookById(bookId),
          categoryService.getAllCategories(),
        ]);

        if (bookResult.success && bookResult.data) {
          
          const book = bookResult.data;
          console.log(book);
          setTitle(book.title || "");
          setAuthor(book.author || "");
          setCategoryId(book.category_id || "");
          setfileUrl(book.file_url || "");
          setuploadedBy(book.uploaded_by || 0);

          // setCoverUrl(book.cover_url || "");
        } else {
          setError("Failed to fetch book details");
        }

        if (categoriesResult.success) {
          setCategories(categoriesResult.data);
        }
      } catch (err) {
        console.error("Error fetching data:", err);
        setError("An error occurred while fetching data");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [bookId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");

    if (!title || !author || !categoryId) {
      setError("Please fill all required fields");
      setSaving(false);
      return;
    }

    try {
      const updates = {
        title,
        author,
        category_id: categoryId,
        file_url: fileUrl,
        uploaded_by: uploadedBy
        // cover_url: coverUrl || null,
      };
      console.log('sending update info');
      
      console.log(updates);
      
      const result = await bookService.updateBook(bookId, updates);

      if (result.success) {
        alert("Book updated successfully!");
        router.push("/books");
      } else {
        setError(result.message || "Failed to update book");
      }
    } catch (err) {
      console.error("Error updating book:", err);
      setError("An error occurred while updating the book");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto py-8 flex justify-center">
        <p>Loading book details...</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 flex justify-center">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Edit Book (ID: {bookId})</CardTitle>
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
            {/* <div className="space-y-2">
              <Label htmlFor="cover-image">Cover Image URL (optional)</Label>
              <Input
                id="cover-image"
                placeholder="Enter cover image URL"
                value={coverUrl}
                onChange={(e) => setCoverUrl(e.target.value)}
              />
            </div> */}
            <div className="space-y-2">
              <p className="text-sm text-gray-500">
                Note: To change the PDF file, please delete this book and add a
                new one.
              </p>
            </div>
            <Button type="submit" className="w-full" disabled={saving}>
              {saving ? "Saving Changes..." : "Save Changes"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
