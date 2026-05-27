import { useState } from 'react'
import { login } from '../services/authService'

function LoginPage() {
  const [email, setEmail] = useState('sebas@gmail.com')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  async function handleLogin() {
    try {
      const response = await login({
        email,
        password
      })

      localStorage.setItem('token', response.token)

      setMessage('Login correcto. Token guardado.')
    } catch (error) {
      setMessage('Login incorrecto')
    }
  }

  return (
    <>
      <h1>Login</h1>

      <input
        type="email"
        value={email}
        onChange={(event) => setEmail(event.target.value)}
        placeholder="Email"
      />

      <input
        type="password"
        value={password}
        onChange={(event) => setPassword(event.target.value)}
        placeholder="Password"
      />

      <button onClick={handleLogin}>
        Login
      </button>

      {message && <p>{message}</p>}
    </>
  )
}

export default LoginPage