import React, { useEffect, useState } from 'react'
import { listInvoicesForCustomer } from '../services/invoiceService'
import { useAuth } from '../hooks/useAuth'

export default function InvoicesPage() {
  const [invoices, setInvoices] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const { user } = useAuth()

  useEffect(() => {
    fetchInvoices()
  }, [])

  const fetchInvoices = async () => {
    try {
      setLoading(true)
      setError('')
      // Get customer id from user; backend provides /api/customers/me endpoint
      const resp = await fetch('/api/customers/me', { headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` } })
      if (!resp.ok) throw new Error('Failed to get customer')
      const cust = await resp.json()
      const data = await listInvoicesForCustomer(cust.customerId)
      setInvoices(data)
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to load invoices')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h2>Invoices</h2>
      {error && <div className="error-message">{error}</div>}
      {loading ? (
        <p>Loading...</p>
      ) : invoices.length === 0 ? (
        <p className="no-data">No invoices found</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Invoice ID</th>
                <th>Period</th>
                <th>Total</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {invoices.map((inv) => (
                <tr key={inv.invoiceId}>
                  <td>{inv.invoiceId}</td>
                  <td>{new Date(inv.billingPeriodStart).toLocaleDateString()} - {new Date(inv.billingPeriodEnd).toLocaleDateString()}</td>
                  <td>{inv.totalAmount}</td>
                  <td>{inv.status}</td>
                  <td>
                    <a href={`/invoices/${inv.invoiceId}`}>View</a>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
