import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { listServicesForCustomerAdmin, createServiceForCustomerAdmin } from '../services/adminCustomerService'

export default function AdminCustomerServicesPage() {
  const { id } = useParams()
  const [services, setServices] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [serviceType, setServiceType] = useState('mobile')
  const [startDate, setStartDate] = useState('')
  const [status, setStatus] = useState('active')
  const [creating, setCreating] = useState(false)

  useEffect(() => { fetchServices() }, [id])

  const fetchServices = async () => {
    try {
      setLoading(true)
      setError('')
      const data = await listServicesForCustomerAdmin(id)
      setServices(data)
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to load services')
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = async (e) => {
    e.preventDefault()
    try {
      setCreating(true)
      setError('')
      await createServiceForCustomerAdmin(id, { serviceType, startDate, status })
      setServiceType('mobile')
      setStartDate('')
      setStatus('active')
      await fetchServices()
    } catch (err) {
      setError(typeof err === 'string' ? err : err.message || 'Failed to create service')
    } finally {
      setCreating(false)
    }
  }

  return (
    <div className="page">
      <h2>Customer Services (Customer ID: {id})</h2>
      {error && <div className="error-message">{error}</div>}
      {loading ? (
        <p>Loading services...</p>
      ) : services.length === 0 ? (
        <p className="no-data">No services found</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Service ID</th>
                <th>Type</th>
                <th>Start Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {services.map((s) => (
                <tr key={s.serviceId ?? s.service_id}>
                  <td>{s.serviceId ?? s.service_id}</td>
                  <td>{s.serviceType ?? s.service_type}</td>
                  <td>{s.startDate ?? s.start_date}</td>
                  <td>{s.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <h3>Add Service (Admin)</h3>
      <form onSubmit={handleCreate} className="admin-form">
        <div className="form-group">
          <label>Service Type</label>
          <select value={serviceType} onChange={(e) => setServiceType(e.target.value)}>
            <option value="mobile">Mobile</option>
            <option value="broadband">Broadband</option>
            <option value="tv">TV</option>
          </select>
        </div>
        <div className="form-group">
          <label>Start Date</label>
          <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Status</label>
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="active">Active</option>
            <option value="suspended">Suspended</option>
            <option value="terminated">Terminated</option>
          </select>
        </div>
        <div className="form-actions">
          <button className="btn-primary" type="submit" disabled={creating}>{creating ? 'Creating...' : 'Add Service'}</button>
        </div>
      </form>
    </div>
  )
}
