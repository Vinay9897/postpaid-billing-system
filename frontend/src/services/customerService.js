
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function authHeaders() {
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  return token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' }
}

export async function getCustomer(userId) {
  const res = await fetch(`${API_BASE}/api/customers/${userId}`, { headers: authHeaders() })
  if (!res.ok) throw new Error(`Failed to fetch customer: ${res.status}`)
  return res.json()
}

export async function resolveCustomerForUser(user) {
  try {
    return await getCustomer(user.id)
  } catch (err) {
    return null
  }
}

export async function listServicesForCustomer(customerId) {
  const res = await fetch(`${API_BASE}/api/customers/${customerId}/services`, { headers: authHeaders() })
  if (!res.ok) throw new Error(`Failed to fetch services: ${res.status}`)
  return res.json()
}

// Convenience helper: resolve current user's customer record then return its services.
export async function getCurrentCustomerServices() {
  try {
    const cust = await getMyCustomer()
    if (!cust) return []
    const cid = cust.customerId
    if (!cid) return []
    return await listServicesForCustomer(cid)
  } catch (err) {
    return []
  }
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
