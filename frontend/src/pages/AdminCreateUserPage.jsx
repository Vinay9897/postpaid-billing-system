import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { createUser } from '../services/adminService'

export default function AdminCreateUserPage() {
  const { register, handleSubmit, formState: { errors }, watch, reset } = useForm({
    defaultValues: {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      role: 'CUSTOMER',
    },
  })
  const [isLoading, setIsLoading] = useState(false)
  const [apiError, setApiError] = useState('')
  const [successMsg, setSuccessMsg] = useState('')
  const navigate = useNavigate()
  const password = watch('password')

  const onSubmit = async (data) => {
    setIsLoading(true)
    setApiError('')
    setSuccessMsg('')
    try {
      await createUser({
        username: data.username,
        email: data.email,
        password: data.password,
        role: data.role,
      })
      setSuccessMsg('User created successfully!')
      reset()
      setTimeout(() => navigate('/admin/users'), 1500)
    } catch (err) {
      setApiError(typeof err === 'string' ? err : 'Failed to create user')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="page admin-form-page">
      <div className="form-header">
        <h2>Create New User</h2>
        <button
          className="btn-secondary"
          onClick={() => navigate('/admin/users')}
        >
          Back to Users
        </button>
      </div>

      {apiError && <div className="error-message">{apiError}</div>}
      {successMsg && <div className="success-message">{successMsg}</div>}

      <form onSubmit={handleSubmit(onSubmit)} className="admin-form">
        <div className="form-group">
          <label>Username *</label>
          <input
            {...register('username', {
              required: 'Username is required',
              minLength: { value: 3, message: 'Username must be at least 3 characters' },
              pattern: {
                value: /^[a-zA-Z0-9_]+$/,
                message: 'Username can only contain letters, numbers, and underscores',
              },
            })}
            placeholder="Enter username"
          />
          {errors.username && <span className="error">{errors.username.message}</span>}
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
          <label>Password *</label>
          <input
            type="password"
            {...register('password', {
              required: 'Password is required',
              minLength: { value: 8, message: 'Password must be at least 8 characters' },
            })}
            placeholder="Enter password"
          />
          {errors.password && <span className="error">{errors.password.message}</span>}
        </div>

        <div className="form-group">
          <label>Confirm Password *</label>
          <input
            type="password"
            {...register('confirmPassword', {
              required: 'Please confirm your password',
              validate: (value) => value === password || 'Passwords do not match',
            })}
            placeholder="Confirm password"
          />
          {errors.confirmPassword && (
            <span className="error">{errors.confirmPassword.message}</span>
          )}
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
            disabled={isLoading}
          >
            {isLoading ? 'Creating...' : 'Create User'}
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
    </div>
  )
}
