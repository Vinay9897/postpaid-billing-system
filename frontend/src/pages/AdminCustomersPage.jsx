import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { listCustomersAdmin, deleteCustomerAdmin } from '../services/adminCustomersService'

export default function AdminCustomersPage() {
  const [customers, setCustomers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [deleteConfirm, setDeleteConfirm] = useState(null)
  const navigate = useNavigate()

  useEffect(() => { fetchCustomers() }, [])

  const fetchCustomers = async () => {
    try {
      setLoading(true)
      setError('')
      const data = await listCustomersAdmin()
      setCustomers(data)
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to load customers')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await deleteCustomerAdmin(id)
      setCustomers(customers.filter(c => (c.customerId ?? c.customer_id) !== id))
      setDeleteConfirm(null)
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to delete')
      setDeleteConfirm(null)
    }
  }

  if (loading) return <div className="page"><p>Loading customers...</p></div>

  return (
    <div className="page admin-customers-page">
      <div className="admin-header">
        <h2>Customers</h2>
        <button className="btn-primary" onClick={() => navigate('/admin/customers/new')}>+ New Customer</button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {customers.length === 0 ? (
        <p className="no-data">No customers found</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>User ID</th>
                <th>Full Name</th>
                <th>Phone</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {customers.map(c => {
                const cid = c.customerId ?? c.customer_id
                return (
                  <tr key={cid}>
                    <td>{cid}</td>
                    <td>{c.userId ?? c.user_id}</td>
                    <td>{c.fullName ?? c.full_name}</td>
                    <td>{c.phoneNumber ?? c.phone_number}</td>
                    <td className="actions-cell">
                      <button className="btn-small btn-edit" onClick={() => navigate(`/admin/customers/${cid}/edit`)}>Edit</button>
                      <button className="btn-small btn-primary" onClick={() => navigate(`/admin/customers/${cid}/services`)}>Services</button>
                      <button className="btn-small btn-delete" onClick={() => setDeleteConfirm(c)}>Delete</button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}

      {deleteConfirm && (
        <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
          <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Confirm Delete</h3>
              <button className="modal-close" onClick={() => setDeleteConfirm(null)}>✕</button>
            </div>
            <div className="modal-body">
              <p>Are you sure you want to delete customer <strong>{deleteConfirm.fullName ?? deleteConfirm.full_name}</strong>?</p>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setDeleteConfirm(null)}>Cancel</button>
              <button className="btn-danger" onClick={() => handleDelete(deleteConfirm.customerId ?? deleteConfirm.customer_id)}>Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
