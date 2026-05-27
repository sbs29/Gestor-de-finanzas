import { Outlet } from 'react-router-dom'
import Navbar from '../components/Navbar'
import Header from '../components/Header'

function MainLayout() {
  return (
    <>
      <Header />
      <Navbar />

      <main>
        <Outlet />
      </main>
    </>
  )
}

export default MainLayout