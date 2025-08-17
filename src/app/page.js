"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import authService from "@/services/authService";
import bookService from "@/services/bookService";
import categoryService from "@/services/categoryService";
import { 
  BookOpen, 
  Users, 
  Library, 
  TrendingUp, 
  ArrowRight, 
  Star, 
  Clock, 
  Eye,
  Search,
  Filter,
  Grid3X3,
  List,
  Download,
  CheckCircle,
  AlertCircle,
  RefreshCw,
  Settings,
  Sparkles,
  Globe,
  Shield,
  Zap
} from "lucide-react";

export default function HomePage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [mounted, setMounted] = useState(false);
  const [stats, setStats] = useState({
    totalBooks: 0,
    totalCategories: 0,
    recentBooks: 0
  });
  const [featuredBooks, setFeaturedBooks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setMounted(true);
    setIsAuthenticated(authService.isAuthenticated());
    setIsAdmin(authService.isAdmin());
    
    // Fetch stats and featured books
    const fetchData = async () => {
      try {
        // Fetch stats
        const booksResult = await bookService.getAllBooks();
        const categoriesResult = await categoryService.getAllCategories();
        
        if (booksResult.success && categoriesResult.success) {
          const recentBooks = booksResult.data.slice(0, 6); // Get 6 most recent books
          setFeaturedBooks(recentBooks);
          setStats({
            totalBooks: booksResult.data.length,
            totalCategories: categoriesResult.data.length,
            recentBooks: recentBooks.length
          });
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (!mounted) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-pink-50 to-purple-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-orange-500/10 to-pink-500/10"></div>
        <div className="relative container mx-auto px-4 py-20">
          <div className="text-center max-w-4xl mx-auto">
            <div className="flex items-center justify-center mb-6">
              <div className="w-16 h-16 bg-gradient-to-r from-orange-500 to-pink-500 rounded-2xl flex items-center justify-center mr-4">
                <BookOpen className="w-8 h-8 text-white" />
              </div>
              <h1 className="text-5xl md:text-6xl font-bold bg-gradient-to-r from-orange-600 to-pink-600 bg-clip-text text-transparent">
                E-Library
              </h1>
            </div>
            <p className="text-xl md:text-2xl text-gray-700 mb-8 leading-relaxed">
              Your gateway to a world of knowledge. Discover, read, and explore thousands of books 
              in our comprehensive digital library.
            </p>
            <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12">
            {isAuthenticated ? (
              <>
                <Link href="/books">
                    <Button size="lg" className="bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white px-8 py-3 text-lg font-semibold shadow-lg hover:shadow-xl transition-all duration-300">
                      <BookOpen className="w-5 h-5 mr-2" />
                      Browse Library
                    </Button>
                </Link>
                {isAdmin && (
                  <Link href="/admin/dashboard">
                      <Button size="lg" variant="outline" className="px-8 py-3 text-lg font-semibold border-2 hover:bg-white/80 transition-all duration-300">
                        <Settings className="w-5 h-5 mr-2" />
                      Admin Dashboard
                    </Button>
                  </Link>
                )}
              </>
            ) : (
              <>
                <Link href="/login">
                    <Button size="lg" className="bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white px-8 py-3 text-lg font-semibold shadow-lg hover:shadow-xl transition-all duration-300">
                      <Sparkles className="w-5 h-5 mr-2" />
                      Get Started
                    </Button>
                </Link>
                <Link href="/signup">
                    <Button size="lg" variant="outline" className="px-8 py-3 text-lg font-semibold border-2 hover:bg-white/80 transition-all duration-300">
                      <Users className="w-5 h-5 mr-2" />
                      Join Now
                    </Button>
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 bg-white/50 backdrop-blur-sm">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
              <CardContent className="p-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-r from-orange-500 to-pink-500 rounded-full flex items-center justify-center mx-auto mb-4">
                  <BookOpen className="w-8 h-8 text-white" />
                </div>
                <h3 className="text-3xl font-bold text-gray-900 mb-2">
                  {loading ? "..." : stats.totalBooks}
                </h3>
                <p className="text-gray-600 font-medium">Total Books</p>
              </CardContent>
            </Card>
            
            <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
              <CardContent className="p-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-r from-pink-500 to-purple-500 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Library className="w-8 h-8 text-white" />
                </div>
                <h3 className="text-3xl font-bold text-gray-900 mb-2">
                  {loading ? "..." : stats.totalCategories}
                </h3>
                <p className="text-gray-600 font-medium">Categories</p>
              </CardContent>
            </Card>
            
            <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
              <CardContent className="p-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-r from-purple-500 to-indigo-500 rounded-full flex items-center justify-center mx-auto mb-4">
                  <TrendingUp className="w-8 h-8 text-white" />
                </div>
                <h3 className="text-3xl font-bold text-gray-900 mb-2">
                  {loading ? "..." : stats.recentBooks}
                </h3>
                <p className="text-gray-600 font-medium">New Additions</p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Featured Books Section */}
      {isAuthenticated && (
        <section className="py-16">
          <div className="container mx-auto px-4">
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
                Featured Books
              </h2>
              <p className="text-lg text-gray-600 max-w-2xl mx-auto">
                Discover our latest additions and most popular reads. Start your reading journey today.
              </p>
            </div>
            
            {loading ? (
              <div className="flex justify-center items-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500"></div>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {featuredBooks.slice(0, 6).map((book) => (
                  <Card key={book.book_id} className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
                    <CardContent className="p-6">
                      <div className="flex justify-center items-center mb-4">
                        <div className="w-16 h-12 bg-gradient-to-br from-orange-100 to-pink-100 rounded-lg flex items-center justify-center border border-orange-200 group-hover:shadow-lg transition-all duration-300">
                          <BookOpen className="w-6 h-6 text-orange-600" />
                        </div>
                      </div>
                      <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2 text-center">
                        {book.title}
                      </h3>
                      <p className="text-sm text-gray-600 mb-3 text-center">
                        by {book.author}
                      </p>
                      {book.category_name && (
                        <div className="text-center mb-4">
                          <span className="inline-block px-3 py-1 bg-orange-100 text-orange-700 text-xs rounded-full font-medium">
                            {book.category_name}
                          </span>
                        </div>
                      )}
                      <div className="flex justify-center">
                        <Link href="/books">
                          <Button 
                            size="sm"
                            className="bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white"
                          >
                            <Eye className="w-4 h-4 mr-1" />
                            View Details
                          </Button>
                        </Link>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
            
            <div className="text-center">
              <Link href="/books">
                <Button size="lg" variant="outline" className="px-8 py-3 text-lg font-semibold border-2 hover:bg-white/80 transition-all duration-300">
                  View All Books
                  <ArrowRight className="w-5 h-5 ml-2" />
                </Button>
              </Link>
            </div>
          </div>
        </section>
      )}

      {/* Features Section */}
      <section className="py-16 bg-white/30 backdrop-blur-sm">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Why Choose E-Library?
            </h2>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
              Experience the future of reading with our advanced digital library platform.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
              <CardContent className="p-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-emerald-500 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Globe className="w-8 h-8 text-white" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-3">Global Access</h3>
                <p className="text-gray-600">
                  Access your favorite books from anywhere in the world, anytime you want.
                </p>
              </CardContent>
            </Card>
            
            <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
              <CardContent className="p-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Shield className="w-8 h-8 text-white" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-3">Secure Platform</h3>
                <p className="text-gray-600">
                  Your data and reading preferences are protected with enterprise-grade security.
                </p>
              </CardContent>
            </Card>
            
            <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
              <CardContent className="p-6 text-center">
                <div className="w-16 h-16 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Zap className="w-8 h-8 text-white" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-3">Lightning Fast</h3>
                <p className="text-gray-600">
                  Enjoy instant access to books with our optimized platform and fast loading times.
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <Card className="bg-gradient-to-r from-orange-500 to-pink-500 text-white text-center max-w-4xl mx-auto">
            <CardContent className="p-12">
              <h2 className="text-3xl md:text-4xl font-bold mb-4">
                Ready to Start Reading?
              </h2>
              <p className="text-xl mb-8 opacity-90">
                Join thousands of readers who have already discovered the joy of digital reading.
              </p>
              <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
                {isAuthenticated ? (
                  <Link href="/books">
                    <Button size="lg" className="bg-white text-orange-600 hover:bg-gray-100 px-8 py-3 text-lg font-semibold shadow-lg hover:shadow-xl transition-all duration-300">
                      <BookOpen className="w-5 h-5 mr-2" />
                      Explore Library
                    </Button>
                  </Link>
                ) : (
                  <>
                    <Link href="/signup">
                      <Button size="lg" className="bg-white text-orange-600 hover:bg-gray-100 px-8 py-3 text-lg font-semibold shadow-lg hover:shadow-xl transition-all duration-300">
                        <Sparkles className="w-5 h-5 mr-2" />
                        Get Started Free
                      </Button>
                    </Link>
                    <Link href="/login">
                      <Button size="lg" variant="outline" className="border-white text-white hover:bg-white hover:text-orange-600 px-8 py-3 text-lg font-semibold transition-all duration-300">
                        <Users className="w-5 h-5 mr-2" />
                        Sign In
                  </Button>
                </Link>
              </>
            )}
          </div>
        </CardContent>
      </Card>
        </div>
      </section>
    </div>
  );
}
