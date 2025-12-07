import React from 'react'
import { useAuth } from '../hooks/useAuth'

export default function DashboardPage() {
  const { user } = useAuth()

  if (!user) {
    return <div className="page"><p>Loading user data...</p></div>
  }

  return (
    <div className="page">
      <h2>Welcome, {user.username}!</h2>
      
      <div className="user-profile">
        <h3>Your Profile</h3>
        <div className="profile-card">
          <p><strong>Username:</strong> {user.username}</p>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Role:</strong> {user.role}</p>
          {user.createdAt && <p><strong>Member Since:</strong> {new Date(user.createdAt).toLocaleDateString()}</p>}
        </div>
      </div>

      <div className="navigation-links">
        <h3>Quick Links</h3>
        <ul>
          <li><a href="/customers">View Customers</a></li>
          <li><a href="/services">View Services</a></li>
          <li><a href="/billing">View Billing</a></li>
          <li><a href="/payments">View Payments</a></li>
        </ul>
      </div>
    </div>
  )
}
