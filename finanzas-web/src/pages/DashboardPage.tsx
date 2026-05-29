import { useEffect, useState } from 'react'
import { getTransactions } from '../services/transactionService'
import type { Transaction } from '../types/Transaction'
import PageHeader from '../components/PageHeader'
import MetricCard from '../components/MetricCard'

function DashboardPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [period, setPeriod] = useState('MONTH')

  useEffect(() => {
    async function loadTransactions() {
      try {
        const data = await getTransactions(0, 100)
        setTransactions(data.content)
      } catch (error) {
        setError('No se pudieron cargar los datos del dashboard')
      } finally {
        setLoading(false)
      }
    }

    loadTransactions()
  }, [])

  function getPeriodStartDate(period: string): Date {
    const now = new Date()
    const startDate = new Date(now)

    if (period === 'WEEK') {
      startDate.setDate(now.getDate() - 7)
    }

    if (period === 'FORTNIGHT') {
      startDate.setDate(now.getDate() - 15)
    }

    if (period === 'MONTH') {
      startDate.setMonth(now.getMonth() - 1)
    }

    if (period === 'THREE_MONTHS') {
      startDate.setMonth(now.getMonth() - 3)
    }

    if (period === 'SIX_MONTHS') {
      startDate.setMonth(now.getMonth() - 6)
    }

    if (period === 'YEAR') {
      startDate.setFullYear(now.getFullYear() - 1)
    }

    return startDate
  }

  const periodStartDate = getPeriodStartDate(period)

  const periodTransactions = transactions.filter(transaction =>
    new Date(transaction.date) >= periodStartDate
  )

  const totalIncome = periodTransactions
    .filter(transaction => transaction.categoryType === 'INCOME')
    .reduce((total, transaction) => total + transaction.amount, 0)

  const totalExpense = periodTransactions
    .filter(transaction => transaction.categoryType === 'EXPENSE')
    .reduce((total, transaction) => total + transaction.amount, 0)

  const balance = totalIncome - totalExpense

  const latestTransactions = transactions.slice(0, 5)

  const categorySummary = periodTransactions.reduce(
    (accumulator, transaction) => {

      const categoryName = transaction.categoryName

      if (!accumulator[categoryName]) {
        accumulator[categoryName] = {
          total: 0,
          type: transaction.categoryType
        }
      }

      accumulator[categoryName].total += transaction.amount

      return accumulator
    },
    {} as Record<
      string,
      {
        total: number
        type: string
      }
    >
  )

  const categoryTotals = Object.entries(categorySummary)
    .sort((a, b) => b[1].total - a[1].total)

  if (loading) {
    return <p>Cargando dashboard...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <section className="page">
      <PageHeader
        title="Dashboard"
        description="Resumen general de tu actividad financiera."
        badgeText={`${periodTransactions.length} transacciones`}
      />

      <section className="card">
        <h2>Período</h2>

        <div className="filters-form">
          <div>
            <label htmlFor="period-filter">Resumen</label>
            <select
              id="period-filter"
              value={period}
              onChange={(event) => setPeriod(event.target.value)}
            >
              <option value="WEEK">Última semana</option>
              <option value="FORTNIGHT">Última quincena</option>
              <option value="MONTH">Último mes</option>
              <option value="THREE_MONTHS">Últimos 3 meses</option>
              <option value="SIX_MONTHS">Últimos 6 meses</option>
              <option value="YEAR">Último año</option>
            </select>
          </div>
        </div>
      </section>

      <section className="dashboard-grid">

        <MetricCard
          title="Balance"
          value={`${balance.toFixed(2)} €`}
          className={
            balance >= 0
              ? 'amount income'
              : 'amount expense'
          }
        />

        <MetricCard
          title="Ingresos"
          value={`+${totalIncome.toFixed(2)} €`}
          className="amount income"
        />

        <MetricCard
          title="Gastos"
          value={`-${totalExpense.toFixed(2)} €`}
          className="amount expense"
        />

        <MetricCard
          title="Total transacciones"
          value={periodTransactions.length.toString()}
        />
        
      </section>

      <section className="card">
        <h2>Resumen por categorías</h2>

        {categoryTotals.length === 0 ? (
          <div className="empty-state">
            <p>No hay datos para el período seleccionado.</p>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Categoría</th>
                <th>Total</th>
              </tr>
            </thead>

            <tbody>
              {categoryTotals.map(([categoryName, data]) => (
                <tr key={categoryName}>
                  <td>{categoryName}</td>
                  <td
                    className={
                      data.type === 'INCOME'
                        ? 'amount income'
                        : 'amount expense'
                    }
                  >
                    {data.type === 'INCOME' ? '+' : '-'}
                    {data.total.toFixed(2)} €
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section className="card">
        <h2>Últimas transacciones</h2>

        {latestTransactions.length === 0 ? (
          <div className="empty-state">
            <p>No hay transacciones registradas.</p>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Descripción</th>
                <th>Cantidad</th>
                <th>Categoría</th>
                <th>Tipo</th>
                <th>Fecha</th>
              </tr>
            </thead>

            <tbody>
              {latestTransactions.map(transaction => (
                <tr key={transaction.id}>
                  <td>{transaction.description}</td>
                  <td
                    className={
                      transaction.categoryType === 'INCOME'
                        ? 'amount income'
                        : 'amount expense'
                    }
                  >
                    {transaction.categoryType === 'INCOME' ? '+' : '-'}
                    {Math.abs(transaction.amount).toFixed(2)} €
                  </td>
                  <td>{transaction.categoryName}</td>
                  <td>
                    <span
                      className={
                        transaction.categoryType === 'INCOME'
                          ? 'type-badge income-badge'
                          : 'type-badge expense-badge'
                      }
                    >
                      {transaction.categoryType}
                    </span>
                  </td>
                  <td>{new Date(transaction.date).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </section>
  )
}

export default DashboardPage