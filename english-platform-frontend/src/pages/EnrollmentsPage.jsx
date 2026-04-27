import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { enrollmentsApi } from '../services/api'
import { StatusBadge, ProgressBar, Spinner, Empty, Toast, ErrorBanner, Confirm } from '../components/UI'
import { GraduationCap, ChevronRight, X, TrendingUp } from 'lucide-react'

export default function EnrollmentsPage() {
  const navigate = useNavigate()
  const [enrollments, setEnrollments] = useState([])
  const [loading, setLoading]         = useState(true)
  const [error, setError]             = useState(null)
  const [toast, setToast]             = useState(null)
  const [confirm, setConfirm]         = useState(null)   // { id, courseTitle }
  const [updating, setUpdating]       = useState(null)   // enrollmentId being updated

  const load = async () => {
    try {
      setLoading(true); setError(null)
      const res = await enrollmentsApi.getMy()
      setEnrollments(res.data.data)
    } catch(e) { setError(e) }
    finally    { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const handleCancel = async (id) => {
    try {
      await enrollmentsApi.cancel(id)
      setToast({ message: 'Enrollment cancelled', type: 'success' })
      setConfirm(null)
      load()
    } catch(e) {
      setToast({ message: e?.response?.data?.message || 'Failed to cancel', type: 'error' })
    }
  }

  const handleProgress = async (id, value) => {
    setUpdating(id)
    try {
      await enrollmentsApi.updateProgress(id, { progressPercent: value })
      setEnrollments(prev => prev.map(e =>
        e.id === id
          ? { ...e, progressPercent: value, status: value === 100 ? 'COMPLETED' : e.status }
          : e
      ))
      if (value === 100) setToast({ message: '🎉 Course completed!', type: 'success' })
    } catch(e) {
      setToast({ message: 'Failed to update progress', type: 'error' })
    } finally { setUpdating(null) }
  }

  const active    = enrollments.filter(e => e.status === 'ACTIVE')
  const completed = enrollments.filter(e => e.status === 'COMPLETED')
  const other     = enrollments.filter(e => !['ACTIVE','COMPLETED'].includes(e.status))

  const EnrollmentCard = ({ e }) => (
    <div className="card flex flex-col gap-4">
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          <h3 className="font-display text-lg leading-tight truncate">{e.courseTitle}</h3>
          <div className="flex items-center gap-2 mt-1.5">
            <StatusBadge status={e.status} />
            <span className="font-mono text-xs text-white/25">
              Enrolled {new Date(e.enrolledAt).toLocaleDateString()}
            </span>
          </div>
        </div>
        {e.status === 'ACTIVE' && (
          <button
            onClick={() => setConfirm({ id: e.id, courseTitle: e.courseTitle })}
            className="text-white/20 hover:text-red-400 transition-colors shrink-0"
            title="Cancel enrollment"
          >
            <X size={14} />
          </button>
        )}
      </div>

      {/* Progress */}
      <div>
        <div className="flex justify-between items-center mb-1.5">
          <span className="mono-label">Progress</span>
          <span className="font-mono text-xs text-white/40">{e.progressPercent}%</span>
        </div>
        <ProgressBar value={e.progressPercent} />
      </div>

      {/* Progress updater (active only) */}
      {e.status === 'ACTIVE' && (
        <div className="flex items-center gap-2 pt-1">
          <TrendingUp size={11} className="text-white/25 shrink-0" />
          <input
            type="range" min="0" max="100" step="5"
            defaultValue={e.progressPercent}
            disabled={updating === e.id}
            onMouseUp={ev => handleProgress(e.id, Number(ev.target.value))}
            onTouchEnd={ev => handleProgress(e.id, Number(ev.target.value))}
            className="flex-1 accent-white cursor-pointer"
          />
          {updating === e.id && <Spinner size={12} />}
        </div>
      )}

      {e.completedAt && (
        <p className="font-mono text-xs text-white/25">
          Completed {new Date(e.completedAt).toLocaleDateString()}
        </p>
      )}

      <button
        onClick={() => navigate(`/courses/${e.courseId}`)}
        className="btn-ghost flex items-center justify-center gap-1.5 text-xs py-2 mt-auto"
      >
        View course <ChevronRight size={12} />
      </button>
    </div>
  )

  return (
    <div className="max-w-7xl mx-auto px-6 py-10">

      <div className="mb-10 animate-in">
        <p className="mono-label mb-2">Learning</p>
        <h1 className="font-display text-4xl mb-3">My Courses</h1>
        <p className="font-mono text-sm text-white/40">
          {enrollments.length} enrollment{enrollments.length !== 1 ? 's' : ''} total
        </p>
      </div>

      <ErrorBanner error={error} />

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size={24} /></div>
      ) : enrollments.length === 0 ? (
        <Empty icon={GraduationCap} title="No enrollments yet" subtitle="Browse courses and enroll to get started" />
      ) : (
        <div className="space-y-10">
          {active.length > 0 && (
            <section className="animate-in stagger-1">
              <p className="mono-label mb-4">In Progress — {active.length}</p>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {active.map(e => <EnrollmentCard key={e.id} e={e} />)}
              </div>
            </section>
          )}
          {completed.length > 0 && (
            <section className="animate-in stagger-2">
              <p className="mono-label mb-4">Completed — {completed.length}</p>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {completed.map(e => <EnrollmentCard key={e.id} e={e} />)}
              </div>
            </section>
          )}
          {other.length > 0 && (
            <section className="animate-in stagger-3">
              <p className="mono-label mb-4">Other — {other.length}</p>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {other.map(e => <EnrollmentCard key={e.id} e={e} />)}
              </div>
            </section>
          )}
        </div>
      )}

      <Confirm
        open={!!confirm}
        onClose={() => setConfirm(null)}
        onConfirm={() => handleCancel(confirm.id)}
        message={`Cancel enrollment in "${confirm?.courseTitle}"?`}
      />
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
