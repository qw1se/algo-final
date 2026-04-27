import React, { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  BookOpen, Users, GraduationCap, LayoutDashboard,
  LogOut, ChevronDown, Shield, UserCircle
} from 'lucide-react'

const navLinks = [
  { to: '/courses',     label: 'Courses',     icon: BookOpen,       roles: ['USER','MANAGER','ADMIN'] },
  { to: '/enrollments', label: 'My Learning', icon: GraduationCap,  roles: ['USER','MANAGER','ADMIN'] },
  { to: '/manage',      label: 'Manage',      icon: LayoutDashboard,roles: ['MANAGER','ADMIN'] },
  { to: '/admin',       label: 'Admin',       icon: Shield,         roles: ['ADMIN'] },
]

export default function Navbar() {
  const { user, logout, isAdmin, isManager } = useAuth()
  const location = useLocation()
  const navigate  = useNavigate()
  const [open, setOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const visible = navLinks.filter(l => {
    if (!user) return false
    return l.roles.includes(user.role)
  })

  return (
    <nav className="fixed top-0 left-0 right-0 z-30 bg-[#0a0a0a]/95 backdrop-blur-sm border-b border-white/8">
      <div className="max-w-7xl mx-auto px-6 h-14 flex items-center justify-between">

        {/* Logo */}
        <Link to="/courses" className="flex items-center gap-2.5">
          <div className="w-7 h-7 border border-white flex items-center justify-center">
            <span className="font-mono text-xs font-medium">EP</span>
          </div>
          <span className="font-display text-sm tracking-wide hidden sm:block">English Platform</span>
        </Link>

        {/* Nav links */}
        {user && (
          <div className="flex items-center gap-1">
            {visible.map(({ to, label, icon: Icon }) => {
              const active = location.pathname.startsWith(to)
              return (
                <Link
                  key={to}
                  to={to}
                  className={`flex items-center gap-1.5 px-3 py-1.5 font-mono text-xs transition-all duration-150
                    ${active
                      ? 'text-white bg-white/10 border border-white/20'
                      : 'text-white/40 hover:text-white hover:bg-white/5 border border-transparent'}`}
                >
                  <Icon size={12} />
                  {label}
                </Link>
              )
            })}
          </div>
        )}

        {/* User menu */}
        {user ? (
          <div className="relative">
            <button
              onClick={() => setOpen(o => !o)}
              className="flex items-center gap-2 font-mono text-xs text-white/50 hover:text-white transition-colors"
            >
              <UserCircle size={16} />
              <span className="hidden sm:block">{user.username}</span>
              <span className={`badge text-xs ${
                user.role === 'ADMIN' ? 'badge-admin' :
                user.role === 'MANAGER' ? 'badge-manager' : 'badge-user'
              }`}>{user.role}</span>
              <ChevronDown size={12} className={`transition-transform ${open ? 'rotate-180' : ''}`} />
            </button>

            {open && (
              <div className="absolute right-0 top-full mt-2 w-44 bg-[#111] border border-white/15 py-1 z-50 animate-in">
                <div className="px-4 py-2 border-b border-white/8">
                  <p className="font-mono text-xs text-white/30">{user.email}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="w-full flex items-center gap-2 px-4 py-2.5 font-mono text-xs text-red-400/70 hover:text-red-400 hover:bg-red-400/5 transition-colors"
                >
                  <LogOut size={12} /> Sign out
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="flex gap-2">
            <Link to="/login"    className="btn-ghost text-xs py-1.5 px-4">Login</Link>
            <Link to="/register" className="btn-primary text-xs py-1.5 px-4">Register</Link>
          </div>
        )}
      </div>
    </nav>
  )
}
