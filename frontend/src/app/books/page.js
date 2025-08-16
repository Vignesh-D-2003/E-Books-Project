import { BookCard } from "@/components/BookCard";

// Mock data for now, this will come from an API
const books = [
  { id: 1, title: "The Great Gatsby", author: "F. Scott Fitzgerald", imageUrl: "/placeholder.svg", admin: true },
  { id: 2, title: "To Kill a Mockingbird", author: "Harper Lee", imageUrl: "/placeholder.svg", admin: true },
  { id: 3, title: "1984", author: "George Orwell", imageUrl: "/placeholder.svg", admin: true },
  { id: 4, title: "The Catcher in the Rye", author: "J.D. Salinger", imageUrl: "/placeholder.svg", admin: false },
];

export default function BooksPage() {
  // This will be replaced with actual role-based logic
  const isAdmin = true;

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8">All Books</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        {books.map((book) => (
          <BookCard key={book.id} book={book} isAdmin={isAdmin} />
        ))}
      </div>
    </div>
  );
}