import { create } from 'zustand'

export const useAuthStore = create((set) => ({
  token: typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null,
  setToken: (token) => set(() => { localStorage.setItem('accessToken', token); return { token } }),
  clear: () => set(() => { localStorage.removeItem('accessToken'); return { token: null } })
}))
