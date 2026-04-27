import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ErrorBanner, Field, Spinner } from '../components/UI'
import { UserPlus } from 'lucide-react'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate      = useNavigate()
  const [form, setForm]       = useState({ username: '', email: '', password: '', fullName: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)

  const set = k => e => setForm(f => ({ ...f, [k]: e.target.value }))

  const handle = async e => {
    e.preventDefault()
    setLoading(true); setError(null)
    try {
      await register(form)
      navigate('/courses')
    } catch(err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4 py-16">
      <div className="w-full max-w-sm animate-in">

        <div className="mb-8">
          <div className="w-10 h-10 border border-white flex items-center justify-center mb-5">
            <span className="font-mono text-sm">EP</span>
          </div>
          <h1 className="font-display text-3xl mb-1">Create account</h1>
          <p className="font-mono text-xs text-white/30">Start learning English today</p>
        </div>

        <form onSubmit={handle} className="flex flex-col gap-4">
          <ErrorBanner error={error} />

          <Field label="Full Name">
            <input className="input" placeholder="Jane Doe" value={form.fullName} onChange={set('fullName')} />
          </Field>

          <Field label="Username *">
            <input className="input" placeholder="jane_doe" value={form.username} onChange={set('username')} required autoFocus />
          </Field>

          <Field label="Email *">
            <input type="email" className="input" placeholder="jane@example.com" value={form.email} onChange={set('email')} required />
          </Field>

          <Field label="Password *">
            <input type="password" className="input" placeholder="Min 6 characters" value={form.password} onChange={set('password')} required minLength={6} />
          </Field>

          <button type="submit" disabled={loading} className="btn-primary flex items-center justify-center gap-2 mt-2">
            {loading ? <Spinner size={14} /> : <UserPlus size={14} />}
            {loading ? 'Creating account…' : 'Register'}
          </button>
        </form>

        <p className="mt-6 font-mono text-xs text-white/30 text-center">
          Already have an account?{' '}
          <Link to="/login" className="text-white/70 hover:text-white transition-colors underline underline-offset-2">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  )
}
