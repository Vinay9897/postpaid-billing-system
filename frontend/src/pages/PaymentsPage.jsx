import React, { useEffect, useState } from 'react'
import { listPaymentsForInvoice, createPayment } from '../services/paymentService'

export default function PaymentsPage() {
  const [invoiceId, setInvoiceId] = useState('')
  const [payments, setPayments] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [amount, setAmount] = useState('')
  const [method, setMethod] = useState('card')

  const fetch = async () => {
    if (!invoiceId) return setError('Enter an invoice ID')
    try {
      setLoading(true)
      setError('')
      const data = await listPaymentsForInvoice(invoiceId)
      setPayments(data)
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to load payments')
    } finally {
      setLoading(false)
    }
  }

  const handlePay = async () => {
    try {
      setLoading(true)
      setError('')
      await createPayment(invoiceId, { amount: parseFloat(amount), payment_method: method })
      setAmount('')
      setMethod('card')
      await fetch()
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to create payment')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h2>Payments</h2>
      <div className="form-group">
        <label>Invoice ID</label>
        <input value={invoiceId} onChange={(e) => setInvoiceId(e.target.value)} />
        <button className="btn-primary" onClick={fetch} disabled={loading}>{loading ? 'Loading...' : 'Fetch'}</button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="form-group">
        <label>Amount</label>
        <input value={amount} onChange={(e) => setAmount(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Payment Method</label>
        <select value={method} onChange={(e) => setMethod(e.target.value)}>
          <option value="card">Card</option>
          <option value="bank_transfer">Bank Transfer</option>
          <option value="cash">Cash</option>
        </select>
      </div>
      <button className="btn-primary" onClick={handlePay} disabled={loading}>Pay</button>

      <h3>Payments</h3>
      {payments.length === 0 ? (
        <p className="no-data">No payments found</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Payment ID</th>
                <th>Date</th>
                <th>Amount</th>
                <th>Method</th>
              </tr>
            </thead>
            <tbody>
              {payments.map((p) => (
                <tr key={p.paymentId}>
                  <td>{p.paymentId}</td>
                  <td>{new Date(p.paymentDate || p.payment_date).toLocaleString()}</td>
                  <td>{p.amount}</td>
                  <td>{p.paymentMethod || p.payment_method}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
