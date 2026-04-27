import React, { createContext, useContext, useState, useEffect } from 'react'
import { authApi } from '../services/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser]       = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (stored) setUser(JSON.parse(stored))
    setLoading(false)
  }, [])

  const login = async (credentials) => {
    const res  = await authApi.login(credentials)
    const data = res.data.data
    localStorage.setItem('token', data.token)
    localStorage.setItem('user',  JSON.stringify(data))
    setUser(data)
    return data
  }

  const register = async (payload) => {
    const res  = await authApi.register(payload)
    const data = res.data.data
    localStorage.setItem('token', data.token)
    localStorage.setItem('user',  JSON.stringify(data))
    setUser(data)
    return data
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  const isAdmin   = user?.role === 'ADMIN'
  const isManager = user?.role === 'MANAGER' || isAdmin

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, isAdmin, isManager }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
