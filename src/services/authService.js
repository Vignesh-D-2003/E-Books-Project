import api from "./api";

const authService = {
  register: async (userData) => {
    try {
      const response = await api.post("/users/register", userData);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Registration failed",
      };
    }
  },

  login: async (credentials) => {
    try {
      const response = await api.post("/users/login", credentials);
      // Store JWT token in localStorage
      if (response.data && response.data.jwtToken) {
        localStorage.setItem("token", response.data.jwtToken);
        localStorage.setItem("user", JSON.stringify(response.data.user));
      }
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Login failed",
      };
    }
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },

  getCurrentUser: () => {
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem("token");
  },

  isAdmin: () => {
    const user = localStorage.getItem("user");
    if (!user) return false;
    return JSON.parse(user).is_admin === true;
  },
};

export default authService;
