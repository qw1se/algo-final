import React from 'react'
import { Loader2, AlertCircle, CheckCircle } from 'lucide-react'

// ─── Spinner ──────────────────────────────────────────
export function Spinner({ size = 16 }) {
  return <Loader2 size={size} className="animate-spin text-white/50" />
}

// ─── Toast ────────────────────────────────────────────
export function Toast({ message, type = 'success', onClose }) {
  if (!message) return null
  return (
    <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3 border font-mono text-sm animate-in
      ${type === 'success' ? 'bg-[#111] border-white/30 text-white' : 'bg-[#111] border-red-400/50 text-red-400'}`}>
      {type === 'success'
        ? <CheckCircle size={14} />
        : <AlertCircle size={14} />}
      {message}
      <button onClick={onClose} className="ml-3 text-white/30 hover:text-white transition-colors">✕</button>
    </div>
  )
}

// ─── Empty State ──────────────────────────────────────
export function Empty({ icon: Icon, title, subtitle }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-3 text-white/30">
      {Icon && <Icon size={36} strokeWidth={1} />}
      <p className="font-display text-lg text-white/20">{title}</p>
      {subtitle && <p className="font-mono text-xs">{subtitle}</p>}
    </div>
  )
}

// ─── Modal ────────────────────────────────────────────
export function Modal({ open, onClose, title, children }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/80" onClick={onClose} />
      <div className="relative bg-[#111] border border-white/20 w-full max-w-lg mx-4 p-7 animate-in">
        <div className="flex items-center justify-between mb-6">
          <h2 className="font-display text-xl">{title}</h2>
          <button onClick={onClose} className="text-white/30 hover:text-white transition-colors font-mono">✕</button>
        </div>
        {children}
      </div>
    </div>
  )
}

// ─── Confirm Dialog ───────────────────────────────────
export function Confirm({ open, onClose, onConfirm, message }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/80" onClick={onClose} />
      <div className="relative bg-[#111] border border-red-400/30 p-7 max-w-sm w-full mx-4 animate-in">
        <p className="font-mono text-sm text-white/70 mb-6">{message}</p>
        <div className="flex gap-3 justify-end">
          <button onClick={onClose} className="btn-ghost text-xs py-2 px-4">Cancel</button>
          <button onClick={onConfirm} className="btn-danger">Confirm</button>
        </div>
      </div>
    </div>
  )
}

// ─── Level Badge ─────────────────────────────────────
export function LevelBadge({ level }) {
  const map = {
    BEGINNER:          'A1',
    ELEMENTARY:        'A2',
    INTERMEDIATE:      'B1',
    UPPER_INTERMEDIATE:'B2',
    ADVANCED:          'C1',
    PROFICIENCY:       'C2',
  }
  return (
    <span className="badge badge-active font-mono text-xs">
      {map[level] || level}
    </span>
  )
}

// ─── Status Badge ─────────────────────────────────────
export function StatusBadge({ status }) {
  const cls = {
    ACTIVE:    'badge-green',
    COMPLETED: 'badge-active',
    CANCELLED: 'badge-red',
    SUSPENDED: 'badge-gray',
  }
  return <span className={`badge ${cls[status] || 'badge-gray'} text-xs`}>{status}</span>
}

// ─── Progress Bar ─────────────────────────────────────
export function ProgressBar({ value }) {
  return (
    <div className="relative h-1 bg-white/10 w-full">
      <div
        className="absolute left-0 top-0 h-full bg-white transition-all duration-500"
        style={{ width: `${value}%` }}
      />
    </div>
  )
}

// ─── Field ────────────────────────────────────────────
export function Field({ label, children }) {
  return (
    <div>
      <label className="mono-label block mb-1.5">{label}</label>
      {children}
    </div>
  )
}

// ─── Error Banner ─────────────────────────────────────
export function ErrorBanner({ error }) {
  if (!error) return null
  const msg = error?.response?.data?.message || error?.message || 'Unknown error'
  return (
    <div className="flex items-center gap-2 border border-red-400/30 bg-red-400/5 px-4 py-3 font-mono text-sm text-red-400">
      <AlertCircle size={14} />
      {msg}
    </div>
  )
}
