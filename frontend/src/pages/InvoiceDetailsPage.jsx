import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getInvoice } from '../services/invoiceService'

export default function InvoiceDetailsPage() {
  const { id } = useParams()
  const [invoice, setInvoice] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetch()
  }, [id])

  const fetch = async () => {
    try {
      setLoading(true)
      setError('')
      // Get customer id then invoice
      const resp = await fetch('/api/customers/me', { headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` } })
      if (!resp.ok) throw new Error('Failed to get customer')
      const cust = await resp.json()
      const inv = await getInvoice(cust.customerId, id)
      setInvoice(inv)
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to load invoice')
    } finally {
      setLoading(false)
    }
  }

  const downloadPdf = () => {
    // If backend exposes a download endpoint, navigate to it (placeholder)
    window.open(`/api/customers/me/invoices/${id}/download`, '_blank')
  }

  if (loading) return <div className="page"><p>Loading invoice...</p></div>

  if (error) return <div className="page"><div className="error-message">{error}</div></div>

  if (!invoice) return <div className="page"><p>No invoice data</p></div>

  return (
    <div className="page">
      <h2>Invoice #{invoice.invoiceId}</h2>
      <p><strong>Period:</strong> {new Date(invoice.billingPeriodStart).toLocaleDateString()} - {new Date(invoice.billingPeriodEnd).toLocaleDateString()}</p>
      <p><strong>Total:</strong> {invoice.totalAmount}</p>
      <p><strong>Status:</strong> {invoice.status}</p>
      <div>
        <button className="btn-primary" onClick={downloadPdf}>Download PDF</button>
      </div>
      <h3>Line items</h3>
      {/* If invoice has items */}
      {invoice.items && invoice.items.length > 0 ? (
        <ul>
          {invoice.items.map((it, idx) => (
            <li key={idx}>{it.description} — {it.amount}</li>
          ))}
        </ul>
      ) : (
        <p>No line items</p>
      )}
    </div>
  )
}
