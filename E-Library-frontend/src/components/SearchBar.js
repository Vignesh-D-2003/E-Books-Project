import { useState, useRef } from "react";
import { Search, Filter, X } from "lucide-react";

const SearchBar = ({ onSearch, placeholder = "Search...", className = "", showCategoryFilter = false, categories = [] }) => {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const searchInputRef = useRef(null);

  const handleSearchChange = (e) => {
    const newQuery = e.target.value;
    setSearchQuery(newQuery);
    onSearch(newQuery, selectedCategory);
  };

  const handleCategoryChange = (e) => {
    const newCategory = e.target.value;
    setSelectedCategory(newCategory);
    onSearch(searchQuery, newCategory);
  };

  const clearFilters = () => {
    setSearchQuery("");
    setSelectedCategory("");
    onSearch("", "");
    searchInputRef.current?.focus();
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Escape') {
      clearFilters();
    }
  };

  return (
    <div className={`mb-8 ${className}`}>
      <div className="flex flex-col sm:flex-row gap-4">
        {/* Search Input */}
        <div className="relative flex-1 max-w-md">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-gray-400" />
          </div>
          <input
            ref={searchInputRef}
            type="text"
            placeholder={placeholder}
            value={searchQuery}
            onChange={handleSearchChange}
            onKeyDown={handleKeyDown}
            className="block w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            aria-label="Search input"
          />
          {searchQuery && (
            <button
              onClick={() => {
                setSearchQuery("");
                onSearch("", selectedCategory);
              }}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
              aria-label="Clear search"
            >
              <X className="h-4 w-4" />
            </button>
          )}
        </div>

        {/* Category Filter */}
        {showCategoryFilter && categories.length > 0 && (
          <div className="relative flex-1 max-w-md">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Filter className="h-5 w-5 text-gray-400" />
            </div>
            <select
              value={selectedCategory}
              onChange={handleCategoryChange}
              className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              aria-label="Filter by category"
            >
              <option value="">All Categories</option>
              {categories.map((category) => (
                <option key={category.category_id} value={category.category_id}>
                  {category.category_name}
                </option>
              ))}
            </select>
          </div>
        )}

        {/* Clear Filters Button */}
        {(searchQuery || selectedCategory) && (
          <button
            onClick={clearFilters}
            className="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors flex items-center gap-2"
            aria-label="Clear all filters"
          >
            <X className="h-4 w-4" />
            Clear Filters
          </button>
        )}
      </div>

      {/* Search Tips */}
      {!searchQuery && !selectedCategory && (
        <p className="mt-2 text-sm text-gray-500">
          💡 Tip: Press Esc to clear search, or use category filter to narrow results
        </p>
      )}
    </div>
  );
};

export default SearchBar;
