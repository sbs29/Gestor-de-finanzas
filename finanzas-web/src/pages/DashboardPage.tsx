import { useEffect, useState } from 'react'
import { getTransactions } from '../services/transactionService'
import type { Transaction } from '../types/Transaction'

function DashboardPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadTransactions() {
      try {
        const data = await getTransactions()
        setTransactions(data.content)
      } catch (error) {
        setError('No se pudieron cargar los datos del dashboard')
      } finally {
        setLoading(false)
      }
    }

    loadTransactions()
  }, [])

  const totalIncome = transactions
    .filter(transaction => transaction.categoryType === 'INCOME')
    .reduce((total, transaction) => total + transaction.amount, 0)

  const totalExpense = transactions
    .filter(transaction => transaction.categoryType === 'EXPENSE')
    .reduce((total, transaction) => total + transaction.amount, 0)

  const balance = totalIncome - totalExpense

  if (loading) {
    return <p>Cargando dashboard...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>Resumen general de tu actividad financiera.</p>
        </div>

        <span className="badge">
          {transactions.length} transacciones
        </span>
      </div>

      <section className="dashboard-grid">
        <article className="card">
          <h2>Balance</h2>
          <p className={balance >= 0 ? 'amount income' : 'amount expense'}>
            {balance.toFixed(2)} €
          </p>
        </article>

        <article className="card">
          <h2>Ingresos</h2>
          <p className="amount income">
            +{totalIncome.toFixed(2)} €
          </p>
        </article>

        <article className="card">
          <h2>Gastos</h2>
          <p className="amount expense">
            -{totalExpense.toFixed(2)} €
          </p>
        </article>

        <article className="card">
          <h2>Total transacciones</h2>
          <p className="amount">
            {transactions.length}
          </p>
        </article>
      </section>
    </section>
  )
}

export default DashboardPage