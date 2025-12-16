import React, { useEffect, useState } from 'react'
import { listInvoicesForCustomer } from '../services/invoiceService'
import {  useAuth } from '../hooks/useAuth'
// import { getCustomer } from '../services/customerService'

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
      // Resolve customer via canonical owner endpoint
      const cust = await getCustomer(user.userId)
      console.log('Resolved customer for invoices:', cust)
      if (!cust) throw new Error('No customer record found for this account')
      const data = await listInvoicesForCustomer(cust.customerId ?? cust.customer_id)
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
