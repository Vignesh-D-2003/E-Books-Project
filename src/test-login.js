async function testLogin() {
  const credentials = {
    email: "test@example.com",
    password: "password",
  };

  try {
    const response = await fetch("http://localhost:8080/users/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    });

    const data = await response.json();
    console.log("Login response:", data);

    console.log("Has jwtToken:", !!data.jwtToken);
    console.log("Has token:", !!data.token);
    console.log("Has user:", !!data.user);
    if (data.user) {
      console.log("User is_admin:", data.user.is_admin);
      console.log("User role:", data.user.role);
    }

    return data;
  } catch (error) {
    console.error("Login test error:", error);
  }
}

testLogin().then((data) => {
  console.log(
    "Test complete. Save the token in localStorage to test redirection:"
  );
  console.log(`localStorage.setItem("token", "${data.jwtToken || ""}")`);
  console.log(
    `localStorage.setItem("user", '${JSON.stringify(data.user || {})}')`
  );
});
