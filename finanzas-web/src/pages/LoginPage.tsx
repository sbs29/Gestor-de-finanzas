import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../services/authService'
import { Navigate } from "react-router-dom";
import { sessionService } from "../services/sessionService";

function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const navigate = useNavigate()

  if (sessionService.isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }

  async function handleLogin() {
    try {
      const response = await login({
        email,
        password
      })

      localStorage.setItem('token', response.token)

      setMessage('Login correcto. Token guardado.')

      navigate('/transactions')
    } catch (error) {
      setMessage('Login incorrecto')
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Login</h1>
          <p>Accede a tu cuenta para gestionar tus finanzas.</p>
        </div>
      </div>

      <section className="card">
        <h2>Iniciar sesión</h2>

        <form
          onSubmit={(event) => {
            event.preventDefault()
            handleLogin()
          }}
        >
          <div>
            <label htmlFor="email">Email</label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="Email"
            />
          </div>

          <div>
            <label htmlFor="password">Password</label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Password"
            />
          </div>

          <button type="submit" className='primary-button'>
            Login
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
          ¿No tienes cuenta? <Link to="/register">Crear cuenta</Link>
        </p>
      </section>
    </section>
  )
}

export default LoginPage