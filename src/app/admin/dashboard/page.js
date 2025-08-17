"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import SearchBar from "@/components/SearchBar";
import bookService from "@/services/bookService";
import authService from "@/services/authService";

export default function AdminDashboard() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [searchResults, setSearchResults] = useState([]);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [searching, setSearching] = useState(false);
  
  const searchTimeoutRef = useRef(null);

  // Memoize the search function to prevent infinite re-renders
  const handleAdminSearch = useCallback(async (query) => {
    // Clear any existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Set a new timeout for debounced search
    searchTimeoutRef.current = setTimeout(async () => {
      if (!query.trim()) {
        setSearchResults([]);
        setShowSearchResults(false);
        return;
      }

      setSearching(true);
      try {
        const result = await bookService.searchBooks(query);
        if (result.success) {
          setSearchResults(result.data.slice(0, 5)); // Show only first 5 results
          setShowSearchResults(true);
        } else {
          setSearchResults([]);
          setShowSearchResults(false);
        }
      } catch (error) {
        console.error("Error searching books:", error);
        setSearchResults([]);
        setShowSearchResults(false);
      } finally {
        setSearching(false);
      }
    }, 500); // 500ms debounce delay
  }, []); // Empty dependency array since we don't need to recreate this function

  useEffect(() => {
    // Check if user is admin
    const checkAdmin = async () => {
      const isAdmin = authService.isAdmin();
      if (!isAdmin) {
        // Redirect to login if not admin
        router.push("/login");
      } else {
        setLoading(false);
      }
    };

    checkAdmin();

    // Cleanup timeout on unmount
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, [router]);

  if (loading) {
    return (
      <div className="container mx-auto py-8 flex justify-center items-center">
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Admin Dashboard</h1>
        <Button
          variant="outline"
          onClick={() => {
            authService.logout();
            router.push("/login");
          }}
        >
          Logout
        </Button>
      </div>

      {/* Quick Search Section */}
      <Card className="mb-8">
        <CardHeader>
          <CardTitle>Quick Search</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="mb-4 text-gray-600">
            Quickly search for books to manage them.
          </p>
          <SearchBar 
            onSearch={handleAdminSearch}
            placeholder="Search books by title or author..."
            className="mb-4"
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
          
          {/* Search Results */}
          {showSearchResults && searchResults.length > 0 && (
            <div className="mt-4">
              <h3 className="text-lg font-semibold mb-2">Quick Results:</h3>
              <div className="space-y-2">
                {searchResults.map((book) => (
                  <div key={book.book_id} className="flex justify-between items-center p-3 bg-gray-50 rounded-md">
                    <div>
                      <p className="font-medium">{book.title}</p>
                      <p className="text-sm text-gray-600">by {book.author}</p>
                    </div>
                    <div className="flex gap-2">
                      <Link href={`/admin/edit-book/${book.book_id}`}>
                        <Button size="sm" variant="outline">Edit</Button>
                      </Link>
                      <Link href="/books">
                        <Button size="sm">View All</Button>
                      </Link>
                    </div>
                  </div>
                ))}
                {searchResults.length >= 5 && (
                  <div className="text-center mt-2">
                    <Link href="/books">
                      <Button variant="outline" size="sm">
                        View all search results
                      </Button>
                    </Link>
                  </div>
                )}
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <Card>
          <CardHeader>
            <CardTitle>Manage Books</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="mb-4">
              Add, update, or delete books from the library.
            </p>
            <Link href="/admin/add-book">
              <Button>Add New Book</Button>
            </Link>
            <Link href="/books" className="ml-4">
              <Button variant="outline">View All Books</Button>
            </Link>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="mb-4">
              Common administrative tasks.
            </p>
            <div className="space-y-2">
              <Link href="/books">
                <Button variant="outline" className="w-full justify-start">
                  Browse All Books
                </Button>
              </Link>
              <Link href="/admin/add-book">
                <Button variant="outline" className="w-full justify-start">
                  Add New Book
                </Button>
              </Link>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
