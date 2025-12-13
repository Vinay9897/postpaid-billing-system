import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const getApi = () => {
  const token = useAuthStore.getState().token
  return axios.create({
    baseURL: '/api',
    headers: { Authorization: `Bearer ${token}` },
  })
}

export const getUsageForService = async (serviceId) => {
  try {
    const api = getApi()
    const resp = await api.get(`/services/${serviceId}/usage`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch usage'
  }
}

export const recordUsage = async (serviceId, { usage_date, usage_amount, unit }) => {
  try {
    const api = getApi()
    const resp = await api.post(`/services/${serviceId}/usage`, { usage_date, usage_amount, unit })
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to record usage'
  }
}

export default {
  getUsageForService,
  recordUsage,
}
