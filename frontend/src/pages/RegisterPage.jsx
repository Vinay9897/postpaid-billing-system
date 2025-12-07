import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { register as registerUser } from '../services/authService'

export default function RegisterPage() {
  const { register, handleSubmit, formState: { errors }, watch, reset } = useForm({
    defaultValues: {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
    },
  })
  const [isLoading, setIsLoading] = useState(false)
  const [apiError, setApiError] = useState('')
  const navigate = useNavigate()
  const password = watch('password')

  const onSubmit = async (data) => {
    setIsLoading(true)
    setApiError('')
    try {
      await registerUser({
        username: data.username,
        email: data.email,
        password: data.password,
      })
      reset()
      navigate('/login')
    } catch (err) {
      setApiError(typeof err === 'string' ? err : 'Registration failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="page">
      <h2>Register</h2>
      {apiError && <div className="error-message">{apiError}</div>}
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="form-group">
          <label>Username</label>
          <input
            {...register('username', {
              required: 'Username is required',
              minLength: { value: 3, message: 'Username must be at least 3 characters' },
              pattern: { value: /^[a-zA-Z0-9_]+$/, message: 'Username can only contain letters, numbers, and underscores' },
            })}
          />
          {errors.username && <span className="error">{errors.username.message}</span>}
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            {...register('email', {
              required: 'Email is required',
              // pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Invalid email format' },
            })}
          />
          {errors.email && <span className="error">{errors.email.message}</span>}
        </div>

        <div className="form-group">
          <label>Password</label>
          <input
            type="password"
            {...register('password', {
              required: 'Password is required',
              minLength: { value: 5, message: 'Password must be at least 5 characters' },
            })}
          />
          {errors.password && <span className="error">{errors.password.message}</span>}
        </div>

        <div className="form-group">
          <label>Confirm Password</label>
          <input
            type="password"
            {...register('confirmPassword', {
              required: 'Confirm Password is required',
              validate: (value) => value === password || 'Passwords do not match',
            })}
          />
          {errors.confirmPassword && <span className="error">{errors.confirmPassword.message}</span>}
        </div>

        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  )
}
