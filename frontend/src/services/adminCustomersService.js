import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const getApi = () => {
  const token = useAuthStore.getState().token
  return axios.create({
    baseURL: '/api',
    headers: { Authorization: `Bearer ${token}` },
  })
}

export const listCustomersAdmin = async () => {
  try {
    const api = getApi()
    const resp = await api.get('/admin/customers')
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch customers'
  }
}

export const deleteCustomerAdmin = async (customerId) => {
  try {
    const api = getApi()
    const resp = await api.delete(`/admin/customers/${customerId}`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to delete customer'
  }
}

export const createCustomerAdmin = async ({ userId, fullName, address = '', phoneNumber = '' }) => {
  try {
    const api = getApi()
    const payload = { userId, fullName, address, phoneNumber }
    const resp = await api.post('/admin/customers', payload)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to create customer'
  }
}

export default {
  listCustomersAdmin,
  deleteCustomerAdmin,
  createCustomerAdmin,
}
