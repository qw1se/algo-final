import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { coursesApi, lessonsApi, enrollmentsApi } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { LevelBadge, Spinner, Empty, Toast, ErrorBanner } from '../components/UI'
import { ArrowLeft, Clock, BookOpen, ChevronRight, Eye, EyeOff } from 'lucide-react'

export default function CourseDetailPage() {
  const { id } = useParams()
  const { user, isManager } = useAuth()
  const navigate = useNavigate()

  const [course, setCourse]   = useState(null)
  const [lessons, setLessons] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError]     = useState(null)
  const [toast, setToast]     = useState(null)
  const [enrolling, setEnrolling] = useState(false)

  useEffect(() => {
    const fetchAll = async () => {
      try {
        setLoading(true)
        const [cRes, lRes] = await Promise.all([
          coursesApi.getById(id),
          isManager ? lessonsApi.getAllByCourse(id) : lessonsApi.getByCourse(id)
        ])
        setCourse(cRes.data.data)
        setLessons(lRes.data.data)
      } catch(e) { setError(e) }
      finally    { setLoading(false) }
    }
    fetchAll()
  }, [id, isManager])

  const handleEnroll = async () => {
    if (!user) { navigate('/login'); return }
    setEnrolling(true)
    try {
      await enrollmentsApi.enroll({ courseId: Number(id) })
      setToast({ message: 'Enrolled! Go to My Learning to track progress.', type: 'success' })
    } catch(e) {
      setToast({ message: e?.response?.data?.message || 'Failed', type: 'error' })
    } finally { setEnrolling(false) }
  }

  if (loading) return <div className="flex justify-center py-32"><Spinner size={24} /></div>
  if (error)   return <div className="max-w-3xl mx-auto px-6 py-10"><ErrorBanner error={error} /></div>
  if (!course) return null

  const totalMinutes = lessons.reduce((s, l) => s + (l.durationMinutes || 0), 0)

  return (
    <div className="max-w-4xl mx-auto px-6 py-10 animate-in">

      {/* Back */}
      <button onClick={() => navigate('/courses')}
        className="flex items-center gap-1.5 font-mono text-xs text-white/30 hover:text-white transition-colors mb-8">
        <ArrowLeft size={12} /> Back to courses
      </button>

      {/* Course header */}
      <div className="card mb-6">
        <div className="flex items-start justify-between gap-6 flex-wrap">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-3">
              <LevelBadge level={course.level} />
              {!course.active && <span className="badge badge-red">Inactive</span>}
            </div>
            <h1 className="font-display text-3xl mb-3">{course.title}</h1>
            {course.description && (
              <p className="font-mono text-sm text-white/40 leading-relaxed max-w-xl">{course.description}</p>
            )}
          </div>

          {user && (
            <button onClick={handleEnroll} disabled={enrolling} className="btn-primary flex items-center gap-2 shrink-0">
              {enrolling ? <Spinner size={13} /> : <BookOpen size={13} />}
              Enroll now
            </button>
          )}
        </div>

        {/* Meta */}
        <div className="flex items-center gap-6 mt-6 pt-4 border-t border-white/8 flex-wrap">
          <span className="flex items-center gap-1.5 font-mono text-xs text-white/30">
            <BookOpen size={11} /> {lessons.length} lesson{lessons.length !== 1 ? 's' : ''}
          </span>
          {totalMinutes > 0 && (
            <span className="flex items-center gap-1.5 font-mono text-xs text-white/30">
              <Clock size={11} /> {Math.floor(totalMinutes / 60)}h {totalMinutes % 60}m total
            </span>
          )}
        </div>
      </div>

      {/* Lessons */}
      <div>
        <p className="mono-label mb-4">Lessons</p>
        {lessons.length === 0 ? (
          <Empty icon={BookOpen} title="No lessons yet" subtitle="Lessons will appear here when published" />
        ) : (
          <div className="flex flex-col gap-2">
            {lessons.map((lesson, i) => (
              <div key={lesson.id}
                className="flex items-center gap-4 px-5 py-4 bg-[#111] border border-white/10 hover:border-white/25 transition-all group">

                {/* Index */}
                <span className="font-mono text-xs text-white/20 w-6 shrink-0 text-right">{lesson.orderIndex}</span>

                {/* Title */}
                <div className="flex-1 min-w-0">
                  <p className="font-mono text-sm text-white/80 group-hover:text-white transition-colors truncate">
                    {lesson.title}
                  </p>
                </div>

                {/* Duration */}
                {lesson.durationMinutes && (
                  <span className="flex items-center gap-1 font-mono text-xs text-white/25 shrink-0">
                    <Clock size={10} /> {lesson.durationMinutes}m
                  </span>
                )}

                {/* Published state (managers see toggle) */}
                {isManager && (
                  <span className={`shrink-0 ${lesson.published ? 'text-white/30' : 'text-white/15'}`}>
                    {lesson.published ? <Eye size={12} /> : <EyeOff size={12} />}
                  </span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
