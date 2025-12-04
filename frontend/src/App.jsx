import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'

function App() {
  return (
    <Router>
      <div className="app">
        <header className="app-header">
          <h1>Postpaid Billing System</h1>
          <p>ABC Telecom Customer Portal</p>
        </header>
        
        <nav className="app-nav">
          <ul>
            <li><a href="/">Home</a></li>
            <li><a href="/login">Login</a></li>
            <li><a href="/register">Register</a></li>
          </ul>
        </nav>

        <main className="app-main">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Routes>
        </main>

        <footer className="app-footer">
          <p>&copy; 2025 ABC Telecom. All rights reserved.</p>
        </footer>
      </div>
    </Router>
  )
}

function HomePage() {
  return (
    <div className="page">
      <h2>Welcome to Postpaid Billing System</h2>
      <p>Step 0: Project Setup Complete</p>
      <p>Backend API available at <code>http://localhost:8080</code></p>
      <p>Frontend running at <code>http://localhost:3000</code></p>
    </div>
  )
}

function LoginPage() {
  return (
    <div className="page">
      <h2>Login</h2>
      <p>Login endpoint will be available in Step 1</p>
    </div>
  )
}

function RegisterPage() {
  return (
    <div className="page">
      <h2>Register</h2>
      <p>Registration endpoint will be available in Step 1</p>
    </div>
  )
}

export default App
