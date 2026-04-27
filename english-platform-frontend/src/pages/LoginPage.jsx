import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ErrorBanner, Field, Spinner } from '../components/UI'
import { LogIn } from 'lucide-react'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate   = useNavigate()
  const [form, setForm]       = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)

  const handle = async e => {
    e.preventDefault()
    setLoading(true); setError(null)
    try {
      await login(form)
      navigate('/courses')
    } catch(err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-sm animate-in">

        {/* Header */}
        <div className="mb-8">
          <div className="w-10 h-10 border border-white flex items-center justify-center mb-5">
            <span className="font-mono text-sm">EP</span>
          </div>
          <h1 className="font-display text-3xl mb-1">Welcome back</h1>
          <p className="font-mono text-xs text-white/30">Sign in to your account</p>
        </div>

        <form onSubmit={handle} className="flex flex-col gap-4">
          <ErrorBanner error={error} />

          <Field label="Username">
            <input
              className="input"
              placeholder="your_username"
              value={form.username}
              onChange={e => setForm(f => ({ ...f, username: e.target.value }))}
              required
              autoFocus
            />
          </Field>

          <Field label="Password">
            <input
              type="password"
              className="input"
              placeholder="••••••••"
              value={form.password}
              onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
              required
            />
          </Field>

          <button type="submit" disabled={loading} className="btn-primary flex items-center justify-center gap-2 mt-2">
            {loading ? <Spinner size={14} /> : <LogIn size={14} />}
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>

        <p className="mt-6 font-mono text-xs text-white/30 text-center">
          No account?{' '}
          <Link to="/register" className="text-white/70 hover:text-white transition-colors underline underline-offset-2">
            Register
          </Link>
        </p>

        {/* Demo hint */}
        <div className="mt-8 border border-white/8 p-4">
          <p className="mono-label mb-2">Demo credentials</p>
          <p className="font-mono text-xs text-white/40">Create an account via /register</p>
          <p className="font-mono text-xs text-white/40">First user is USER role by default</p>
        </div>
      </div>
    </div>
  )
}
