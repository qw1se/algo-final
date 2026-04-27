import React, { useState, useEffect } from 'react'
import { coursesApi, lessonsApi } from '../services/api'
import { LevelBadge, Spinner, Empty, Toast, ErrorBanner, Confirm, Modal, Field } from '../components/UI'
import {
  Plus, Pencil, Trash2, ToggleLeft, ToggleRight,
  BookOpen, Eye, EyeOff, ChevronDown, ChevronRight, Layers
} from 'lucide-react'

const LEVELS = ['BEGINNER','ELEMENTARY','INTERMEDIATE','UPPER_INTERMEDIATE','ADVANCED','PROFICIENCY']
const emptyC = { title:'', description:'', level:'BEGINNER', active:true }
const emptyL = { title:'', content:'', orderIndex:1, durationMinutes:30, published:false, courseId:'' }

export default function ManagePage() {
  const [courses, setCourses]       = useState([])
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState(null)
  const [toast, setToast]           = useState(null)
  const [confirm, setConfirm]       = useState(null)
  const [expanded, setExpanded]     = useState({})         // courseId → bool
  const [lessons, setLessons]       = useState({})          // courseId → lessons[]

  // Course modal
  const [courseModal, setCourseModal] = useState(false)
  const [courseEdit, setCourseEdit]   = useState(null)      // null = create
  const [courseForm, setCourseForm]   = useState(emptyC)
  const [saving, setSaving]           = useState(false)

  // Lesson modal
  const [lessonModal, setLessonModal] = useState(false)
  const [lessonEdit, setLessonEdit]   = useState(null)
  const [lessonForm, setLessonForm]   = useState(emptyL)

  const loadCourses = async () => {
    try { setLoading(true); const r = await coursesApi.getAll(); setCourses(r.data.data) }
    catch(e) { setError(e) }
    finally  { setLoading(false) }
  }

  const loadLessons = async (cid) => {
    const r = await lessonsApi.getAllByCourse(cid)
    setLessons(p => ({ ...p, [cid]: r.data.data }))
  }

  useEffect(() => { loadCourses() }, [])

  const toggleExpand = async (cid) => {
    const next = !expanded[cid]
    setExpanded(p => ({ ...p, [cid]: next }))
    if (next && !lessons[cid]) await loadLessons(cid)
  }

  // ─── Course CRUD ───────────────────────────────────
  const openCreateCourse = () => { setCourseEdit(null); setCourseForm(emptyC); setCourseModal(true) }
  const openEditCourse   = (c)  => { setCourseEdit(c); setCourseForm({ title:c.title, description:c.description||'', level:c.level, active:c.active }); setCourseModal(true) }

  const saveCourse = async () => {
    setSaving(true)
    try {
      if (courseEdit) await coursesApi.update(courseEdit.id, courseForm)
      else            await coursesApi.create(courseForm)
      setToast({ message: courseEdit ? 'Course updated' : 'Course created', type:'success' })
      setCourseModal(false)
      loadCourses()
    } catch(e) { setToast({ message: e?.response?.data?.message||'Failed', type:'error' }) }
    finally    { setSaving(false) }
  }

  const deleteCourse = async (id) => {
    try { await coursesApi.delete(id); setToast({ message:'Course deleted', type:'success' }); loadCourses() }
    catch(e) { setToast({ message: e?.response?.data?.message||'Failed', type:'error' }) }
    finally  { setConfirm(null) }
  }

  const toggleCourse = async (c) => {
    try { await coursesApi.toggle(c.id); loadCourses() }
    catch(e) { setToast({ message:'Failed', type:'error' }) }
  }

  // ─── Lesson CRUD ──────────────────────────────────
  const openCreateLesson = (cid) => { setLessonEdit(null); setLessonForm({ ...emptyL, courseId: cid }); setLessonModal(true) }
  const openEditLesson   = (l)   => { setLessonEdit(l); setLessonForm({ title:l.title, content:l.content||'', orderIndex:l.orderIndex, durationMinutes:l.durationMinutes||30, published:l.published, courseId:l.courseId }); setLessonModal(true) }

  const saveLesson = async () => {
    setSaving(true)
    try {
      if (lessonEdit) await lessonsApi.update(lessonEdit.id, lessonForm)
      else            await lessonsApi.create(lessonForm)
      setToast({ message: lessonEdit ? 'Lesson updated' : 'Lesson created', type:'success' })
      setLessonModal(false)
      await loadLessons(lessonForm.courseId)
    } catch(e) { setToast({ message: e?.response?.data?.message||'Failed', type:'error' }) }
    finally    { setSaving(false) }
  }

  const deleteLesson = async (l) => {
    try {
      await lessonsApi.delete(l.id)
      setToast({ message:'Lesson deleted', type:'success' })
      await loadLessons(l.courseId)
    } catch(e) { setToast({ message:'Failed', type:'error' }) }
    finally    { setConfirm(null) }
  }

  const togglePublish = async (l) => {
    try { await lessonsApi.togglePublish(l.id); await loadLessons(l.courseId) }
    catch(e) { setToast({ message:'Failed', type:'error' }) }
  }

  const setC = k => e => setCourseForm(f => ({ ...f, [k]: e.target.value }))
  const setL = k => e => setLessonForm(f => ({ ...f, [k]: e.target.type === 'checkbox' ? e.target.checked : e.target.value }))

  return (
    <div className="max-w-5xl mx-auto px-6 py-10">

      <div className="flex items-end justify-between mb-10 animate-in">
        <div>
          <p className="mono-label mb-2">Manager</p>
          <h1 className="font-display text-4xl">Manage Content</h1>
        </div>
        <button onClick={openCreateCourse} className="btn-primary flex items-center gap-2">
          <Plus size={14} /> New Course
        </button>
      </div>

      <ErrorBanner error={error} />

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size={24} /></div>
      ) : courses.length === 0 ? (
        <Empty icon={BookOpen} title="No courses yet" subtitle="Create your first course" />
      ) : (
        <div className="flex flex-col gap-3 animate-in stagger-1">
          {courses.map(course => (
            <div key={course.id} className="border border-white/10 bg-[#111]">
              {/* Course row */}
              <div className="flex items-center gap-4 px-5 py-4 hover:bg-white/2 transition-colors">
                <button onClick={() => toggleExpand(course.id)} className="text-white/30 hover:text-white transition-colors">
                  {expanded[course.id] ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
                </button>

                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-mono text-sm text-white/90">{course.title}</span>
                    <LevelBadge level={course.level} />
                    {!course.active && <span className="badge badge-red text-xs">Inactive</span>}
                  </div>
                  <div className="flex gap-3 mt-1">
                    <span className="font-mono text-xs text-white/25">{course.lessonCount} lessons</span>
                    <span className="font-mono text-xs text-white/25">{course.enrollmentCount} enrolled</span>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center gap-1 shrink-0">
                  <button onClick={() => toggleCourse(course)} title={course.active ? 'Deactivate' : 'Activate'}
                    className="p-2 text-white/30 hover:text-white transition-colors">
                    {course.active ? <ToggleRight size={16} /> : <ToggleLeft size={16} />}
                  </button>
                  <button onClick={() => openEditCourse(course)} className="p-2 text-white/30 hover:text-white transition-colors">
                    <Pencil size={13} />
                  </button>
                  <button onClick={() => setConfirm({ type:'course', id:course.id, label:course.title })}
                    className="p-2 text-white/20 hover:text-red-400 transition-colors">
                    <Trash2 size={13} />
                  </button>
                </div>
              </div>

              {/* Lessons panel */}
              {expanded[course.id] && (
                <div className="border-t border-white/8 bg-[#0d0d0d]">
                  <div className="flex items-center justify-between px-8 py-3">
                    <span className="mono-label">Lessons</span>
                    <button onClick={() => openCreateLesson(course.id)} className="flex items-center gap-1 font-mono text-xs text-white/40 hover:text-white transition-colors">
                      <Plus size={11} /> Add lesson
                    </button>
                  </div>

                  {!lessons[course.id] ? (
                    <div className="px-8 py-4"><Spinner size={14} /></div>
                  ) : lessons[course.id].length === 0 ? (
                    <p className="px-8 py-4 font-mono text-xs text-white/25">No lessons yet</p>
                  ) : (
                    <div className="flex flex-col">
                      {lessons[course.id].map(l => (
                        <div key={l.id} className="flex items-center gap-4 px-8 py-3 border-t border-white/5 hover:bg-white/2 transition-colors group">
                          <span className="font-mono text-xs text-white/20 w-5 text-right shrink-0">{l.orderIndex}</span>
                          <span className="flex-1 font-mono text-xs text-white/70 truncate">{l.title}</span>
                          {l.durationMinutes && <span className="font-mono text-xs text-white/20 shrink-0">{l.durationMinutes}m</span>}
                          <div className="flex items-center gap-1 shrink-0 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button onClick={() => togglePublish(l)} title={l.published ? 'Unpublish':'Publish'}
                              className="p-1.5 text-white/30 hover:text-white transition-colors">
                              {l.published ? <Eye size={12} /> : <EyeOff size={12} />}
                            </button>
                            <button onClick={() => openEditLesson(l)} className="p-1.5 text-white/30 hover:text-white transition-colors">
                              <Pencil size={11} />
                            </button>
                            <button onClick={() => setConfirm({ type:'lesson', item: l, label:l.title })}
                              className="p-1.5 text-white/20 hover:text-red-400 transition-colors">
                              <Trash2 size={11} />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Course Modal */}
      <Modal open={courseModal} onClose={() => setCourseModal(false)} title={courseEdit ? 'Edit Course' : 'New Course'}>
        <div className="flex flex-col gap-4">
          <Field label="Title">
            <input className="input" value={courseForm.title} onChange={setC('title')} placeholder="Course title" />
          </Field>
          <Field label="Description">
            <textarea className="input h-24 resize-none" value={courseForm.description} onChange={setC('description')} placeholder="Optional description" />
          </Field>
          <Field label="Level">
            <select className="input" value={courseForm.level} onChange={setC('level')}>
              {LEVELS.map(l => <option key={l} value={l}>{l}</option>)}
            </select>
          </Field>
          <label className="flex items-center gap-2 font-mono text-sm text-white/60 cursor-pointer">
            <input type="checkbox" checked={courseForm.active} onChange={e => setCourseForm(f=>({...f,active:e.target.checked}))} className="accent-white" />
            Active
          </label>
          <button onClick={saveCourse} disabled={saving} className="btn-primary flex items-center justify-center gap-2 mt-2">
            {saving ? <Spinner size={13} /> : null}
            {saving ? 'Saving…' : courseEdit ? 'Save changes' : 'Create course'}
          </button>
        </div>
      </Modal>

      {/* Lesson Modal */}
      <Modal open={lessonModal} onClose={() => setLessonModal(false)} title={lessonEdit ? 'Edit Lesson' : 'New Lesson'}>
        <div className="flex flex-col gap-4">
          <Field label="Title">
            <input className="input" value={lessonForm.title} onChange={setL('title')} placeholder="Lesson title" />
          </Field>
          <Field label="Content">
            <textarea className="input h-28 resize-none" value={lessonForm.content} onChange={setL('content')} placeholder="Lesson content (optional)" />
          </Field>
          <div className="grid grid-cols-2 gap-4">
            <Field label="Order Index">
              <input type="number" className="input" min="1" value={lessonForm.orderIndex} onChange={setL('orderIndex')} />
            </Field>
            <Field label="Duration (min)">
              <input type="number" className="input" min="1" value={lessonForm.durationMinutes} onChange={setL('durationMinutes')} />
            </Field>
          </div>
          <label className="flex items-center gap-2 font-mono text-sm text-white/60 cursor-pointer">
            <input type="checkbox" checked={lessonForm.published} onChange={setL('published')} className="accent-white" />
            Published
          </label>
          <button onClick={saveLesson} disabled={saving} className="btn-primary flex items-center justify-center gap-2 mt-2">
            {saving ? <Spinner size={13} /> : null}
            {saving ? 'Saving…' : lessonEdit ? 'Save changes' : 'Create lesson'}
          </button>
        </div>
      </Modal>

      {/* Confirm */}
      <Confirm
        open={!!confirm}
        onClose={() => setConfirm(null)}
        onConfirm={() => confirm?.type === 'course' ? deleteCourse(confirm.id) : deleteLesson(confirm.item)}
        message={`Delete "${confirm?.label}"? This cannot be undone.`}
      />
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
