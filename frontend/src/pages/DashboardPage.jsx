import React, { useEffect, useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import { getCustomer,listServicesForCustomer } from '../services/customerService'
import { listInvoicesForCustomer } from '../services/invoiceService'

export default function DashboardPage() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [customer, setCustomer] = useState(null)
  const [services, setServices] = useState([])
  const [invoices, setInvoices] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    let mounted = true
    const load = async () => {
      try {
        setLoading(true)
        setError('')
       if (!user || !user.userId) return

        // If user is admin, don't attempt to resolve a customer for them.
        // Show admin navigation instead of customer-specific "not found" errors.
        if (String(user.role).toUpperCase() === 'ADMIN') {
          if (mounted) {
            setCustomer(null)
            setServices([])
            setInvoices([])
          }
          return
        }

        const cust = await getCustomer(user.userId)
        console.log('Resolved customer for dashboard:', cust)
        if (!mounted) return
        if (!cust) {
          // For non-admin users, show a friendly error when no customer exists.
          setError('No customer record found for this account')
          return
        }
        setCustomer(cust)
        const svc = await listServicesForCustomer(cust.customerId ?? cust.customer_id)
        if (!mounted) return
        setServices(svc)
        const inv = await listInvoicesForCustomer(cust.customerId ?? cust.customer_id)
        if (!mounted) return
        setInvoices(inv)
      } catch (err) {
        setError(typeof err === 'string' ? err : err.message || 'Failed to load dashboard data')
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    return () => { mounted = false }
  }, [])

  if (!user) return <div className="page"><p>Loading user data...</p></div>

  return (
    <div className="page">
      <h2>Welcome, {user.username}!</h2>

      {error && <div className="error-message">{error}</div>}

      <div className="dashboard-grid">
        <section className="card">
          <h3>Your Profile</h3>
          <p><strong>Username:</strong> {user.username}</p>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Role:</strong> {user.role}</p>
          {user.createdAt && <p><strong>Member Since:</strong> {new Date(user.createdAt).toLocaleDateString()}</p>}
        </section>

        <section className="card">
          <h3>Current Services</h3>
          {loading ? <p>Loading services...</p> : services.length === 0 ? <p>No active services</p> : (
            <ul>
              {services.map(s => (
                <li key={s.serviceId ?? s.service_id}>{s.serviceType ?? s.service_type} — {s.status} (Start: {new Date(s.startDate ?? s.start_date).toLocaleDateString()})</li>
              ))}
            </ul>
          )}
        </section>

        <section className="card">
          <h3>Outstanding Invoices</h3>
          {loading ? <p>Loading invoices...</p> : invoices.filter(i => (i.status ?? '').toLowerCase() !== 'paid').length === 0 ? (
            <p>No outstanding invoices</p>
          ) : (
            <ul>
              {invoices.filter(i => (i.status ?? '').toLowerCase() !== 'paid').map(inv => (
                <li key={inv.invoiceId ?? inv.invoice_id}>
                  Invoice #{inv.invoiceId ?? inv.invoice_id} — {inv.totalAmount} — {inv.status}
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  )
}
