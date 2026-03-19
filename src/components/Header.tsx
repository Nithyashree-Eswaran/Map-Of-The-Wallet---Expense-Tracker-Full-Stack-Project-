
import React from "react";

const Header = () => {
  return (
    <header className="bg-white shadow-sm">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <div className="flex items-center">
          <h1 className="text-xl font-bold text-gray-800">ExpenseTracker</h1>
        </div>
        <div>
          <button className="bg-primary text-primary-foreground px-4 py-2 rounded-md text-sm font-medium">
            Login
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
