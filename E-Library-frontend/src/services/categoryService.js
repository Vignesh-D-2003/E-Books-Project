import api from "./api";

const categoryService = {
  getAllCategories: async () => {
    try {
      const response = await api.get("/categories");
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to fetch categories",
      };
    }
  },

  addCategory: async (categoryData) => {
    try {
      const response = await api.post("/categories", categoryData);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to add category",
      };
    }
  },
  updateCategory: async (id, updates) => {
    try {
      const response = await api.put(
        `/categories/update-category/${id}`,
        updates
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to update category",
      };
    }
  },

  deleteCategory: async (id) => {
    try {
      const response = await api.delete(`/categories/${id}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || "Failed to delete category",
      };
    }
  },
};

export default categoryService;
