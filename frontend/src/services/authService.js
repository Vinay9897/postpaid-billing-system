import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

export const register = async ({ username, email, password }) => {
  const resp = await api.post('/register', { username, email, password })
  return resp.data
}

export const login = async ({ username, password }) => {
  const resp = await api.post('/login', { username, password })
  return resp.data
}
