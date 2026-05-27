import { Link, useNavigate } from "react-router-dom";

function Navbar() {

    const navigate = useNavigate()
    const token = localStorage.getItem('token')

    function handleLogout() {
        localStorage.removeItem('token')
        navigate('/login')
    }

    return (

        <nav>

            <Link to="/">Home</Link>

            {' | '}

            <Link to="/login">Login</Link>

            {' | '}

            <Link to="/dashboard">Dashboard</Link>

            {' | '}

            <Link to="/transactions">Transactions</Link>

            {' | '}

            <Link to="/categories">Categories</Link>

            {token && (
                <>
                    {' | '}
                    <button onClick={handleLogout}>
                        Logout
                    </button>
                </>
            )}

        </nav>

    )

}

export default Navbar