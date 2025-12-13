import React, { useState } from 'react'
import { getUsageForService } from '../services/usageService'

export default function UsageHistoryPage() {
  const [serviceId, setServiceId] = useState('')
  const [usage, setUsage] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const fetch = async () => {
    if (!serviceId) return setError('Enter a service ID')
    try {
      setLoading(true)
      setError('')
      const data = await getUsageForService(serviceId)
      setUsage(data)
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Failed to load usage')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h2>Usage History</h2>
      <div className="form-group">
        <label>Service ID</label>
        <input value={serviceId} onChange={(e) => setServiceId(e.target.value)} />
        <button className="btn-primary" onClick={fetch} disabled={loading}>
          {loading ? 'Loading...' : 'Fetch'}
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {usage.length === 0 ? (
        <p className="no-data">No usage records</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Amount</th>
                <th>Unit</th>
              </tr>
            </thead>
            <tbody>
              {usage.map((u) => (
                <tr key={u.usageId || `${u.usage_date}-${u.usage_amount}`}>
                  <td>{new Date(u.usageDate || u.usage_date).toLocaleDateString()}</td>
                  <td>{u.usageAmount || u.usage_amount}</td>
                  <td>{u.unit}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
