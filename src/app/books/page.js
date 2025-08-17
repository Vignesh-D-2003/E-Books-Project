"use client";

import { useState, useEffect } from "react";
import { BookCard } from "@/components/BookCard";
import bookService from "@/services/bookService";
import authService from "@/services/authService";

export default function BooksPage() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    // Check if user is admin
    setIsAdmin(authService.isAdmin());

    // Fetch books
    const fetchBooks = async () => {
      try {
        const result = await bookService.getAllBooks();
        if (result.success) {
          setBooks(result.data);
        } else {
          setError(result.message);
        }
      } catch (error) {
        console.error("Error fetching books:", error);
        setError("Failed to load books. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, []);

  if (loading) {
    return (
      <div className="container mx-auto py-8 flex justify-center items-center">
        <p>Loading books...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto py-8 flex justify-center items-center">
        <p className="text-red-500">{error}</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8">All Books</h1>
      {books.length === 0 ? (
        <p className="text-center">No books found</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
          {books.map((book) => (
            <BookCard key={book.book_id} book={book} isAdmin={isAdmin} />
          ))}
        </div>
      )}
    </div>
  );
}
