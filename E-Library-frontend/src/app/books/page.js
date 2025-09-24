"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { BookCard } from "@/components/BookCard";
import SearchBar from "@/components/SearchBar";
import bookService from "@/services/bookService";
import categoryService from "@/services/categoryService";
import authService from "@/services/authService";
import { 
  BookOpen, 
  Search, 
  Filter, 
  Grid3X3, 
  List, 
  Download, 
  TrendingUp,
  Library,
  Eye,
  Clock,
  Users,
  CheckCircle,
  AlertCircle,
  ArrowRight,
  RefreshCw,
  Settings
} from "lucide-react";
import Link from "next/link";

export default function BooksPage() {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [viewMode, setViewMode] = useState("grid"); // grid or list
  const [stats, setStats] = useState({
    totalBooks: 0,
    totalCategories: 0,
    recentBooks: 0
  });
  
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

  // Fetch statistics
  const fetchStats = async () => {
    try {
      const [booksResult, categoriesResult] = await Promise.all([
        bookService.getAllBooks(),
        categoryService.getAllCategories()
      ]);

      if (booksResult.success) {
        setStats(prev => ({
          ...prev,
          totalBooks: booksResult.data.length,
          recentBooks: booksResult.data.slice(0, 10).length
        }));
      }

      if (categoriesResult.success) {
        setStats(prev => ({
          ...prev,
          totalCategories: categoriesResult.data.length
        }));
      }
    } catch (error) {
      console.error("Error fetching stats:", error);
    }
  };

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

        await fetchStats();
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
      <div className="min-h-screen bg-gradient-to-br from-orange-50 via-pink-50 to-purple-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600 font-medium">Loading your library...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-pink-50 to-purple-50">
      {/* Header */}
      <div className="bg-white/80 backdrop-blur-sm border-b border-white/20 shadow-sm">
        <div className="container mx-auto px-6 py-6">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-gradient-to-br from-orange-500 to-pink-500 rounded-2xl flex items-center justify-center">
                <Library className="w-7 h-7 text-white" />
              </div>
              <div>
                <h1 className="text-3xl font-bold bg-gradient-to-r from-orange-600 to-pink-600 bg-clip-text text-transparent">
                  Digital Library
                </h1>
                <p className="text-gray-600">Discover and read amazing books</p>
              </div>
            </div>
            
            {isAdmin && (
              <Link href="/admin/dashboard">
                <button className="px-4 py-2 bg-gradient-to-r from-orange-500 to-pink-500 text-white rounded-xl hover:from-orange-600 hover:to-pink-600 transition-all duration-200 flex items-center gap-2">
                  <Settings className="w-4 h-4" />
                  Admin Panel
                </button>
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="container mx-auto px-6 py-8">
        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg border border-white/20">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Books</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalBooks}</p>
                <p className="text-xs text-green-600 flex items-center gap-1 mt-1">
                  <TrendingUp className="w-3 h-3" />
                  Available for reading
                </p>
              </div>
              <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center">
                <BookOpen className="w-6 h-6 text-white" />
              </div>
            </div>
          </div>

          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg border border-white/20">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Categories</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalCategories}</p>
                <p className="text-xs text-blue-600 flex items-center gap-1 mt-1">
                  <Filter className="w-3 h-3" />
                  Organized content
                </p>
              </div>
              <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-green-600 rounded-xl flex items-center justify-center">
                <Grid3X3 className="w-6 h-6 text-white" />
              </div>
            </div>
          </div>

          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg border border-white/20">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Recent Additions</p>
                <p className="text-2xl font-bold text-gray-900">{stats.recentBooks}</p>
                <p className="text-xs text-purple-600 flex items-center gap-1 mt-1">
                  <Clock className="w-3 h-3" />
                  Latest books
                </p>
              </div>
              <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center">
                <Clock className="w-6 h-6 text-white" />
              </div>
            </div>
          </div>
        </div>

        {/* Search and Filter Section */}
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-xl border border-white/20 p-8 mb-8">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6 mb-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center">
                <Search className="w-5 h-5 text-white" />
              </div>
              <div>
                <h2 className="text-xl font-semibold text-gray-900">Find Your Next Read</h2>
                <p className="text-sm text-gray-600">Search and filter through our collection</p>
              </div>
            </div>

            {/* View Mode Toggle */}
            <div className="flex items-center gap-2 bg-gray-100 rounded-xl p-1">
              <button
                onClick={() => setViewMode("grid")}
                className={`p-2 rounded-lg transition-all duration-200 ${
                  viewMode === "grid" 
                    ? "bg-white shadow-sm text-orange-600" 
                    : "text-gray-600 hover:text-gray-900"
                }`}
              >
                <Grid3X3 className="w-5 h-5" />
              </button>
              <button
                onClick={() => setViewMode("list")}
                className={`p-2 rounded-lg transition-all duration-200 ${
                  viewMode === "list" 
                    ? "bg-white shadow-sm text-orange-600" 
                    : "text-gray-600 hover:text-gray-900"
                }`}
              >
                <List className="w-5 h-5" />
              </button>
            </div>
          </div>

          <SearchBar 
            onSearch={handleSearch}
            placeholder="Search books by title or author..."
            showCategoryFilter={true}
            categories={categories}
          />
        </div>

        {/* Loading indicator for search */}
        {searching && (
          <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-xl">
            <p className="text-blue-800 flex items-center gap-2">
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-500"></div>
              Searching your library...
            </p>
          </div>
        )}

        {/* Error Display */}
        {error && (
          <div className="mb-8 p-4 bg-red-50 border border-red-200 rounded-xl">
            <div className="flex items-center gap-3">
              <AlertCircle className="w-5 h-5 text-red-600" />
              <p className="text-red-800 font-medium">{error}</p>
            </div>
          </div>
        )}

        {/* Search Results Summary */}
        {(searchQuery || selectedCategory) && (
          <div className="mb-6 p-4 bg-gradient-to-r from-blue-50 to-purple-50 border border-blue-200 rounded-xl">
            <div className="flex items-center gap-3">
              <CheckCircle className="w-5 h-5 text-blue-600" />
              <p className="text-blue-800 font-medium">
                Found {books.length} book{books.length !== 1 ? 's' : ''}
                {searchQuery && ` matching "${searchQuery}"`}
                {selectedCategory && categories.length > 0 && (
                  ` in category "${categories.find(c => c.category_id === parseInt(selectedCategory))?.category_name}"`
                )}
              </p>
            </div>
          </div>
        )}

        {/* Books Display */}
        {books.length === 0 ? (
          <div className="text-center py-16">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <BookOpen className="w-12 h-12 text-gray-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">
              {searchQuery || selectedCategory ? "No books found" : "No books available"}
            </h3>
            <p className="text-gray-600 mb-6">
              {searchQuery || selectedCategory 
                ? `No books found${searchQuery ? ` for "${searchQuery}"` : ''}${selectedCategory && categories.length > 0 ? ` in category "${categories.find(c => c.category_id === parseInt(selectedCategory))?.category_name}"` : ''}`
                : "The library is currently empty. Check back later for new additions!"
              }
            </p>
            {(searchQuery || selectedCategory) && (
              <button
                onClick={() => handleSearch("", "")}
                className="px-6 py-3 bg-gradient-to-r from-orange-500 to-pink-500 text-white rounded-xl hover:from-orange-600 hover:to-pink-600 transition-all duration-200 flex items-center gap-2 mx-auto"
              >
                <RefreshCw className="w-4 h-4" />
                Show All Books
              </button>
            )}
          </div>
        ) : (
          <div className={`grid gap-6 ${
            viewMode === "grid" 
              ? "grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5" 
              : "grid-cols-1"
          }`}>
            {books.map((book) => (
              <BookCard 
                key={book.book_id} 
                book={book} 
                isAdmin={isAdmin} 
                viewMode={viewMode}
              />
            ))}
          </div>
        )}

        {/* Footer */}
        <div className="mt-16 text-center">
          <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-8 border border-white/20">
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Enjoy Your Reading Journey</h3>
            <p className="text-gray-600 mb-4">
              Discover new worlds, learn new things, and expand your knowledge with our digital library.
            </p>
           
          </div>
        </div>
      </div>
    </div>
  );
}
