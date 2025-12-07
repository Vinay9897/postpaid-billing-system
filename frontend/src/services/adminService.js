import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const getApi = () => {
  const token = useAuthStore.getState().token
  return axios.create({
    baseURL: '/api',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

// List all users
export const listUsers = async () => {
  try {
    const api = getApi()
    const resp = await api.get('/admin/users')
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch users'
  }
}

// Get single user by ID
export const getUser = async (userId) => {
  try {
    const api = getApi()
    const resp = await api.get(`/admin/users/${userId}`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch user'
  }
}

// Create new user
export const createUser = async ({ username, email, password, role }) => {
  try {
    const api = getApi()
    const resp = await api.post('/admin/users', {
      username,
      email,
      password,
      role,
    })
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to create user'
  }
}

// Update user (email and role)
export const updateUser = async (userId, { email, role }) => {
  try {
    const api = getApi()
    const resp = await api.put(`/admin/users/${userId}`, {
      email,
      role,
    })
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to update user'
  }
}

// Set/reset user password
export const setPassword = async (userId, { password }) => {
  try {
    const api = getApi()
    const resp = await api.post(`/admin/users/${userId}/password`, {
      password,
    })
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to set password'
  }
}

// Delete user
export const deleteUser = async (userId) => {
  try {
    const api = getApi()
    const resp = await api.delete(`/admin/users/${userId}`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to delete user'
  }
}
