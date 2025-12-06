import React, { useState } from 'react'
import { login } from '../services/authService'
import { useAuthStore } from '../store/authStore'

export default function LoginPage(){
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const setToken = useAuthStore(state => state.setToken)

  const submit = async (e) => {
    e.preventDefault()
    try{
      const data = await login({ username, password })
      setToken(data.accessToken)
      localStorage.setItem('accessToken', data.accessToken)
      alert('Login success')
    }catch(err){
      alert('Login failed')
    }
  }

  return (
    <div className="page">
      <h2>Login</h2>
      <form onSubmit={submit}>
        <div>
          <label>Username</label>
          <input value={username} onChange={e=>setUsername(e.target.value)} />
        </div>
        <div>
          <label>Password</label>
          <input type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        </div>
        <button type="submit">Login</button>
      </form>
    </div>
  )
}
