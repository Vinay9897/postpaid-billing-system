import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const getApi = () => {
  const token = useAuthStore.getState().token
  return axios.create({
    baseURL: '/api',
    headers: { Authorization: `Bearer ${token}` },
  })
}

export const listPaymentsForInvoice = async (invoiceId) => {
  try {
    const api = getApi()
    const resp = await api.get(`/invoices/${invoiceId}/payments`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch payments'
  }
}

export const createPayment = async (invoiceId, { amount, payment_method }) => {
  try {
    const api = getApi()
    const resp = await api.post(`/invoices/${invoiceId}/payments`, { amount, payment_method })
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to create payment'
  }
}

export default {
  listPaymentsForInvoice,
  createPayment,
}
