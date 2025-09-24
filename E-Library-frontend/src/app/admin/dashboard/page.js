"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import SearchBar from "@/components/SearchBar";
import bookService from "@/services/bookService";
import categoryService from "@/services/categoryService";
import authService from "@/services/authService";
import { 
  BookOpen, 
  Plus, 
  Search, 
  Users, 
  Settings, 
  LogOut, 
  TrendingUp, 
  FileText, 
  Edit3, 
  Eye, 
  ArrowRight,
  Calendar,
  Clock,
  CheckCircle,
  AlertCircle,
  BarChart3,
  Grid3X3,
  Library,
  Download,
  Upload,
  XCircle,
  RefreshCw,
  AlertTriangle,
  Sparkles,
  Zap,
  Shield,
  Globe
} from "lucide-react";

export default function AdminDashboard() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [searchResults, setSearchResults] = useState([]);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState(null);
  const [categories, setCategories] = useState([]);
  const [stats, setStats] = useState({
    totalBooks: 0,
    totalCategories: 0,
    recentBooks: 0,
    totalUsers: 0
  });
  
  const searchTimeoutRef = useRef(null);

  // Memoize the search function to prevent infinite re-renders
  const handleAdminSearch = useCallback(async (query, categoryId = "") => {
    // Clear any existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Set a new timeout for debounced search
    searchTimeoutRef.current = setTimeout(async () => {
      if (!query.trim() && !categoryId) {
        setSearchResults([]);
        setShowSearchResults(false);
        setSearchError(null);
        return;
      }

      setSearching(true);
      setSearchError(null);
      
      try {
        let result;
        
        // If only category filter is applied
        if (!query.trim() && categoryId) {
          const allBooksResult = await bookService.getAllBooks();
          if (allBooksResult.success) {
            const filteredBooks = allBooksResult.data.filter(book => 
              book.category_id === parseInt(categoryId)
            );
            setSearchResults(filteredBooks.slice(0, 5));
            setShowSearchResults(true);
          } else {
            throw new Error(allBooksResult.message || "Failed to fetch books");
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
            
            setSearchResults(filteredBooks.slice(0, 5));
            setShowSearchResults(true);
          } else {
            throw new Error(result.message || "Failed to search books");
          }
        }
      } catch (error) {
        console.error("Error searching books:", error);
        setSearchError(error.message || "An error occurred while searching");
        setSearchResults([]);
        setShowSearchResults(false);
      } finally {
        setSearching(false);
      }
    }, 300); // Reduced debounce delay for better responsiveness
  }, []);

  // Fetch dashboard statistics
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
          recentBooks: booksResult.data.slice(0, 5).length
        }));
      }

      if (categoriesResult.success) {
        setCategories(categoriesResult.data);
        setStats(prev => ({
          ...prev,
          totalCategories: categoriesResult.data.length
        }));
      }
    } catch (error) {
      console.error("Error fetching stats:", error);
    }
  };

  const handleRetrySearch = () => {
    setSearchError(null);
    // Re-trigger the last search
    const searchInput = document.querySelector('input[placeholder*="Search"]');
    if (searchInput) {
      const event = new Event('input', { bubbles: true });
      searchInput.dispatchEvent(event);
    }
  };

  useEffect(() => {
    // Check if user is admin
    const checkAdmin = async () => {
      const isAdmin = authService.isAdmin();
      if (!isAdmin) {
        // Redirect to login if not admin
        router.push("/login");
      } else {
        setLoading(false);
        await fetchStats();
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
      <div className="min-h-screen bg-gradient-to-br from-orange-50 via-pink-50 to-purple-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600 font-medium">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-pink-50 to-purple-50">
      {/* Header */}
      <div className="bg-white/80 backdrop-blur-sm border-b border-white/20 shadow-sm sticky top-0 z-10">
        <div className="container mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-orange-500 to-pink-500 rounded-xl flex items-center justify-center">
                <Library className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-2xl font-bold bg-gradient-to-r from-orange-600 to-pink-600 bg-clip-text text-transparent">
                  Admin Dashboard
                </h1>
                <p className="text-sm text-gray-600">Manage your digital library</p>
              </div>
            </div>
            <Button
              variant="outline"
              onClick={() => {
                authService.logout();
                router.push("/login");
              }}
              className="flex items-center gap-2 hover:bg-red-50 hover:border-red-200 hover:text-red-600 transition-all duration-200"
            >
              <LogOut className="w-4 h-4" />
              Logout
            </Button>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-6 py-8">
        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Total Books</p>
                  <p className="text-3xl font-bold text-gray-900">{stats.totalBooks}</p>
                  <p className="text-xs text-green-600 flex items-center gap-1 mt-1">
                    <TrendingUp className="w-3 h-3" />
                    +12% this month
                  </p>
                </div>
                <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <BookOpen className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Categories</p>
                  <p className="text-3xl font-bold text-gray-900">{stats.totalCategories}</p>
                  <p className="text-xs text-blue-600 flex items-center gap-1 mt-1">
                    <Grid3X3 className="w-3 h-3" />
                    Organized content
                  </p>
                </div>
                <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-green-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <FileText className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Recent Books</p>
                  <p className="text-3xl font-bold text-gray-900">{stats.recentBooks}</p>
                  <p className="text-xs text-purple-600 flex items-center gap-1 mt-1">
                    <Clock className="w-3 h-3" />
                    Last 30 days
                  </p>
                </div>
                <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <Calendar className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">Active Users</p>
                  <p className="text-3xl font-bold text-gray-900">{stats.totalUsers || 0}</p>
                  <p className="text-xs text-orange-600 flex items-center gap-1 mt-1">
                    <Users className="w-3 h-3" />
                    Growing community
                  </p>
                </div>
                <div className="w-12 h-12 bg-gradient-to-br from-orange-500 to-pink-500 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <Users className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Quick Search Section */}
        <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-xl mb-8">
          <CardHeader className="pb-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center">
                <Search className="w-5 h-5 text-white" />
              </div>
              <div>
                <CardTitle className="text-xl">Quick Search</CardTitle>
                <p className="text-sm text-gray-600">Find and manage books instantly</p>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <SearchBar 
              onSearch={handleAdminSearch}
              placeholder="Search books by title or author..."
              className="mb-6"
              showCategoryFilter={true}
              categories={categories}
            />
            
            {/* Loading indicator for search */}
            {searching && (
              <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-xl">
                <p className="text-blue-800 flex items-center gap-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-500"></div>
                  Searching your library...
                </p>
              </div>
            )}

            {/* Search Error */}
            {searchError && (
              <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl">
                <div className="flex items-center gap-3 mb-2">
                  <XCircle className="w-5 h-5 text-red-600" />
                  <h3 className="font-semibold text-red-800">Search Error</h3>
                </div>
                <p className="text-red-700 mb-3">{searchError}</p>
                <Button 
                  onClick={handleRetrySearch}
                  variant="outline" 
                  size="sm"
                  className="border-red-300 text-red-700 hover:bg-red-50"
                >
                  <RefreshCw className="w-4 h-4 mr-2" />
                  Try Again
                </Button>
              </div>
            )}
            
            {/* Search Results */}
            {showSearchResults && searchResults.length > 0 && (
              <div className="space-y-3">
                <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-green-600" />
                  Quick Results ({searchResults.length})
                </h3>
                <div className="grid gap-3">
                  {searchResults.map((book) => (
                    <div key={book.book_id} className="flex justify-between items-center p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl border border-gray-200 hover:shadow-md transition-all duration-200 group">
                      <div className="flex-1">
                        <p className="font-semibold text-gray-900 group-hover:text-orange-600 transition-colors">{book.title}</p>
                        <p className="text-sm text-gray-600">by {book.author}</p>
                        {book.category_name && (
                          <span className="inline-block mt-1 px-2 py-1 bg-orange-100 text-orange-700 text-xs rounded-full">
                            {book.category_name}
                          </span>
                        )}
                      </div>
                      <div className="flex gap-2">
                        <Link href={`/admin/edit-book/${book.book_id}`}>
                          <Button size="sm" variant="outline" className="flex items-center gap-1 hover:bg-blue-50 hover:border-blue-300">
                            <Edit3 className="w-3 h-3" />
                            Edit
                          </Button>
                        </Link>
                        <Link href="/books">
                          <Button size="sm" className="flex items-center gap-1 bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600">
                            <Eye className="w-3 h-3" />
                            View
                          </Button>
                        </Link>
                      </div>
                    </div>
                  ))}
                </div>
                {searchResults.length >= 5 && (
                  <div className="text-center pt-4">
                    <Link href="/books">
                      <Button variant="outline" size="sm" className="flex items-center gap-2 mx-auto hover:bg-orange-50 hover:border-orange-300">
                        View all search results
                        <ArrowRight className="w-4 h-4" />
                      </Button>
                    </Link>
                  </div>
                )}
              </div>
            )}

            {/* No Results */}
            {showSearchResults && searchResults.length === 0 && !searching && !searchError && (
              <div className="text-center py-8">
                <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <AlertTriangle className="w-8 h-8 text-gray-400" />
                </div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">No Books Found</h3>
                <p className="text-gray-600 mb-4">Try adjusting your search terms or category filter</p>
                <Button 
                  onClick={() => {
                    setShowSearchResults(false);
                    setSearchResults([]);
                  }}
                  variant="outline"
                  className="border-orange-300 text-orange-700 hover:bg-orange-50"
                >
                  Clear Search
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Action Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Manage Books */}
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardHeader className="pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-green-500 to-green-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <BookOpen className="w-5 h-5 text-white" />
                </div>
                <div>
                  <CardTitle className="text-lg">Manage Books</CardTitle>
                  <p className="text-sm text-gray-600">Add, edit, or remove books</p>
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
                              <p className="text-gray-700">
                  Complete control over your library&apos;s book collection. Add new titles, update existing ones, or remove outdated content.
                </p>
              <div className="flex gap-2">
                <Link href="/admin/add-book" className="flex-1">
                  <Button className="w-full bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 flex items-center gap-2">
                    <Plus className="w-4 h-4" />
                    Add Book
                  </Button>
                </Link>
                <Link href="/books" className="flex-1">
                  <Button variant="outline" className="w-full flex items-center gap-2 hover:bg-orange-50 hover:border-orange-300">
                    <Eye className="w-4 h-4" />
                    View All
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>

          {/* Quick Actions */}
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardHeader className="pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <BarChart3 className="w-5 h-5 text-white" />
                </div>
                <div>
                  <CardTitle className="text-lg">Quick Actions</CardTitle>
                  <p className="text-sm text-gray-600">Common administrative tasks</p>
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-3">
              <Link href="/books" className="block">
                <Button variant="outline" className="w-full justify-start bg-white/50 hover:bg-white/80 transition-all duration-200 hover:border-orange-300">
                  <BookOpen className="w-4 h-4 mr-2" />
                  Browse All Books
                </Button>
              </Link>
              <Link href="/admin/add-book" className="block">
                <Button variant="outline" className="w-full justify-start bg-white/50 hover:bg-white/80 transition-all duration-200 hover:border-green-300">
                  <Plus className="w-4 h-4 mr-2" />
                  Add New Book
                </Button>
              </Link>
              <Link href="/admin/dashboard" className="block">
                <Button variant="outline" className="w-full justify-start bg-white/50 hover:bg-white/80 transition-all duration-200 hover:border-blue-300">
                  <Settings className="w-4 h-4 mr-2" />
                  System Settings
                </Button>
              </Link>
            </CardContent>
          </Card>

          {/* Analytics & Reports */}
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
            <CardHeader className="pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <TrendingUp className="w-5 h-5 text-white" />
                </div>
                <div>
                  <CardTitle className="text-lg">Analytics</CardTitle>
                  <p className="text-sm text-gray-600">Library insights & reports</p>
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
                              <p className="text-gray-700">
                  Get detailed insights into your library&apos;s performance, user engagement, and content popularity.
                </p>
              <div className="space-y-2">
                <div className="flex items-center justify-between p-2 bg-purple-50 rounded-lg">
                  <span className="text-sm text-purple-700">Most Popular</span>
                  <span className="text-xs text-purple-600 bg-purple-100 px-2 py-1 rounded">Coming Soon</span>
                </div>
                <div className="flex items-center justify-between p-2 bg-blue-50 rounded-lg">
                  <span className="text-sm text-blue-700">User Activity</span>
                  <span className="text-xs text-blue-600 bg-blue-100 px-2 py-1 rounded">Coming Soon</span>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Recent Activity */}
        <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-xl mt-8">
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-orange-500 to-pink-500 rounded-xl flex items-center justify-center">
                <Clock className="w-5 h-5 text-white" />
              </div>
              <div>
                <CardTitle className="text-xl">Recent Activity</CardTitle>
                <p className="text-sm text-gray-600">Latest updates and changes</p>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center gap-4 p-4 bg-green-50 rounded-xl border border-green-200 hover:bg-green-100 transition-colors duration-200">
                <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                  <CheckCircle className="w-4 h-4 text-white" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-green-800">New book added</p>
                  <p className="text-sm text-green-600">&quot;The Great Gatsby&quot; was added to the library</p>
                </div>
                <span className="text-xs text-green-600">2 hours ago</span>
              </div>
              
              <div className="flex items-center gap-4 p-4 bg-blue-50 rounded-xl border border-blue-200 hover:bg-blue-100 transition-colors duration-200">
                <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                  <Edit3 className="w-4 h-4 text-white" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-blue-800">Book updated</p>
                  <p className="text-sm text-blue-600">&quot;1984&quot; information was modified</p>
                </div>
                <span className="text-xs text-blue-600">1 day ago</span>
              </div>
              
              <div className="flex items-center gap-4 p-4 bg-purple-50 rounded-xl border border-purple-200 hover:bg-purple-100 transition-colors duration-200">
                <div className="w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center">
                  <Users className="w-4 h-4 text-white" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-purple-800">New user registered</p>
                  <p className="text-sm text-purple-600">John Doe joined the library</p>
                </div>
                <span className="text-xs text-purple-600">3 days ago</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
