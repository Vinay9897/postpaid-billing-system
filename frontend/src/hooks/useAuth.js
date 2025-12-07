import { useEffect } from 'react'
import { useAuthStore } from '../store/authStore'
import { decodeToken } from '../utils/jwtDecode'

export const useAuth = () => {
  const token = useAuthStore((state) => state.token)
  const user = useAuthStore((state) => state.user)
  const setToken = useAuthStore((state) => state.setToken)
  const setUser = useAuthStore((state) => state.setUser)
  const logout = useAuthStore((state) => state.logout)

  // Ensure user object is populated from token when available (handles page reloads/navigation)
  useEffect(() => {
    if (token && !user) {
      const decoded = decodeToken(token)
      if (decoded) {
        const u = {
          userId: decoded.sub ? Number(decoded.sub) : decoded.user_id || decoded.id || null,
          username: decoded.username || decoded.user_name || decoded.name || null,
          email: decoded.email || null,
          role: (decoded.role || (decoded.roles && (Array.isArray(decoded.roles) ? decoded.roles[0] : decoded.roles)) || null),
          createdAt: decoded.createdAt || decoded.created_at || null,
        }
        setUser(u)
      }
    }
  }, [token, user, setUser])

  return {
    token,
    user,
    isAuthenticated: !!token,
    setToken,
    setUser,
    logout,
  }
}
