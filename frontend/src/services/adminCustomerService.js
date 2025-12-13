import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const getApi = () => {
  const token = useAuthStore.getState().token
  return axios.create({
    baseURL: '/api',
    headers: { Authorization: `Bearer ${token}` },
  })
}

export const listServicesForCustomerAdmin = async (customerId) => {
  try {
    const api = getApi()
    const resp = await api.get(`/admin/customers/${customerId}/services`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch services'
  }
}

export const createServiceForCustomerAdmin = async (customerId, { serviceType, startDate, status }) => {
  try {
    const api = getApi()
    const payload = { serviceType, startDate, status }
    const resp = await api.post(`/admin/customers/${customerId}/services`, payload)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to create service'
  }
}

export default {
  listServicesForCustomerAdmin,
  createServiceForCustomerAdmin,
}
