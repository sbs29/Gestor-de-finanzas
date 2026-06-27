import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { register } from '../services/authService'
import { sessionService } from '../services/sessionService'

function RegisterPage() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  const navigate = useNavigate()

  if (sessionService.isAuthenticated()) {
    return <Navigate to="/dashboard" replace />
  }

  async function handleRegister() {
    try {
      await register({
        name,
        email,
        password
      })

      setMessage('Registro correcto. Ya puedes iniciar sesión.')

      setTimeout(() => {
        navigate('/login')
      }, 800)
    } catch (error) {
      setMessage('No se pudo crear la cuenta')
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Crear cuenta</h1>
          <p>Regístrate para empezar a gestionar tus finanzas personales.</p>
        </div>
      </div>

      <section className="card">
        <h2>Registro</h2>

        <form
          onSubmit={(event) => {
            event.preventDefault()
            handleRegister()
          }}
        >
          <div>
            <label htmlFor="name">Nombre</label>

            <input
              id="name"
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Tu nombre"
            />
          </div>

          <div>
            <label htmlFor="register-email">Email</label>

            <input
              id="register-email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="tu@email.com"
            />
          </div>

          <div>
            <label htmlFor="register-password">Password</label>

            <input
              id="register-password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Password"
            />
          </div>

          <button type="submit" className="primary-button">
            Crear cuenta
          </button>
        </form>

        {message && (
          <p
            className={
              message.includes('correcto')
                ? 'form-message'
                : 'error-message'
            }
          >
            {message}
          </p>
        )}

        <p className="auth-switch">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
        </p>
      </section>
    </section>
  )
}

export default RegisterPage