import { create } from 'zustand'

export const useAuthStore = create((set) => ({
  token: typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null,
  user: null,
  
  setToken: (token) => set(() => { 
    if (token) {
      localStorage.setItem('accessToken', token)
    }
    return { token } 
  }),
  
  setUser: (user) => set({ user }),
  
  logout: () => set(() => { 
    localStorage.removeItem('accessToken')
    return { token: null, user: null } 
  }),
  
  clear: () => set(() => { 
    localStorage.removeItem('accessToken')
    return { token: null, user: null } 
  })
}))
