import api from "./api";

const bookService = {
  getAllBooks: async () => {
    try {
      const response = await api.get("/books");
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to fetch books",
      };
    }
  },

  getBookById: async (id) => {
    try {
      const response = await api.get(`/books/${id}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to fetch book",
      };
    }
  },

  addBook: async (bookData, pdfFile) => {
    try {
      const formData = new FormData();

      formData.append("book", JSON.stringify(bookData));

      formData.append("pdf", pdfFile);

      const response = await api.post("/books", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to add book",
      };
    }
  },

  updateBook: async (id, updates) => {
    try {
      const response = await api.put(`/books/update/${id}`, updates);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to update book",
      };
    }
  },

  deleteBook: async (id) => {
    try {
      const response = await api.delete(`/books/${id}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to delete book",
      };
    }
  },

  searchBooks: async (query) => {
    try {
      const response = await api.get(`/books/search?query=${query}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to search books",
      };
    }
  },
};

export default bookService;
