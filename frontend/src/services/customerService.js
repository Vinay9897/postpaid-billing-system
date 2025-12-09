import { useAuthStore } from '../store/authStore'

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function authHeaders() {
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  return token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' }
}

export async function getCustomer(id) {
  const res = await fetch(`${API_BASE}/api/customers/${id}`, { headers: authHeaders() })
  if (!res.ok) throw new Error(`Failed to fetch customer: ${res.status}`)
  return res.json()
}

export async function getMyCustomer() {
  const res = await fetch(`${API_BASE}/api/customers/me`, { headers: authHeaders() })
  if (!res.ok) throw new Error(`Failed to fetch my customer: ${res.status}`)
  return res.json()
}

export async function updateCustomer(id, data) {
  const res = await fetch(`${API_BASE}/api/customers/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Failed to update customer: ${res.status} ${text}`)
  }
  return res.json()
}

export async function deleteCustomer(id) {
  const res = await fetch(`${API_BASE}/api/customers/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Failed to delete customer: ${res.status} ${text}`)
  }
  return true
}
