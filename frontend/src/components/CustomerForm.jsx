import { useState } from 'react'

export default function CustomerForm({ initial, onCancel, onSave }) {
  const [form, setForm] = useState({
    fullName: initial?.fullName || initial?.full_name || '',
    address: initial?.address || '',
    phoneNumber: initial?.phoneNumber || initial?.phone_number || '',
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError(null)
    try {
      await onSave(form)
    } catch (err) {
      setError(err.message || 'Save failed')
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      {error && <div className="form-error">{error}</div>}

      <label>
        Full name
        <input name="fullName" value={form.fullName} onChange={handleChange} required />
      </label>

      <label>
        Address
        <input name="address" value={form.address} onChange={handleChange} />
      </label>

      <label>
        Phone number
        <input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} />
      </label>

      <div className="form-actions">
        <button type="button" onClick={onCancel} disabled={saving} className="btn btn-secondary">Cancel</button>
        <button type="submit" disabled={saving} className="btn btn-primary">{saving ? 'Saving...' : 'Save'}</button>
      </div>
    </form>
  )
}
