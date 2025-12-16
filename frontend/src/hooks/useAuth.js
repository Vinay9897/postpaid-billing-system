import { useEffect } from 'react'
import { useAuthStore } from '../store/authStore'
import { decodeToken } from '../utils/jwtDecode'
import { extractRolesFromDecoded } from '../utils/roleUtils'

export const useAuth = () => {
  const token = useAuthStore((state) => state.token)
  const user = useAuthStore((state) => state.user)
  const setToken = useAuthStore((state) => state.setToken)
  const setUser = useAuthStore((state) => state.setUser)
  const logout = useAuthStore((state) => state.logout)

  // Ensure user object is populated from token when available (handles page reloads/navigation)
  useEffect(() => {
    if (token && !user) { // explain this line meaning
      const decoded = decodeToken(token)
      if (decoded) {
        const roles = extractRolesFromDecoded(decoded)
        const primaryRole = roles.length ? roles[0] : (decoded.role || null)
        const u = {
          userId: decoded.sub ? Number(decoded.sub) : decoded.user_id || decoded.id || null,
          username: decoded.username || decoded.user_name || decoded.name || null,
          email: decoded.email || null,
          role: primaryRole,
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
