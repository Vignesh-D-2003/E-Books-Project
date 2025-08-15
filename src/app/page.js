import Image from "next/image";
import Link from "next/link";



export default function Home() {
  const cats = [
    "All Books", "Fiction", "Non-Fiction", "Mystery & Thriller", "Romance",
    "Science Fiction", "Fantasy", "Biography", "History", "Self-Help", "Business"
  ];

  const books = Array.from({ length: 20 }, (_, i) => ({
    title: `Book ${i + 1}`,
    author: `Author ${i + 1}`,
    price: `FREE`,
    img: `https://picsum.photos/200/300?random=${i}`,
    genre: cats[(i % cats.length) || 1],
    rating: (4 + Math.random()).toFixed(1),
    pages: 300 + Math.floor(Math.random() * 200)
}));


  return (
    <div style={{ fontFamily: "Arial, sans-serif", padding: "20px", background: "#a7c3dfff" }}>
      {/* HEADER */}


      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
        <div style={{ fontSize: "22px", fontWeight: "bold", color: "#ff5722", display: "flex", alignItems: "center" }}>📚 E-Books</div>
        <input placeholder="Search books, authors, or categories..."
          style={{ flex: 1, maxWidth: "500px", margin: "0 20px", padding: "10px", borderRadius: "20px", border: "1px solid #ccc", color: "#180505ff" }} />
        <div>
          <Link href="/login">
            <button style={{ padding: "8px 15px", border: "1px solid #1583e3ff", background: "#0979c3ff", borderRadius: "5px", marginRight: "8px", cursor: "pointer" }}>Login</button>
          </Link>
          <Link href="/signup">
            <button style={{ padding: "8px 15px", background: "#ff5722", color: "#fff", border: "none", borderRadius: "5px", cursor: "pointer" }}>Sign Up</button>
          </Link>
        </div>
      </header>

      {/* HERO */}
      <div style={{ textAlign: "center", marginBottom: "20px" }}>
        <h1 style={{ margin: "0", fontSize: "28px", color: "#180505ff" }}>Discover Your Next Great Read</h1>
        <p style={{ color: "#180505ff" }}>Explore thousands of ebooks across all genres. Find your perfect book and start reading instantly.</p>
      </div>

      {/* CATEGORY BUTTONS */}
      <div style={{ textAlign: "center", marginBottom: "20px" }}>
        {cats.map(c =>
          <button key={c} style={{
            margin: "5px",
            padding: "8px 15px",
            borderRadius: "20px",
            border: "none",
            background: c === "All Books" ? "#ff5722" : "#e0e0e0",
            color: c === "All Books" ? "#fff" : "#333",
            cursor: "pointer"
          }}>{c}</button>
        )}
      </div>

      {/* BOOK GRID */}
      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fill,minmax(180px,1fr))",
        gap: "20px",
        maxWidth: "1200px",
        margin: "0 auto"
      }}>
        {books.map(b => (
          <div key={b.title} style={{
            background: "#fff",
            borderRadius: "8px",
            padding: "10px",
            boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
            display: "flex",
            flexDirection: "column"
          }}>
            <img src={b.img} alt={b.title} style={{ width: "100%", borderRadius: "5px", marginBottom: "10px" }} />
            <span style={{ fontSize: "12px", color: "#ff5722", fontWeight: "bold" }}>{b.genre}</span>
            <h3 style={{ fontSize: "16px", margin: "5px 0" }}>{b.title}</h3>
            <span style={{ color: "#777", fontSize: "14px" }}>{b.author}</span>
            <span style={{ fontSize: "14px", margin: "5px 0" }}>⭐ {b.rating} • {b.pages} pages</span>
            <span style={{ color: "#ff5722", fontWeight: "bold", marginBottom: "8px" }}>{b.price}</span>
            <button style={{
              background: "#ff5722",
              color: "#fff",
              border: "none",
              padding: "8px",
              borderRadius: "5px",
              cursor: "pointer",
              marginTop: "auto"
            }}>Download</button>
          </div>
        ))}
      </div>
    </div>
  );
}
