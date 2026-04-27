import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Navbar from './components/Navbar'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import CoursesPage from './pages/CoursesPage'
import CourseDetailPage from './pages/CourseDetailPage'
import EnrollmentsPage from './pages/EnrollmentsPage'
import ManagePage from './pages/ManagePage'
import AdminPage from './pages/AdminPage'
import { Spinner } from './components/UI'

function ProtectedRoute({ children, requireManager, requireAdmin }) {
  const { user, loading, isManager, isAdmin } = useAuth()
  if (loading) return <div className="flex justify-center items-center min-h-screen"><Spinner size={24} /></div>
  if (!user) return <Navigate to="/login" replace />
  if (requireAdmin && !isAdmin) return <Navigate to="/courses" replace />
  if (requireManager && !isManager) return <Navigate to="/courses" replace />
  return children
}

function AppRoutes() {
  const { user } = useAuth()
  return (
    <>
      <Navbar />
      <main className="pt-14">
        <Routes>
          <Route path="/" element={<Navigate to="/courses" replace />} />
          <Route path="/login"    element={user ? <Navigate to="/courses" replace /> : <LoginPage />} />
          <Route path="/register" element={user ? <Navigate to="/courses" replace /> : <RegisterPage />} />
          <Route path="/courses"     element={<CoursesPage />} />
          <Route path="/courses/:id" element={<CourseDetailPage />} />
          <Route path="/enrollments" element={
            <ProtectedRoute><EnrollmentsPage /></ProtectedRoute>
          } />
          <Route path="/manage" element={
            <ProtectedRoute requireManager><ManagePage /></ProtectedRoute>
          } />
          <Route path="/admin" element={
            <ProtectedRoute requireAdmin><AdminPage /></ProtectedRoute>
          } />
          <Route path="*" element={<Navigate to="/courses" replace />} />
        </Routes>
      </main>
    </>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  )
}
