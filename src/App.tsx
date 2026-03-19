import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from "./components/ui/toaster";
import { Toaster as Sonner } from './components/ui/sonner';
import { TooltipProvider } from './components/ui/tooltip';
import { ExpenseProvider } from './context/ExpenseContext';
import Auth from './pages/Auth';
import { ThemeProvider } from './context/ThemeContext';
import AddExpense from "./pages/AddExpense";
import ExpenseForm from './pages/AddExpense';
import TestInput from './pages/TestInput';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Expenses from './pages/Expenses';
import Statistics from './pages/Statistics';
import Profile from './pages/Profile';
import Settings from './pages/Settings';
import NotFound from './pages/NotFound';


const queryClient = new QueryClient();

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true); // <-- Loading state

  useEffect(() => {
    const token = localStorage.getItem('auth-token');
    if (token) {
      setIsAuthenticated(true);
    }
    setCheckingAuth(false); // finished checking auth token
  }, []);

  const handleLogin = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('auth-token');
    setIsAuthenticated(false);
  };

  if (checkingAuth) {
    // Show something while we check auth — could be a spinner too!
    return (
      <div style={{ display: 'flex', justifyContent: 'center', marginTop: '50px', fontSize: '18px', color: '#666' }}>
        Loading...
      </div>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ExpenseProvider>
          <TooltipProvider>
            <Toaster />
            <Sonner />
            <Routes>
              {/* Redirect root to Auth or Dashboard */}
              <Route path="/" element={
                isAuthenticated ? 
                  <Navigate to="/dashboard" replace /> :
                  <Navigate to="/auth" replace />
              } />

              {/* Auth route */}
              <Route
                path="/auth"
                element={
                  isAuthenticated ? (
                    <Navigate to="/dashboard" replace />
                  ) : (
                    <Auth onLogin={handleLogin} />
                  )
                }
              />

               <Route path="/" element={isAuthenticated ? <Layout onLogout={handleLogout} /> : <Navigate to="/auth" replace />}>
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/add-expense" element={<AddExpense />} />
                  <Route path="/expenses" element={<Expenses />} />
                  <Route path="/statistics" element={<Statistics />} />
                  <Route path="/profile" element={<Profile />} />
                  <Route path="/settings" element={<Settings />} />
                  <Route path="/test" element={<TestInput />} />
                </Route>
                
                {/* Catch-all Route */}
                <Route path="*" element={<NotFound />} />
              </Routes>

            </TooltipProvider>
          </ExpenseProvider>
        </BrowserRouter>
        <ThemeProvider>
          <Toaster />
          <Sonner />
      </ThemeProvider>
    </QueryClientProvider>
  );
};

export default App;
