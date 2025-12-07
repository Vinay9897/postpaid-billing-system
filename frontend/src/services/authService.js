import axios from 'axios'
import { decodeToken } from '../utils/jwtDecode'

const api = axios.create({ baseURL: '/api' })

export const register = async ({ username, email, password }) => {
  try {
    const resp = await api.post('/register', { username, email, password })
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Registration failed'
  }
}

export const login = async ({ username, password }) => {
  try {
    const resp = await api.post('/login', { username, password })
    const token = resp.data.accessToken
    
    // Decode token to extract user info
    const decoded = decodeToken(token)
    
    return {
      accessToken: token,
      user: decoded,
    }
  } catch (err) {
    throw err.response?.data?.message || 'Login failed'
  }
}
