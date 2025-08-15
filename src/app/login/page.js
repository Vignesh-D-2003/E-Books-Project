export default function Login() {
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
            <h1 style={{ fontSize: "24px", marginBottom: "10px", color: "#ff5722", textAlign: "center" }}>Welcome Back</h1>
            <p style={{ textAlign: "center", color: "#666", marginBottom: "20px" }}>Login to your account</p>
            <form style={{ display: "flex", flexDirection: "column", gap: "15px" }}>
              <input type="email" placeholder="Email" style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
              <input type="password" placeholder="Password" style={{ padding: "10px", borderRadius: "5px", border: "1px solid #ccc" }} />
              <button style={{ background: "#ff5722", color: "#fff", border: "none", padding: "10px", borderRadius: "5px", cursor: "pointer" }}>Login</button>
            </form>

            <p style={{ textAlign: "center", marginTop: "15px", fontSize: "14px",color:"black" }}>
              Don't have an account? <a href="/signup" style={{ color: "#ff5722" }}>Sign up</a>
            </p>

          </div>
        </div>

        {/* RIGHT IMAGE */}
        <div style={{ flex: 1, display: "none", background: "#eee", overflow: "hidden" }} className="image-section">
          <img src="https://picsum.photos/800/900" alt="Books" style={{ width: "100%", height: "100%", objectFit: "cover" }} />
        </div>
      </div>
    </>
  );
}
