import { useEffect, useState } from 'react'
import { getCurrentUserProfile } from '../services/userService'
import type { UserProfile } from '../types/UserProfile'

function getInitials(name: string) {
  return name
    .trim()
    .split(' ')
    .filter(Boolean)
    .map((word) => word[0])
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

function getRoleLabel(role: string) {
  if (role === 'USER') return 'Usuario'
  if (role === 'ADMIN') return 'Administrador'

  return role
}

function ProfilePage() {
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadProfile() {
      try {
        setLoading(true)
        setError('')

        const data = await getCurrentUserProfile()
        setProfile(data)
      } catch {
        setError('No se pudo cargar la información del perfil.')
      } finally {
        setLoading(false)
      }
    }

    loadProfile()
  }, [])

  if (loading) {
    return <p>Cargando perfil...</p>
  }

  if (error) {
    return <p className="error-message">{error}</p>
  }

  if (!profile) {
    return <p>No hay información de perfil disponible.</p>
  }

  return (
    <section className="page-container profile-page">
      <div className="page-header">
        <div>
          <h1>Mi Perfil</h1>
          <p>Administra la información asociada a tu cuenta.</p>
        </div>
      </div>

      <div className="profile-card">
        <div className="profile-summary">
          <div className="profile-avatar">{getInitials(profile.name)}</div>

          <div>
            <h2>{profile.name}</h2>
            <p>{profile.email}</p>
          </div>
        </div>

        <div className="profile-details">
          <div>
            <span>ID de usuario</span>
            <strong>{profile.id}</strong>
          </div>

          <div>
            <span>Rol</span>
            <strong>{getRoleLabel(profile.role)}</strong>
          </div>

          <div>
            <span>Estado</span>
            <strong className="status-badge">
              <span className="status-dot" />
              Sesión activa
            </strong>
          </div>
        </div>
      </div>

      <div className="profile-future-card">
        <div>
          <h2>Funciones disponibles próximamente</h2>
          <p>
            En una futura actualización podrás editar tu nombre, cambiar tu
            contraseña y gestionar opciones avanzadas de tu cuenta.
          </p>
        </div>

        <span className="future-badge">Próximamente</span>
      </div>
    </section>
  )
}

export default ProfilePage