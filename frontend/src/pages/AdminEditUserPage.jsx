import React, { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate, useParams } from 'react-router-dom'
import { getUser, updateUser, setPassword } from '../services/adminService'

export default function AdminEditUserPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { register, handleSubmit, formState: { errors }, reset, watch } = useForm({
    defaultValues: {
      username: '',
      email: '',
      role: 'CUSTOMER',
      password: '',
      confirmPassword: '',
    },
  })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [successMsg, setSuccessMsg] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPasswordModal, setShowPasswordModal] = useState(false)
  const [passwordData, setPasswordData] = useState({ password: '', confirmPassword: '' })
  const [passwordError, setPasswordError] = useState('')
  const [passwordLoading, setPasswordLoading] = useState(false)
  const password = watch('password')

  useEffect(() => {
    fetchUser()
  }, [id])

  const fetchUser = async () => {
    try {
      setLoading(true)
      const userData = await getUser(id)
      reset({
        username: userData.username,
        email: userData.email,
        role: userData.role,
      })
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to load user')
    } finally {
      setLoading(false)
    }
  }

  const onSubmit = async (data) => {
    setIsSubmitting(true)
    setError('')
    setSuccessMsg('')
    try {
      await updateUser(id, {
        email: data.email,
        role: data.role,
      })
      setSuccessMsg('User updated successfully!')
      setTimeout(() => {
        setSuccessMsg('')
      }, 2000)
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to update user')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handlePasswordChange = async () => {
    if (passwordData.password !== passwordData.confirmPassword) {
      setPasswordError('Passwords do not match')
      return
    }
    if (passwordData.password.length < 8) {
      setPasswordError('Password must be at least 8 characters')
      return
    }

    setPasswordLoading(true)
    setPasswordError('')
    try {
      await setPassword(id, { password: passwordData.password })
      setShowPasswordModal(false)
      setPasswordData({ password: '', confirmPassword: '' })
      setSuccessMsg('Password updated successfully!')
      setTimeout(() => setSuccessMsg(''), 2000)
    } catch (err) {
      setPasswordError(typeof err === 'string' ? err : 'Failed to update password')
    } finally {
      setPasswordLoading(false)
    }
  }

  if (loading) {
    return <div className="page"><p>Loading user...</p></div>
  }

  return (
    <div className="page admin-form-page">
      <div className="form-header">
        <h2>Edit User</h2>
        <button
          className="btn-secondary"
          onClick={() => navigate('/admin/users')}
        >
          Back to Users
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}
      {successMsg && <div className="success-message">{successMsg}</div>}

      <form onSubmit={handleSubmit(onSubmit)} className="admin-form">
        <div className="form-group">
          <label>Username (Read-only)</label>
          <input
            {...register('username')}
            disabled
            className="input-disabled"
          />
        </div>

        <div className="form-group">
          <label>Email *</label>
          <input
            type="email"
            {...register('email', {
              required: 'Email is required',
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: 'Please enter a valid email address',
              },
            })}
            placeholder="Enter email"
          />
          {errors.email && <span className="error">{errors.email.message}</span>}
        </div>

        <div className="form-group">
          <label>Role *</label>
          <select
            {...register('role', {
              required: 'Role is required',
            })}
          >
            <option value="CUSTOMER">Customer</option>
            <option value="SUPPORT">Support</option>
            <option value="ADMIN">Admin</option>
          </select>
          {errors.role && <span className="error">{errors.role.message}</span>}
        </div>

        <div className="form-actions">
          <button
            type="submit"
            className="btn-primary"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Saving...' : 'Save Changes'}
          </button>
          <button
            type="button"
            className="btn-warning"
            onClick={() => setShowPasswordModal(true)}
          >
            Reset Password
          </button>
          <button
            type="button"
            className="btn-secondary"
            onClick={() => navigate('/admin/users')}
          >
            Cancel
          </button>
        </div>
      </form>

      {showPasswordModal && (
        <div className="modal-overlay" onClick={() => setShowPasswordModal(false)}>
          <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Reset Password</h3>
              <button
                className="modal-close"
                onClick={() => setShowPasswordModal(false)}
              >
                ✕
              </button>
            </div>
            <div className="modal-body">
              {passwordError && <div className="error-message">{passwordError}</div>}
              <div className="form-group">
                <label>New Password *</label>
                <input
                  type="password"
                  value={passwordData.password}
                  onChange={(e) =>
                    setPasswordData({ ...passwordData, password: e.target.value })
                  }
                  placeholder="Enter new password (min 8 characters)"
                />
              </div>
              <div className="form-group">
                <label>Confirm Password *</label>
                <input
                  type="password"
                  value={passwordData.confirmPassword}
                  onChange={(e) =>
                    setPasswordData({ ...passwordData, confirmPassword: e.target.value })
                  }
                  placeholder="Confirm new password"
                />
              </div>
            </div>
            <div className="modal-footer">
              <button
                className="btn-secondary"
                onClick={() => setShowPasswordModal(false)}
              >
                Cancel
              </button>
              <button
                className="btn-primary"
                onClick={handlePasswordChange}
                disabled={passwordLoading}
              >
                {passwordLoading ? 'Updating...' : 'Update Password'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
