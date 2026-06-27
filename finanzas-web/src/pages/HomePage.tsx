import { Link } from "react-router-dom";
import { sessionService } from "../services/sessionService";

function HomePage() {
  const isAuthenticated = sessionService.isAuthenticated();

  return (
    <section className="home-page">
      <div className="home-hero">
        {isAuthenticated ? (
          <>
            <span className="home-eyebrow">Panel personal</span>

            <h1>Bienvenido de nuevo</h1>

            <p>
              Accede rápidamente a tu dashboard financiero, gestiona tus
              categorías y registra nuevos movimientos.
            </p>

            <div className="home-actions">
              <Link to="/dashboard" className="primary-button">
                Ir al dashboard
              </Link>

              <Link to="/transactions" className="secondary-button">
                Ver transacciones
              </Link>
            </div>
          </>
        ) : (
          <>
            <span className="home-eyebrow">Finanzas personales</span>

            <h1>Controla tus ingresos, gastos y patrimonio.</h1>

            <p>
              Finanzas Web te ayuda a organizar tus movimientos, clasificar tus
              categorías y visualizar tu evolución financiera desde una única
              aplicación.
            </p>

            <div className="home-actions">
              <Link to="/login" className="primary-button">
                Iniciar sesión
              </Link>
            </div>
          </>
        )}
      </div>

      <div className="home-grid">
        <Link to="/dashboard" className="home-card">
          <h2>Dashboard visual</h2>
          <p>
            Consulta ingresos, gastos, balance mensual, distribución por
            categorías y evolución de patrimonio.
          </p>
        </Link>

        <Link to="/categories" className="home-card">
          <h2>Categorías</h2>
          <p>
            Organiza tus movimientos por tipo de ingreso o gasto para entender
            mejor tus hábitos financieros.
          </p>
        </Link>

        <Link to="/transactions" className="home-card">
          <h2>Transacciones</h2>
          <p>
            Registra operaciones reales con fecha, importe, descripción y
            categoría asociada.
          </p>
        </Link>
      </div>
    </section>
  );
}

export default HomePage;