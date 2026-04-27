import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { coursesApi, enrollmentsApi } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { LevelBadge, Spinner, Empty, Toast, ErrorBanner } from '../components/UI'
import { BookOpen, Users, Layers, ChevronRight, Filter } from 'lucide-react'

const LEVELS = ['ALL','BEGINNER','ELEMENTARY','INTERMEDIATE','UPPER_INTERMEDIATE','ADVANCED','PROFICIENCY']

export default function CoursesPage() {
  const { user, isManager } = useAuth()
  const navigate = useNavigate()
  const [courses, setCourses]   = useState([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState(null)
  const [level, setLevel]       = useState('ALL')
  const [toast, setToast]       = useState(null)
  const [enrolling, setEnrolling] = useState(null)

  const load = async () => {
    try {
      setLoading(true); setError(null)
      const res = await coursesApi.getActive()
      setCourses(res.data.data)
    } catch(e) { setError(e) }
    finally    { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const filtered = level === 'ALL' ? courses : courses.filter(c => c.level === level)

  const handleEnroll = async (courseId) => {
    if (!user) { navigate('/login'); return }
    setEnrolling(courseId)
    try {
      await enrollmentsApi.enroll({ courseId })
      setToast({ message: 'Enrolled successfully!', type: 'success' })
    } catch(e) {
      const msg = e?.response?.data?.message || 'Enrollment failed'
      setToast({ message: msg, type: 'error' })
    } finally {
      setEnrolling(null)
    }
  }

  return (
    <div className="max-w-7xl mx-auto px-6 py-10">

      {/* Header */}
      <div className="mb-10 animate-in">
        <p className="mono-label mb-2">Catalog</p>
        <h1 className="font-display text-4xl mb-3">English Courses</h1>
        <p className="font-mono text-sm text-white/40 max-w-md">
          Browse all available courses. Enroll to start tracking your progress.
        </p>
      </div>

      {/* Level filter */}
      <div className="flex items-center gap-2 mb-8 flex-wrap animate-in stagger-1">
        <Filter size={12} className="text-white/30" />
        {LEVELS.map(l => (
          <button
            key={l}
            onClick={() => setLevel(l)}
            className={`font-mono text-xs px-3 py-1 border transition-all duration-150
              ${level === l
                ? 'border-white text-white bg-white/10'
                : 'border-white/15 text-white/40 hover:border-white/40 hover:text-white/70'}`}
          >
            {l === 'ALL' ? 'All' : l === 'UPPER_INTERMEDIATE' ? 'Upper-Int' : l.charAt(0) + l.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      <ErrorBanner error={error} />

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size={24} /></div>
      ) : filtered.length === 0 ? (
        <Empty icon={BookOpen} title="No courses found" subtitle="Try a different level filter" />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 animate-in stagger-2">
          {filtered.map((course, i) => (
            <div key={course.id} className="card flex flex-col gap-4 group" style={{ animationDelay: `${i * 0.05}s` }}>
              {/* Top */}
              <div className="flex items-start justify-between gap-3">
                <div className="flex-1 min-w-0">
                  <LevelBadge level={course.level} />
                  <h2 className="font-display text-lg mt-2 leading-tight">{course.title}</h2>
                </div>
                <div className="w-8 h-8 border border-white/15 flex items-center justify-center shrink-0 group-hover:border-white/40 transition-colors">
                  <BookOpen size={14} className="text-white/40" />
                </div>
              </div>

              {/* Description */}
              {course.description && (
                <p className="font-mono text-xs text-white/40 leading-relaxed line-clamp-2">
                  {course.description}
                </p>
              )}

              {/* Stats */}
              <div className="flex items-center gap-4 pt-2 border-t border-white/8">
                <span className="flex items-center gap-1.5 font-mono text-xs text-white/30">
                  <Layers size={11} /> {course.lessonCount} lessons
                </span>
                <span className="flex items-center gap-1.5 font-mono text-xs text-white/30">
                  <Users size={11} /> {course.enrollmentCount} enrolled
                </span>
              </div>

              {/* Actions */}
              <div className="flex gap-2 mt-auto">
                <button
                  onClick={() => navigate(`/courses/${course.id}`)}
                  className="btn-ghost flex-1 flex items-center justify-center gap-1.5 text-xs py-2"
                >
                  View <ChevronRight size={12} />
                </button>
                {user && (
                  <button
                    onClick={() => handleEnroll(course.id)}
                    disabled={enrolling === course.id}
                    className="btn-primary flex items-center justify-center gap-1.5 text-xs py-2 px-4"
                  >
                    {enrolling === course.id ? <Spinner size={12} /> : 'Enroll'}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {toast && (
        <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />
      )}
    </div>
  )
}
