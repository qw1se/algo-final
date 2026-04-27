import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT token to every request
api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('token')
  if (token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

// Handle 401 globally
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// ─── Auth ───────────────────────────────────────────
export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login:    (data) => api.post('/auth/login', data),
}

// ─── Courses ─────────────────────────────────────────
export const coursesApi = {
  getActive:  ()       => api.get('/courses'),
  getAll:     ()       => api.get('/courses/all'),
  getById:    (id)     => api.get(`/courses/${id}`),
  getByLevel: (level)  => api.get(`/courses/level/${level}`),
  create:     (data)   => api.post('/courses', data),
  update:     (id, d)  => api.put(`/courses/${id}`, d),
  delete:     (id)     => api.delete(`/courses/${id}`),
  toggle:     (id)     => api.patch(`/courses/${id}/toggle`),
}

// ─── Lessons ─────────────────────────────────────────
export const lessonsApi = {
  getByCourse:    (cid)    => api.get(`/lessons/course/${cid}`),
  getAllByCourse: (cid)    => api.get(`/lessons/course/${cid}/all`),
  getById:        (id)     => api.get(`/lessons/${id}`),
  create:         (data)   => api.post('/lessons', data),
  update:         (id, d)  => api.put(`/lessons/${id}`, d),
  delete:         (id)     => api.delete(`/lessons/${id}`),
  togglePublish:  (id)     => api.patch(`/lessons/${id}/toggle-publish`),
}

// ─── Enrollments ──────────────────────────────────────
export const enrollmentsApi = {
  enroll:       (data)         => api.post('/enrollments', data),
  getMy:        ()             => api.get('/enrollments/my'),
  getAll:       ()             => api.get('/enrollments'),
  getById:      (id)           => api.get(`/enrollments/${id}`),
  getByCourse:  (cid)          => api.get(`/enrollments/course/${cid}`),
  updateProgress: (id, data)   => api.patch(`/enrollments/${id}/progress`, data),
  cancel:       (id)           => api.patch(`/enrollments/${id}/cancel`),
  updateStatus: (id, status)   => api.patch(`/enrollments/${id}/status?status=${status}`),
}

// ─── Users (Admin) ────────────────────────────────────
export const usersApi = {
  getMe:      ()          => api.get('/users/me'),
  getAll:     ()          => api.get('/admin/users'),
  getById:    (id)        => api.get(`/admin/users/${id}`),
  getByRole:  (role)      => api.get(`/admin/users/role/${role}`),
  update:     (id, data)  => api.put(`/admin/users/${id}`, data),
  delete:     (id)        => api.delete(`/admin/users/${id}`),
}

export default api
