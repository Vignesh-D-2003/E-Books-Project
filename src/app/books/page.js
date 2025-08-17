"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { BookCard } from "@/components/BookCard";
import SearchBar from "@/components/SearchBar";
import bookService from "@/services/bookService";
import categoryService from "@/services/categoryService";
import authService from "@/services/authService";

export default function BooksPage() {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  
  const searchTimeoutRef = useRef(null);

  // Memoize the search function to prevent infinite re-renders
  const handleSearch = useCallback(async (query, categoryId) => {
    // Clear any existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Set a new timeout for debounced search
    searchTimeoutRef.current = setTimeout(async () => {
      setSearching(true);
      
      try {
        // If both search and category are empty, fetch all books
        if (!query.trim() && !categoryId) {
          setLoading(true);
          const result = await bookService.getAllBooks();
          if (result.success) {
            setBooks(result.data);
            setSearchQuery("");
            setSelectedCategory("");
          } else {
            setError(result.message);
          }
          setLoading(false);
          return;
        }

        let result;
        
        // If only category filter is applied
        if (!query.trim() && categoryId) {
          const allBooksResult = await bookService.getAllBooks();
          if (allBooksResult.success) {
            const filteredBooks = allBooksResult.data.filter(book => 
              book.category_id === parseInt(categoryId)
            );
            setBooks(filteredBooks);
            setSearchQuery("");
            setSelectedCategory(categoryId);
            setError(null);
          } else {
            setError(allBooksResult.message);
            setBooks([]);
          }
        } else {
          // If search query is provided, use the search endpoint
          result = await bookService.searchBooks(query);
          if (result.success) {
            let filteredBooks = result.data;
            
            // If category is also selected, filter the search results
            if (categoryId) {
              filteredBooks = filteredBooks.filter(book => 
                book.category_id === parseInt(categoryId)
              );
            }
            
            setBooks(filteredBooks);
            setSearchQuery(query);
            setSelectedCategory(categoryId);
            setError(null);
          } else {
            setError(result.message);
            setBooks([]);
          }
        }
      } catch (error) {
        console.error("Error searching books:", error);
        setError("Failed to search books. Please try again later.");
        setBooks([]);
      } finally {
        setSearching(false);
      }
    }, 500); // 500ms debounce delay
  }, []); // Empty dependency array since we don't need to recreate this function

  useEffect(() => {
    // Check if user is admin
    setIsAdmin(authService.isAdmin());

    // Fetch books and categories
    const fetchData = async () => {
      try {
        const [booksResult, categoriesResult] = await Promise.all([
          bookService.getAllBooks(),
          categoryService.getAllCategories()
        ]);

        if (booksResult.success) {
          setBooks(booksResult.data);
        } else {
          setError(booksResult.message);
        }

        if (categoriesResult.success) {
          setCategories(categoriesResult.data);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
        setError("Failed to load data. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();

    // Cleanup timeout on unmount
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, []); // Only run once on component mount

  if (loading) {
    return (
      <div className="container mx-auto py-8 flex justify-center items-center">
        <p>Loading books...</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8">All Books</h1>
      
      {/* Enhanced Search Bar */}
      <SearchBar 
        onSearch={handleSearch}
        placeholder="Search books by title or author..."
        showCategoryFilter={true}
        categories={categories}
      />

      {/* Loading indicator for search */}
      {searching && (
        <div className="mb-4 p-4 bg-blue-50 border border-blue-200 rounded-md">
          <p className="text-blue-800 flex items-center gap-2">
            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-500"></div>
            Searching...
          </p>
        </div>
      )}

      {error && (
        <div className="mb-8 p-4 bg-red-50 border border-red-200 rounded-md">
          <p className="text-red-600">{error}</p>
        </div>
      )}

      {/* Search Results Summary */}
      {(searchQuery || selectedCategory) && (
        <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-md">
          <p className="text-blue-800">
            Found {books.length} book{books.length !== 1 ? 's' : ''}
            {searchQuery && ` matching "${searchQuery}"`}
            {selectedCategory && categories.length > 0 && (
              ` in category "${categories.find(c => c.category_id === parseInt(selectedCategory))?.category_name}"`
            )}
          </p>
        </div>
      )}

      {books.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">
            {searchQuery || selectedCategory 
              ? `No books found${searchQuery ? ` for "${searchQuery}"` : ''}${selectedCategory && categories.length > 0 ? ` in category "${categories.find(c => c.category_id === parseInt(selectedCategory))?.category_name}"` : ''}`
              : "No books found"
            }
          </p>
          {(searchQuery || selectedCategory) && (
            <button
              onClick={() => handleSearch("", "")}
              className="mt-4 text-blue-600 hover:text-blue-800 underline"
            >
              Clear search and show all books
            </button>
          )}
        </div>
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
