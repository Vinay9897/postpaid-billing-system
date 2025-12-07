import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { login } from '../services/authService'
import { useAuthStore } from '../store/authStore'

export default function LoginPage() {
  const { register, handleSubmit, formState: { errors }, reset } = useForm({
    defaultValues: {
      username: '',
      password: '',
    },
  })
  const [isLoading, setIsLoading] = useState(false)
  const [apiError, setApiError] = useState('')
  const navigate = useNavigate()
  const setToken = useAuthStore((state) => state.setToken)
  const setUser = useAuthStore((state) => state.setUser)

  const onSubmit = async (data) => {
    setIsLoading(true)
    setApiError('')
    try {
      const result = await login(data)
      setToken(result.accessToken)
      setUser(result.user)
      reset()
      navigate('/dashboard')
    } catch (err) {
      setApiError(typeof err === 'string' ? err : 'Login failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="page">
      <h2>Login</h2>
      {apiError && <div className="error-message">{apiError}</div>}
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="form-group">
          <label>Username</label>
          <input
            {...register('username', {
              required: 'Username is required',
              minLength: { value: 3, message: 'Username must be at least 3 characters' },
            })}
          />
          {errors.username && <span className="error">{errors.username.message}</span>}
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

        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  )
}
