import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const getApi = () => {
  const token = useAuthStore.getState().token
  return axios.create({
    baseURL: '/api',
    headers: { Authorization: `Bearer ${token}` },
  })
}

export const listInvoicesForCustomer = async (customerId) => {
  try {
    const api = getApi()
    const resp = await api.get(`/customers/${customerId}/invoices`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch invoices'
  }
}

export const getInvoice = async (customerId, invoiceId) => {
  try {
    const api = getApi()
    const resp = await api.get(`/customers/${customerId}/invoices/${invoiceId}`)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to fetch invoice'
  }
}

export const createInvoice = async (customerId, payload) => {
  try {
    const api = getApi()
    const resp = await api.post(`/customers/${customerId}/invoices`, payload)
    return resp.data
  } catch (err) {
    throw err.response?.data?.message || 'Failed to create invoice'
  }
}

export default {
  listInvoicesForCustomer,
  getInvoice,
  createInvoice,
}
