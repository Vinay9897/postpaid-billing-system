import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getInvoice } from '../services/invoiceService'
import { getCurrentCustomerServices } from '../services/customerService'
import { useAuth } from '../hooks/useAuth'

export default function InvoiceDetailsPage() {
  const { id } = useParams()
  const [invoice, setInvoice] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const { user } = useAuth()
  const [customer, setCustomer] = useState(null)
  const [services, setServices] = useState([])

  useEffect(() => {
    fetch()
  }, [id])

  const fetch = async () => {
    try {
      setLoading(true)
      setError('')
      // Resolve customer then invoice using canonical /api/customers/me
      const cust = await getMyCustomer()
      if (!cust) throw new Error('No customer record found for this account')
      setCustomer(cust)
      // load customer services (if any) for current logged-in customer
      try {
        const svcs = await getCurrentCustomerServices()
        setServices(Array.isArray(svcs) ? svcs : [])
      } catch (e) {
        setServices([])
      }

      const inv = await getInvoice(cust.customerId ?? cust.customer_id, id)
      setInvoice(inv)
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to load invoice')
    } finally {
      setLoading(false)
    }
  }

  const downloadPdf = () => {
    // If backend exposes a download endpoint, navigate to it (placeholder)
    const cid = customer?.customerId
    if (!cid) return
    window.open(`/api/customers/${cid}/invoices/${id}/download`, '_blank')
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
      <h3>Customer Services</h3>
      {services && services.length > 0 ? (
        <ul>
          {services.map((s) => (
            <li key={s.serviceId ?? s.id ?? JSON.stringify(s)}>{s.name || s.description || `Service ${s.serviceId ?? s.id}`}</li>
          ))}
        </ul>
      ) : (
        <p>No services found for this customer</p>
      )}
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
