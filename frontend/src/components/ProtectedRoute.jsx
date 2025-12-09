import { Navigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { decodeToken } from '../utils/jwtDecode'
import { hasRole } from '../utils/roleUtils'

export default function ProtectedRoute({ children }) {
  const token = useAuthStore((state) => state.token)
  
  if (!token) {
    return <Navigate to="/login" replace />
  }
  
  return children
}

// Role-based variant
export function AdminRoute({ children }) {
  const token = useAuthStore((state) => state.token)
  const user = useAuthStore((state) => state.user)

  if (!token) {
    return <Navigate to="/login" replace />
  }

  // Determine role: prefer store user, fall back to decoding token
  // Prefer normalized role from user in store
  if (user?.role && String(user.role).toUpperCase() === 'ADMIN') return children

  if (token) {
    const decoded = decodeToken(token)
    if (hasRole(decoded, 'ADMIN')) return children
  }

  return <Navigate to="/dashboard" replace />

  return children
}
