export default function Signup() {
  return (
    <>
      <style>{`
        input::placeholder {
          color: lightgray;
        }
      `}</style>

      <div style={{ fontFamily: "Arial, sans-serif", display: "flex", minHeight: "100vh" }}>
        {/* LEFT FORM */}
        <div style={{ flex: 1, display: "flex", justifyContent: "center", alignItems: "center", background: "#fff8f0" }}>
          <div style={{ background: "#fff", padding: "40px", borderRadius: "10px", boxShadow: "0 2px 8px rgba(0,0,0,0.1)", width: "350px" }}>
            <h1 style={{ fontSize: "24px", marginBottom: "10px", color: "#ff5722", textAlign: "center" }}>Create Account</h1>
            <p style={{ textAlign: "center", color: "#666", marginBottom: "20px" }}>Join and start reading today</p>
            <form style={{ display: "flex", flexDirection: "column", gap: "15px" }}>
              <input type="text" placeholder="Full Name" style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
              <input type="email" placeholder="Email" style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
              <input type="password" placeholder="Password" style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
              <input type="password" placeholder="Confirm Password" style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
              <button style={{ background: "#ff5722", color: "#fff", border: "none", padding: "10px", borderRadius: "5px", cursor: "pointer" }}>Sign Up</button>
            </form>
            <p style={{ textAlign: "center", marginTop: "15px", fontSize: "14px" }}>
              Already have an account? <a href="/login" style={{ color: "#ff5722" }}>Login</a>
            </p>
          </div>
        </div>

        {/* RIGHT IMAGE */}
        <div style={{ flex: 1, display: "none", background: "#eee", overflow: "hidden" }} className="image-section">
          <img src="https://picsum.photos/800/901" alt="Reading" style={{ width: "100%", height: "100%", objectFit: "cover" }} />
        </div>
      </div>
    </>
  );
}
