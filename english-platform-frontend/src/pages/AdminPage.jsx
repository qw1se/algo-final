import React, { useState, useEffect } from 'react'
import { usersApi, enrollmentsApi } from '../services/api'
import { Spinner, Empty, Toast, ErrorBanner, Confirm, Modal, Field } from '../components/UI'
import { Users, Trash2, Pencil, Shield, ChevronDown } from 'lucide-react'

const ROLES = ['USER','MANAGER','ADMIN']

const RoleBadge = ({ role }) => {
  const cls = role === 'ADMIN' ? 'badge-admin' : role === 'MANAGER' ? 'badge-manager' : 'badge-user'
  return <span className={`badge ${cls} text-xs`}>{role}</span>
}

export default function AdminPage() {
  const [users, setUsers]         = useState([])
  const [enrollments, setEnrollments] = useState([])
  const [loading, setLoading]     = useState(true)
  const [error, setError]         = useState(null)
  const [toast, setToast]         = useState(null)
  const [confirm, setConfirm]     = useState(null)
  const [tab, setTab]             = useState('users')

  // Edit user modal
  const [editModal, setEditModal] = useState(false)
  const [editUser, setEditUser]   = useState(null)
  const [editForm, setEditForm]   = useState({ fullName:'', email:'', role:'USER', enabled:true })
  const [saving, setSaving]       = useState(false)

  // Filters
  const [roleFilter, setRoleFilter] = useState('ALL')

  const loadUsers = async () => {
    try {
      setLoading(true); setError(null)
      const r = await usersApi.getAll()
      setUsers(r.data.data)
    } catch(e) { setError(e) }
    finally    { setLoading(false) }
  }

  const loadEnrollments = async () => {
    try {
      const r = await enrollmentsApi.getAll()
      setEnrollments(r.data.data)
    } catch(e) { /* ignore */ }
  }

  useEffect(() => { loadUsers(); loadEnrollments() }, [])

  const openEdit = (u) => {
    setEditUser(u)
    setEditForm({ fullName: u.fullName||'', email: u.email, role: u.role, enabled: u.enabled })
    setEditModal(true)
  }

  const saveUser = async () => {
    setSaving(true)
    try {
      await usersApi.update(editUser.id, editForm)
      setToast({ message:'User updated', type:'success' })
      setEditModal(false)
      loadUsers()
    } catch(e) { setToast({ message: e?.response?.data?.message||'Failed', type:'error' }) }
    finally    { setSaving(false) }
  }

  const deleteUser = async (id) => {
    try { await usersApi.delete(id); setToast({ message:'User deleted', type:'success' }); loadUsers() }
    catch(e) { setToast({ message: e?.response?.data?.message||'Failed', type:'error' }) }
    finally  { setConfirm(null) }
  }

  const updateEnrollStatus = async (id, status) => {
    try {
      await enrollmentsApi.updateStatus(id, status)
      setToast({ message:`Status set to ${status}`, type:'success' })
      loadEnrollments()
    } catch(e) { setToast({ message:'Failed', type:'error' }) }
  }

  const setF = k => e => setEditForm(f => ({ ...f, [k]: e.target.type === 'checkbox' ? e.target.checked : e.target.value }))

  const filteredUsers = roleFilter === 'ALL' ? users : users.filter(u => u.role === roleFilter)

  // Stats
  const stats = [
    { label: 'Total Users',       value: users.length },
    { label: 'Admins',            value: users.filter(u=>u.role==='ADMIN').length },
    { label: 'Managers',          value: users.filter(u=>u.role==='MANAGER').length },
    { label: 'Active Enrollments',value: enrollments.filter(e=>e.status==='ACTIVE').length },
  ]

  return (
    <div className="max-w-6xl mx-auto px-6 py-10">

      <div className="mb-10 animate-in">
        <p className="mono-label mb-2">Administration</p>
        <h1 className="font-display text-4xl">Admin Panel</h1>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-10 animate-in stagger-1">
        {stats.map(s => (
          <div key={s.label} className="card text-center py-5">
            <p className="font-display text-3xl mb-1">{s.value}</p>
            <p className="mono-label">{s.label}</p>
          </div>
        ))}
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-6 border-b border-white/10 animate-in stagger-2">
        {['users','enrollments'].map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`font-mono text-xs px-4 py-2.5 border-b-2 transition-all duration-150 -mb-px
              ${tab === t ? 'border-white text-white' : 'border-transparent text-white/30 hover:text-white/60'}`}>
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      <ErrorBanner error={error} />

      {/* Users tab */}
      {tab === 'users' && (
        <div className="animate-in">
          {/* Role filter */}
          <div className="flex gap-2 mb-5 flex-wrap">
            {['ALL',...ROLES].map(r => (
              <button key={r} onClick={() => setRoleFilter(r)}
                className={`font-mono text-xs px-3 py-1 border transition-all
                  ${roleFilter === r ? 'border-white text-white bg-white/10' : 'border-white/15 text-white/40 hover:border-white/40'}`}>
                {r}
              </button>
            ))}
          </div>

          {loading ? (
            <div className="flex justify-center py-16"><Spinner size={22} /></div>
          ) : filteredUsers.length === 0 ? (
            <Empty icon={Users} title="No users found" />
          ) : (
            <div className="border border-white/10 overflow-hidden">
              {/* Table header */}
              <div className="grid grid-cols-12 gap-4 px-5 py-3 bg-[#0d0d0d] border-b border-white/8">
                <span className="col-span-1 mono-label">ID</span>
                <span className="col-span-3 mono-label">Username</span>
                <span className="col-span-4 mono-label hidden md:block">Email</span>
                <span className="col-span-2 mono-label">Role</span>
                <span className="col-span-2 mono-label">Actions</span>
              </div>
              {filteredUsers.map((u, i) => (
                <div key={u.id}
                  className={`grid grid-cols-12 gap-4 px-5 py-3.5 items-center hover:bg-white/2 transition-colors
                    ${i < filteredUsers.length - 1 ? 'border-b border-white/5' : ''}`}>
                  <span className="col-span-1 font-mono text-xs text-white/25">#{u.id}</span>
                  <div className="col-span-3">
                    <p className="font-mono text-xs text-white/80">{u.username}</p>
                    {u.fullName && <p className="font-mono text-xs text-white/30 truncate">{u.fullName}</p>}
                  </div>
                  <span className="col-span-4 font-mono text-xs text-white/40 hidden md:block truncate">{u.email}</span>
                  <div className="col-span-2 flex items-center gap-1.5">
                    <RoleBadge role={u.role} />
                    {!u.enabled && <span className="badge badge-red text-xs">Disabled</span>}
                  </div>
                  <div className="col-span-2 flex items-center gap-1">
                    <button onClick={() => openEdit(u)} className="p-1.5 text-white/25 hover:text-white transition-colors">
                      <Pencil size={12} />
                    </button>
                    <button onClick={() => setConfirm({ id:u.id, label:u.username })}
                      className="p-1.5 text-white/20 hover:text-red-400 transition-colors">
                      <Trash2 size={12} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Enrollments tab */}
      {tab === 'enrollments' && (
        <div className="animate-in">
          {enrollments.length === 0 ? (
            <Empty icon={Shield} title="No enrollments" />
          ) : (
            <div className="border border-white/10 overflow-hidden">
              <div className="grid grid-cols-12 gap-4 px-5 py-3 bg-[#0d0d0d] border-b border-white/8">
                <span className="col-span-1 mono-label">ID</span>
                <span className="col-span-3 mono-label">User</span>
                <span className="col-span-4 mono-label">Course</span>
                <span className="col-span-2 mono-label">Status</span>
                <span className="col-span-2 mono-label">Change</span>
              </div>
              {enrollments.map((e, i) => (
                <div key={e.id}
                  className={`grid grid-cols-12 gap-4 px-5 py-3.5 items-center hover:bg-white/2 transition-colors
                    ${i < enrollments.length - 1 ? 'border-b border-white/5' : ''}`}>
                  <span className="col-span-1 font-mono text-xs text-white/25">#{e.id}</span>
                  <span className="col-span-3 font-mono text-xs text-white/60 truncate">{e.username}</span>
                  <span className="col-span-4 font-mono text-xs text-white/60 truncate">{e.courseTitle}</span>
                  <span className={`col-span-2 font-mono text-xs ${
                    e.status === 'ACTIVE' ? 'text-green-400/70' :
                    e.status === 'COMPLETED' ? 'text-white/60' :
                    e.status === 'CANCELLED' ? 'text-red-400/60' : 'text-white/30'
                  }`}>{e.status}</span>
                  <div className="col-span-2">
                    <select
                      value={e.status}
                      onChange={ev => updateEnrollStatus(e.id, ev.target.value)}
                      className="bg-[#1a1a1a] border border-white/10 text-white/50 font-mono text-xs px-2 py-1 outline-none hover:border-white/30 transition-colors cursor-pointer w-full">
                      {['ACTIVE','COMPLETED','SUSPENDED','CANCELLED'].map(s => (
                        <option key={s} value={s}>{s}</option>
                      ))}
                    </select>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Edit User Modal */}
      <Modal open={editModal} onClose={() => setEditModal(false)} title={`Edit — ${editUser?.username}`}>
        <div className="flex flex-col gap-4">
          <Field label="Full Name">
            <input className="input" value={editForm.fullName} onChange={setF('fullName')} placeholder="Full name" />
          </Field>
          <Field label="Email">
            <input type="email" className="input" value={editForm.email} onChange={setF('email')} />
          </Field>
          <Field label="Role">
            <select className="input" value={editForm.role} onChange={setF('role')}>
              {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </Field>
          <label className="flex items-center gap-2 font-mono text-sm text-white/60 cursor-pointer">
            <input type="checkbox" checked={editForm.enabled} onChange={setF('enabled')} className="accent-white" />
            Account enabled
          </label>
          <button onClick={saveUser} disabled={saving} className="btn-primary flex items-center justify-center gap-2 mt-2">
            {saving ? <Spinner size={13} /> : null}
            {saving ? 'Saving…' : 'Save changes'}
          </button>
        </div>
      </Modal>

      <Confirm
        open={!!confirm}
        onClose={() => setConfirm(null)}
        onConfirm={() => deleteUser(confirm.id)}
        message={`Delete user "${confirm?.label}"? This cannot be undone.`}
      />
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
