import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom'
import './App.css'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ProtectedRoute from './components/ProtectedRoute'
import { useAuth } from './hooks/useAuth'
import AdminUsersPage from './pages/AdminUsersPage'
import AdminCreateUserPage from './pages/AdminCreateUserPage'
import AdminEditUserPage from './pages/AdminEditUserPage'
import AdminCustomersPage from './pages/AdminCustomersPage'
import { AdminRoute } from './components/ProtectedRoute'
import CustomerProfilePage from './pages/CustomerProfilePage'
import UsageHistoryPage from './pages/UsageHistoryPage'
import InvoicesPage from './pages/InvoicesPage'
import InvoiceDetailsPage from './pages/InvoiceDetailsPage'
import PaymentsPage from './pages/PaymentsPage'
import AdminCustomerServicesPage from './pages/AdminCustomerServicesPage'

function App() {
  return (
    <Router>
      <div className="app">
        <Header />

        <nav className="app-nav">
          <Navigation />
        </nav>

        <main className="app-main">
          <Routes>
             <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <DashboardPage />
                </ProtectedRoute>
              }
            />
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
           
            <Route
              path="/admin/users"
              element={
                <AdminRoute>
                  <AdminUsersPage />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/customers"
              element={
                <AdminRoute>
                  <AdminCustomersPage />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/users/new"
              element={
                <AdminRoute>
                  <AdminCreateUserPage />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/users/:id/edit"
              element={
                <AdminRoute>
                  <AdminEditUserPage />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/customers/:id/services"
              element={
                <AdminRoute>
                  <AdminCustomerServicesPage />
                </AdminRoute>
              }
            />
            <Route
              path="/customers/:id"
              element={
                <ProtectedRoute>
                  <CustomerProfilePage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/usage"
              element={
                <ProtectedRoute>
                  <UsageHistoryPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/invoices"
              element={
                <ProtectedRoute>
                  <InvoicesPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/invoices/:id"
              element={
                <ProtectedRoute>
                  <InvoiceDetailsPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/payments"
              element={
                <ProtectedRoute>
                  <PaymentsPage />
                </ProtectedRoute>
              }
            />
          </Routes>
        </main>

        <footer className="app-footer">
          <p>&copy; 2025 ABC Telecom. All rights reserved.</p>
        </footer>
      </div>
    </Router>
  )
}

function Header() {
  const { isAuthenticated, user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <header className="app-header">
      <h1>Postpaid Billing System</h1>
      <p>ABC Telecom Customer Portal</p>
      {isAuthenticated && (
        <div className="user-info">
          <span>Welcome, {user?.username}</span>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      )}
    </header>
  )
}

function Navigation() {
  const { isAuthenticated, user } = useAuth()

  return (
   
    <ul>
      <li><a href="/">Home</a></li>
      {!isAuthenticated ? (
        <>
          <li><a href="/login">Login</a></li>
          <li><a href="/register">Register</a></li>
        </>
      ) : (
        <>
          <li><a href="/dashboard">Dashboard</a></li>
          {user?.role?.toUpperCase() === 'ADMIN' ? (
            <>
              <li><a href="/admin/users">Manage Customers</a></li>
            </>
          ) : (
             <li><a href="/customers/">Customer</a></li>
          )}
         
          <li><a href="/services">Services</a></li>
          <li><a href="/billing">Billing</a></li>
        </>
      )}
    </ul>
  )
}

function HomePage() {
  const { isAuthenticated, user } = useAuth()

  return (
    <div className="page">
      <h2>Welcome to Postpaid Billing System</h2>
      {isAuthenticated ? (
        <div>
          <p>Hello, {user?.username}! You are logged in.</p>
          <ul>
            <li><a href="/dashboard">Dashboard</a></li>
            {user?.role?.toUpperCase() === 'ADMIN' && <li><a href="/admin/users">Manage Users</a></li>}
            <li><a href="/customers/me">Customers</a></li>
            <li><a href="/usage">Usage</a></li>
            <li><a href="/invoices">Invoices</a></li>
            <li><a href="/payments">Payments</a></li>
          </ul>
        </div>
      ) : (
        <div>
          <p>Backend API available at <code>http://localhost:8080</code></p>
          <p>Frontend running at <code>http://localhost:3000</code></p>
          <p><a href="/login">Login</a> or <a href="/register">Register</a> to continue</p>
        </div>
      )}
    </div>
  )
}

export default App
