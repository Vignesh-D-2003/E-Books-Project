"use client";

import Link from "next/link";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import bookService from "@/services/bookService";
import { 
  BookOpen, 
  Edit3, 
  Trash2, 
  Eye, 
  Calendar,
  User,
  FileText
} from "lucide-react";

export function BookCard({ book, isAdmin, viewMode = "grid" }) {
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleted, setDeleted] = useState(false);

  const imageUrl = "/book.svg";

  const handleReadBook = () => {
    if (book.file_url) {
      window.open(book.file_url, "_blank");
    }
  };

  const handleDelete = async () => {
    if (window.confirm(`Are you sure you want to delete "${book.title}"?`)) {
      setIsDeleting(true);
      try {
        const result = await bookService.deleteBook(book.book_id);
        if (result.success) {
          setDeleted(true);
        } else {
          alert(result.message || "Failed to delete book");
        }
      } catch (error) {
        console.error("Error deleting book:", error);
        alert("An error occurred while deleting the book");
      } finally {
        setIsDeleting(false);
      }
    }
  };

  if (deleted) {
    return null;
  }

  // List view
  if (viewMode === "list") {
    return (
      <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300">
        <CardContent className="p-6">
          <div className="flex items-center gap-6">
            {/* Book Cover */}
            <div className="flex-shrink-0">
              <div className="w-20 h-28 bg-gradient-to-br from-orange-100 to-pink-100 rounded-xl flex items-center justify-center border border-orange-200">
                <BookOpen className="w-8 h-8 text-orange-600" />
              </div>
            </div>

            {/* Book Info */}
            <div className="flex-1 min-w-0">
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1">
                  <h3 className="text-xl font-semibold text-gray-900 mb-2 line-clamp-1">
                    {book.title}
                  </h3>
                  <div className="flex items-center gap-4 text-sm text-gray-600 mb-3">
                    <div className="flex items-center gap-1">
                      <User className="w-4 h-4" />
                      <span>{book.author}</span>
                    </div>
                    {book.category_name && (
                      <div className="flex items-center gap-1">
                        <FileText className="w-4 h-4" />
                        <span>{book.category_name}</span>
                      </div>
                    )}
                    <div className="flex items-center gap-1">
                      <Calendar className="w-4 h-4" />
                      <span>Available</span>
                    </div>
                  </div>
                  <p className="text-gray-600 text-sm line-clamp-2">
                    Discover the fascinating world of {book.title} by {book.author}. 
                    This book offers an engaging reading experience that will captivate your imagination.
                  </p>
                </div>

                {/* Actions */}
                <div className="flex items-center gap-3 flex-shrink-0">
                  <Button
                    onClick={handleReadBook}
                    className="bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white flex items-center gap-2"
                  >
                    <Eye className="w-4 h-4" />
                    Read
                  </Button>
                  
                  {isAdmin && (
                    <>
                      <Link href={`/admin/edit-book/${book.book_id}`}>
                        <Button variant="outline" size="sm" className="flex items-center gap-1">
                          <Edit3 className="w-4 h-4" />
                          Edit
                        </Button>
                      </Link>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={handleDelete}
                        disabled={isDeleting}
                        className="flex items-center gap-1"
                      >
                        <Trash2 className="w-4 h-4" />
                        {isDeleting ? "Deleting..." : "Delete"}
                      </Button>
                    </>
                  )}
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  // Grid view (default)
  return (
    <Card className="bg-white/80 backdrop-blur-sm border border-white/20 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 group">
      <CardContent className="p-6">
        {/* Book Cover */}
        <div className="flex justify-center items-center mb-6">
          <div className="w-24 h-32 bg-gradient-to-br from-orange-100 to-pink-100 rounded-xl flex items-center justify-center border border-orange-200 group-hover:shadow-lg transition-all duration-300">
            <BookOpen className="w-12 h-12 text-orange-600" />
          </div>
        </div>

        {/* Book Info */}
        <div className="text-center mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2 leading-tight">
            {book.title}
          </h3>
          <p className="text-sm text-gray-600 mb-3 flex items-center justify-center gap-1">
            <User className="w-3 h-3" />
            {book.author}
          </p>
          {book.category_name && (
            <span className="inline-block px-3 py-1 bg-orange-100 text-orange-700 text-xs rounded-full font-medium">
              {book.category_name}
            </span>
          )}
        </div>

        {/* Actions */}
        <div className="space-y-3">
          <Button 
            onClick={handleReadBook}
            className="w-full bg-gradient-to-r from-orange-500 to-pink-500 hover:from-orange-600 hover:to-pink-600 text-white flex items-center justify-center gap-2 transition-all duration-200"
          >
            <Eye className="w-4 h-4" />
            Read Book
          </Button>
          
          {isAdmin && (
            <div className="flex gap-2">
              <Link href={`/admin/edit-book/${book.book_id}`} className="flex-1">
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="w-full flex items-center justify-center gap-1 bg-white/50 hover:bg-white/80 transition-all duration-200"
                >
                  <Edit3 className="w-3 h-3" />
                  Edit
                </Button>
              </Link>
              <Button
                variant="destructive"
                size="sm"
                onClick={handleDelete}
                disabled={isDeleting}
                className="flex-1 flex items-center justify-center gap-1"
              >
                <Trash2 className="w-3 h-3" />
                {isDeleting ? "..." : "Delete"}
              </Button>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
