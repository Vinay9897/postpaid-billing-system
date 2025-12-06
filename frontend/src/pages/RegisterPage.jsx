import React, { useState } from 'react'
import { register } from '../services/authService'

export default function RegisterPage(){
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    try{
      const data = await register({ username, email, password })
      alert('Registered user id: ' + data.user_id)
    }catch(err){
      alert('Register failed')
    }
  }

  return (
    <div className="page">
      <h2>Register</h2>
      <form onSubmit={submit}>
        <div>
          <label>Username</label>
          <input value={username} onChange={e=>setUsername(e.target.value)} />
        </div>
        <div>
          <label>Email</label>
          <input value={email} onChange={e=>setEmail(e.target.value)} />
        </div>
        <div>
          <label>Password</label>
          <input type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  )
}
