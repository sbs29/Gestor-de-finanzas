import { Link, useNavigate } from "react-router-dom";
import { sessionService } from "../services/sessionService";

function Navbar() {
  const navigate = useNavigate();
  const isAuthenticated = sessionService.isAuthenticated();

  const handleLogout = () => {
    sessionService.logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-logo">
        Finanzas Web
      </Link>

      <div className="navbar-links">
        <Link to="/">Inicio</Link>

        {isAuthenticated ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/categories">Categorías</Link>
            <Link to="/transactions">Transacciones</Link>
            <button onClick={handleLogout} className="secondary-button">
              Logout
            </button>
          </>
        ) : (
          <Link to="/login">Login</Link>
        )}
      </div>
    </nav>
  );
}

export default Navbar;