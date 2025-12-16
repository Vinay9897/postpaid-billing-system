import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getCustomer, updateCustomer } from '../services/customerService'
import { deleteCustomer } from '../services/customerService'
import CustomerForm from '../components/CustomerForm'
import { useAuth } from '../hooks/useAuth'
import { decodeToken } from '../utils/jwtDecode'
import './CustomerProfile.css'

export default function CustomerProfilePage({ me }) {
  const { id } = useParams()
  const navigate = useNavigate()
  const { token, user, logout } = useAuth()
  const [customer, setCustomer] = useState(null)
  const [loading, setLoading] = useState(true)
  const [editing, setEditing] = useState(false)
  const [confirmDelete, setConfirmDelete] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    let mounted = true
    setLoading(true)
    const fetcher = me ? getCustomer : () => getCustomer(id)
    fetcher()
      .then((c) => { if (mounted) setCustomer(c) })
      .catch((err) => { if (mounted) setError(err.message || 'Failed to load') })
      .finally(() => { if (mounted) setLoading(false) })
    return () => { mounted = false }
  }, [id, me])

  const isAdmin = () => {
    if (user?.role) return String(user.role).toUpperCase() === 'ADMIN'
    if (!token) return false
    const decoded = decodeToken(token)
    return (decoded?.role && String(decoded.role).toUpperCase() === 'ADMIN')
  }

  const isOwner = () => {
    if (user?.userId) return Number(user.userId) === Number(customer?.user_id)
    if (!token) return false
    const decoded = decodeToken(token)
    const sub = decoded?.sub ? Number(decoded.sub) : decoded?.user_id || decoded?.id
    return Number(sub) === Number(customer?.user_id)
  }

  const canEdit = () => isAdmin() || isOwner()

  const handleSave = async (data) => {
    const updated = await updateCustomer(id, data)
    setCustomer(updated)
    setEditing(false)
  }

  const handleDelete = async () => {
    try {
      await deleteCustomer(id)
      // after deleting their customer profile, log the user out and redirect home
      logout()
      navigate('/')
    } catch (err) {
      setError(err.message || 'Failed to delete')
    }
  }

  if (loading) return <div className="page"><p>Loading customer...</p></div>
  if (error) return <div className="page"><p className="error">{error}</p></div>
  if (!customer) return <div className="page"><p>No customer profile found for id {id}.</p>{isAdmin() && <button onClick={() => navigate('/admin/customers')}>Manage Customers</button>}</div>

  return (
    <div className="page customer-page">
      <h2>Customer Profile</h2>

      {!editing ? (
        <div className="customer-details">
          <div><strong>Customer ID:</strong> {customer.customerId ?? customer.customer_id}</div>
          <div><strong>User ID:</strong> {customer.userId ?? customer.user_id}</div>
          <div><strong>Full name:</strong> {customer.fullName ?? customer.full_name ?? '-'}</div>
          <div><strong>Address:</strong> {customer.address ?? '-'} </div>
          <div><strong>Phone:</strong> {customer.phoneNumber ?? customer.phone_number ?? '-'}</div>

          {canEdit() && (
            <div className="actions">
              <button onClick={() => setEditing(true)} className="btn btn-primary">Edit Profile</button>
              <button onClick={() => setConfirmDelete(true)} className="btn btn-danger">Delete Profile</button>
            </div>
          )}
        </div>
      ) : (
        <div className="customer-edit">
          <h3>Edit Profile</h3>
          <CustomerForm initial={{
            fullName: customer.fullName ?? customer.full_name,
            address: customer.address,
            phoneNumber: customer.phoneNumber ?? customer.phone_number,
          }} onCancel={() => setEditing(false)} onSave={handleSave} />
        </div>
      )}
      {confirmDelete && (
        <div className="modal-overlay" onClick={() => setConfirmDelete(false)}>
          <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Confirm Delete</h3>
              <button className="modal-close" onClick={() => setConfirmDelete(false)}>✕</button>
            </div>
            <div className="modal-body">
              <p>Are you sure you want to delete your customer profile? This action cannot be undone.</p>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setConfirmDelete(false)}>Cancel</button>
              <button className="btn btn-danger" onClick={handleDelete}>Delete Profile</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// render confirmation modal at end of file via separate export is not necessary here; handled inline by state


