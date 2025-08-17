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
  Upload
} from "lucide-react";

export default function AdminDashboard() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [searchResults, setSearchResults] = useState([]);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [searching, setSearching] = useState(false);
  const [stats, setStats] = useState({
    totalBooks: 0,
    totalCategories: 0,
    recentBooks: 0,
    totalUsers: 0
  });
  
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
      <div className="bg-white/80 backdrop-blur-sm border-b border-white/20 shadow-sm">
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
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
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
                <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center">
                  <BookOpen className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
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
                <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-green-600 rounded-xl flex items-center justify-center">
                  <FileText className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
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
                <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
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
                <div className="w-12 h-12 bg-gradient-to-br from-orange-500 to-pink-500 rounded-xl flex items-center justify-center">
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
            
            {/* Search Results */}
            {showSearchResults && searchResults.length > 0 && (
              <div className="space-y-3">
                <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-green-600" />
                  Quick Results ({searchResults.length})
                </h3>
                <div className="grid gap-3">
                  {searchResults.map((book) => (
                    <div key={book.book_id} className="flex justify-between items-center p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl border border-gray-200 hover:shadow-md transition-all duration-200">
                      <div className="flex-1">
                        <p className="font-semibold text-gray-900">{book.title}</p>
                        <p className="text-sm text-gray-600">by {book.author}</p>
                        {book.category_name && (
                          <span className="inline-block mt-1 px-2 py-1 bg-orange-100 text-orange-700 text-xs rounded-full">
                            {book.category_name}
                          </span>
                        )}
                      </div>
                      <div className="flex gap-2">
                        <Link href={`/admin/edit-book/${book.book_id}`}>
                          <Button size="sm" variant="outline" className="flex items-center gap-1">
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
                      <Button variant="outline" size="sm" className="flex items-center gap-2 mx-auto">
                        View all search results
                        <ArrowRight className="w-4 h-4" />
                      </Button>
                    </Link>
                  </div>
                )}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Action Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Manage Books */}
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
            <CardHeader className="pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-green-500 to-green-600 rounded-xl flex items-center justify-center">
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
                Complete control over your library's book collection. Add new titles, update existing ones, or remove outdated content.
              </p>
              <div className="flex gap-2">
                <Link href="/admin/add-book" className="flex-1">
                  <Button className="w-full bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 flex items-center gap-2">
                    <Plus className="w-4 h-4" />
                    Add Book
                  </Button>
                </Link>
                <Link href="/books" className="flex-1">
                  <Button variant="outline" className="w-full flex items-center gap-2">
                    <Eye className="w-4 h-4" />
                    View All
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>

          {/* Quick Actions */}
          <Card className="bg-white/80 backdrop-blur-sm border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
            <CardHeader className="pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center">
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
                <Button variant="outline" className="w-full justify-start bg-white/50 hover:bg-white/80 transition-all duration-200">
                  <BookOpen className="w-4 h-4 mr-2" />
                  Browse All Books
                </Button>
              </Link>
              <Link href="/admin/add-book" className="block">
                <Button variant="outline" className="w-full justify-start bg-white/50 hover:bg-white/80 transition-all duration-200">
                  <Plus className="w-4 h-4 mr-2" />
                  Add New Book
                </Button>
              </Link>
              <Link href="/admin/dashboard" className="block">
                <Button variant="outline" className="w-full justify-start bg-white/50 hover:bg-white/80 transition-all duration-200">
                  <Settings className="w-4 h-4 mr-2" />
                  System Settings
                </Button>
              </Link>
            </CardContent>
          </Card>

          {/* Analytics & Reports */}
          
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
              <div className="flex items-center gap-4 p-4 bg-green-50 rounded-xl border border-green-200">
                <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                  <CheckCircle className="w-4 h-4 text-white" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-green-800">New book added</p>
                  <p className="text-sm text-green-600">"The Great Gatsby" was added to the library</p>
                </div>
                <span className="text-xs text-green-600">2 hours ago</span>
              </div>
              
              <div className="flex items-center gap-4 p-4 bg-blue-50 rounded-xl border border-blue-200">
                <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                  <Edit3 className="w-4 h-4 text-white" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-blue-800">Book updated</p>
                  <p className="text-sm text-blue-600">"1984" information was modified</p>
                </div>
                <span className="text-xs text-blue-600">1 day ago</span>
              </div>
              
              <div className="flex items-center gap-4 p-4 bg-purple-50 rounded-xl border border-purple-200">
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
