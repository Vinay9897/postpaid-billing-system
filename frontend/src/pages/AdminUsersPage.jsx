import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { listUsers, deleteUser } from '../services/adminService'
import { useAuth } from '../hooks/useAuth'

export default function AdminUsersPage() {
  const [users, setUsers] = useState([])
  const [filteredUsers, setFilteredUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [searchTerm, setSearchTerm] = useState('')
  const [deleteConfirm, setDeleteConfirm] = useState(null)
  const navigate = useNavigate()
  const { user: currentUser } = useAuth()

  useEffect(() => {
    fetchUsers()
  }, [])

  const fetchUsers = async () => {
    try {
      setLoading(true)
      setError('')
      const data = await listUsers()
      setUsers(data)
      setFilteredUsers(data)
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to load users')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (e) => {
    const term = e.target.value.toLowerCase()
    setSearchTerm(term)
    const filtered = users.filter(
      (u) =>
        u.username.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term)
    )
    setFilteredUsers(filtered)
  }

  const handleDelete = async (userId, username) => {
    if (currentUser?.userId === userId) {
      setError('Cannot delete your own account')
      setDeleteConfirm(null)
      return
    }
    try {
      await deleteUser(userId)
      setUsers(users.filter((u) => u.userId !== userId))
      setFilteredUsers(filteredUsers.filter((u) => u.userId !== userId))
      setDeleteConfirm(null)
      setError('')
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to delete user')
      setDeleteConfirm(null)
    }
  }

  const getRoleBadgeClass = (role) => {
    const upperRole = role?.toUpperCase()
    switch (upperRole) {
      case 'ADMIN':
        return 'badge-admin'
      case 'SUPPORT':
        return 'badge-support'
      case 'CUSTOMER':
        return 'badge-customer'
      default:
        return 'badge-default'
    }
  }

  if (loading) {
    return <div className="page"><p>Loading users...</p></div>
  }

  return (
    <div className="page admin-users-page">
      <div className="admin-header">
        <h2>User Management</h2>
        <button className="btn-primary" onClick={() => navigate('/admin/users/new')}>
          + New User
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="search-box">
        <input
          type="text"
          placeholder="Search by username or email..."
          value={searchTerm}
          onChange={handleSearch}
          className="search-input"
        />
      </div>

      {filteredUsers.length === 0 ? (
        <p className="no-data">No users found</p>
      ) : (
        <div className="table-container">
          <table className="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Created At</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((u) => (
                <tr key={u.userId}>
                  <td>{u.userId}</td>
                  <td>{u.username}</td>
                  <td>{u.email}</td>
                  <td>
                    <span className={`role-badge ${getRoleBadgeClass(u.role)}`}>
                      {u.role?.toUpperCase()}
                    </span>
                  </td>
                  <td>{new Date(u.createdAt).toLocaleDateString()}</td>
                  <td className="actions-cell">
                    <button
                      className="btn-small btn-edit"
                      onClick={() => navigate(`/admin/users/${u.userId}/edit`)}
                      title="Edit user"
                    >
                      Edit
                    </button>
                    <button
                      className="btn-small btn-delete"
                      onClick={() => setDeleteConfirm(u)}
                      title="Delete user"
                      disabled={currentUser?.userId === u.userId}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {deleteConfirm && (
        <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
          <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Confirm Delete</h3>
              <button
                className="modal-close"
                onClick={() => setDeleteConfirm(null)}
              >
                ✕
              </button>
            </div>
            <div className="modal-body">
              <p>
                Are you sure you want to delete user <strong>{deleteConfirm.username}</strong>?
              </p>
              <p className="text-muted">This action cannot be undone.</p>
            </div>
            <div className="modal-footer">
              <button
                className="btn-secondary"
                onClick={() => setDeleteConfirm(null)}
              >
                Cancel
              </button>
              <button
                className="btn-danger"
                onClick={() => handleDelete(deleteConfirm.userId, deleteConfirm.username)}
              >
                Delete User
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
