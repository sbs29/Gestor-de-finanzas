import { Link, useNavigate } from 'react-router-dom'

function Navbar() {
  const navigate = useNavigate()
  const token = localStorage.getItem('token')

  function handleLogout() {
    localStorage.removeItem('token')
    navigate('/login')
  }

  return (
    <header className="app-header">
      <div className="app-header__content">
        <Link to="/" className="app-logo">
          Finanzas App
        </Link>

        <nav className="app-nav">
          <Link to="/">Home</Link>
          <Link to="/login">Login</Link>

          {token && (
            <>
              <Link to="/dashboard">Dashboard</Link>
              <Link to="/transactions">Transactions</Link>
              <Link to="/categories">Categories</Link>

              <button type="button" onClick={handleLogout}>
                Logout
              </button>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}

export default Navbar