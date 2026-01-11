import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const AdminRoute = ({ children }) => {
  const { user, loading, isAdmin } = useAuth()

  if (loading) {
    return <div className="text-center py-8">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  if (!isAdmin()) {
    return <Navigate to="/" />
  }

  return children
}

export default AdminRoute