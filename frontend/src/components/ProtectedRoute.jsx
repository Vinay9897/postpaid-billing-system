import { Navigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { decodeToken } from '../utils/jwtDecode'

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
  let role = user?.role
  if (!role && token) {
    const decoded = decodeToken(token)
    role = decoded?.role || decoded?.ROLE || decoded?.roleName || decoded?.sub && null
  }

  if (role?.toUpperCase() !== 'ADMIN') {
    return <Navigate to="/dashboard" replace />
  }

  return children
}
