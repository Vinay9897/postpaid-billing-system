// Utility to extract and normalize roles from decoded JWT payload or user objects
export function extractRolesFromDecoded(decoded) {
  if (!decoded) return []

  // Common claim names: role (string), roles (array or string), authorities (array)
  const candidates = []
  if (decoded.role) candidates.push(decoded.role)
  if (decoded.roles) candidates.push(decoded.roles)
  if (decoded.authorities) candidates.push(decoded.authorities)

  // Flatten and normalize to uppercase without ROLE_ prefix
  const flat = []
  candidates.forEach((c) => {
    if (!c) return
    if (Array.isArray(c)) c.forEach((v) => flat.push(String(v)))
    else flat.push(String(c))
  })

  const normalized = Array.from(
    new Set(
      flat.map((r) => {
        let s = String(r || '')
        // If it's an object like {role: 'ADMIN'}, try to stringify
        try {
          if (s.startsWith('{') && s.endsWith('}')) {
            const obj = JSON.parse(s)
            s = obj.role || obj.authority || s
          }
        } catch (e) {
          // ignore
        }
        s = s.replace(/^ROLE_/i, '')
        return s.toUpperCase()
      })
    )
  ).filter(Boolean)

  return normalized
}

export function hasRole(decodedOrUser, role) {
  const wanted = String(role || '').toUpperCase()
  if (!wanted) return false

  let decoded = null
  if (decodedOrUser && typeof decodedOrUser === 'object') decoded = decodedOrUser

  const roles = extractRolesFromDecoded(decoded)
  return roles.includes(wanted)
}
