import { useEffect, useState } from 'react'
import { getTransactions } from '../services/transactionService'
import type { Transaction } from '../types/Transaction'
import PageHeader from '../components/PageHeader'
import { getMonthlySummary } from '../services/dashboardService'
import type { MonthlySummary } from '../types/MonthlySummary'
import { MonthlyBalanceChart } from '../components/dashboard/MonthlyBalanceChart'

function DashboardPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [period, setPeriod] = useState('MONTH')
  const [latestLimit, setLatestLimit] = useState(5)
  const [analysisMode, setAnalysisMode] = useState('RELATIVE')
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth())
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
  const [monthlySummary, setMonthlySummary] =useState<MonthlySummary[]>([])

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

  useEffect(() => {
    async function loadMonthlySummary() {
      try {
        const data = await getMonthlySummary(selectedYear)
        setMonthlySummary(data)
      } catch (error) {
        setError('No se pudo cargar el resumen mensual')
      }
    }

    loadMonthlySummary()
  }, [selectedYear])

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

  function getAnalysisLabel(): string {
  if (analysisMode === 'MONTH') {
    return `Resumen de ${months[selectedMonth]} ${selectedYear}`
  }

  if (analysisMode === 'YEAR') {
    return `Resumen del año ${selectedYear}`
  }

  const labels: Record<string, string> = {
    WEEK: 'Resumen de la última semana',
    FORTNIGHT: 'Resumen de la última quincena',
    MONTH: 'Resumen del último mes',
    THREE_MONTHS: 'Resumen de los últimos 3 meses',
    SIX_MONTHS: 'Resumen de los últimos 6 meses',
    YEAR: 'Resumen del último año'
  }

  return labels[period]
}

  const periodTransactions = transactions.filter(transaction => {
    const transactionDate = new Date(transaction.date)

    if (analysisMode === 'RELATIVE') {
      return transactionDate >= getPeriodStartDate(period)
    }

    if (analysisMode === 'MONTH') {
      return (
        transactionDate.getMonth() === selectedMonth &&
        transactionDate.getFullYear() === selectedYear
      )
    }

    if (analysisMode === 'YEAR') {
      return transactionDate.getFullYear() === selectedYear
    }

    return true
  })

  const totalIncome = periodTransactions
    .filter(transaction => transaction.categoryType === 'INCOME')
    .reduce((total, transaction) => total + transaction.amount, 0)

  const totalExpense = periodTransactions
    .filter(transaction => transaction.categoryType === 'EXPENSE')
    .reduce((total, transaction) => total + transaction.amount, 0)

  const balance = totalIncome - totalExpense

  const latestTransactions = transactions.slice(0, latestLimit)

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

  const months = [
    'Enero',
    'Febrero',
    'Marzo',
    'Abril',
    'Mayo',
    'Junio',
    'Julio',
    'Agosto',
    'Septiembre',
    'Octubre',
    'Noviembre',
    'Diciembre'
  ]

  const currentYear = new Date().getFullYear()
  const years = Array.from(
    { length: 5 },
    (_, index) => currentYear - index
  )
  const analysisLabel = getAnalysisLabel()

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

      <section className="dashboard-hero">
        <div>
          <span className="dashboard-eyebrow">{analysisLabel}</span>

          <h2>Balance financiero</h2>

          <p className={balance >= 0 ? 'hero-amount income' : 'hero-amount expense'}>
            {balance.toFixed(2)} €
          </p>

          <p className="dashboard-hero__description">
            Resultado calculado a partir de tus ingresos y gastos registrados.
          </p>

          <div className="dashboard-period-inline">
            <div>
              <label htmlFor="analysis-mode">Tipo de análisis</label>

              <select
                id="analysis-mode"
                value={analysisMode}
                onChange={(event) => setAnalysisMode(event.target.value)}
              >
                <option value="RELATIVE">Período rápido</option>
                <option value="MONTH">Mes específico</option>
                <option value="YEAR">Año completo</option>
              </select>
            </div>

            {analysisMode === 'RELATIVE' && (
              <div>
                <label htmlFor="period-filter">Período</label>

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
            )}

            {analysisMode === 'MONTH' && (
              <>
                <div>
                  <label htmlFor="month-filter">Mes</label>

                  <select
                    id="month-filter"
                    value={selectedMonth}
                    onChange={(event) => setSelectedMonth(Number(event.target.value))}
                  >
                    {months.map((month, index) => (
                      <option key={month} value={index}>
                        {month}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label htmlFor="year-filter-month">Año</label>

                  <select
                    id="year-filter-month"
                    value={selectedYear}
                    onChange={(event) => setSelectedYear(Number(event.target.value))}
                  >
                    {years.map(year => (
                      <option key={year} value={year}>
                        {year}
                      </option>
                    ))}
                  </select>
                </div>
              </>
            )}

            {analysisMode === 'YEAR' && (
              <div>
                <label htmlFor="year-filter">Año</label>

                <select
                  id="year-filter"
                  value={selectedYear}
                  onChange={(event) => setSelectedYear(Number(event.target.value))}
                >
                  {years.map(year => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <span>{periodTransactions.length} movimientos analizados</span>
          </div>

          <div className="dashboard-hero-metrics">
            <article>
              <span>Ingresos</span>
              <strong className="income">+{totalIncome.toFixed(2)} €</strong>
            </article>

            <article>
              <span>Gastos</span>
              <strong className="expense">-{totalExpense.toFixed(2)} €</strong>
            </article>

            <article>
              <span>Transacciones</span>
              <strong>{periodTransactions.length}</strong>
            </article>
          </div>
        </div>
      </section>

      <section className="card">
        <h2>Resumen mensual</h2>

        <MonthlyBalanceChart data={monthlySummary} />
      </section>

      <section className="card">
        <h2>Resumen por categorías</h2>

        {categoryTotals.length === 0 ? (
          <div className="empty-state">
            <p>No hay datos para el período seleccionado.</p>
          </div>
        ) : (
          <div className="category-summary-list">
            {categoryTotals.map(([categoryName, data]) => {
              const maxTotal = Math.max(
                ...categoryTotals.map(([, item]) => item.total)
              )

              const percentage = maxTotal > 0
                ? (data.total / maxTotal) * 100
                : 0

              return (
                <article className="category-summary-item" key={categoryName}>
                  <div className="category-summary-item__header">
                    <div>
                      <strong>{categoryName}</strong>
                      <span>{data.type === 'INCOME' ? 'Ingreso' : 'Gasto'}</span>
                    </div>

                    <p className={data.type === 'INCOME' ? 'amount income' : 'amount expense'}>
                      {data.type === 'INCOME' ? '+' : '-'}
                      {data.total.toFixed(2)} €
                    </p>
                  </div>

                  <div className="category-summary-bar">
                    <div
                      className={
                        data.type === 'INCOME'
                          ? 'category-summary-bar__fill income-fill'
                          : 'category-summary-bar__fill expense-fill'
                      }
                      style={{ width: `${percentage}%` }}
                    />
                  </div>
                </article>
              )
            })}
          </div>
        )}
      </section>

      <section className="card">
        <div className="section-header">
          <div>
            <h2>Últimas transacciones</h2>
            <p>Consulta tus movimientos más recientes.</p>
          </div>

          <div className="section-header__control">
            <label htmlFor="latest-limit">Mostrar</label>

            <select
              id="latest-limit"
              value={latestLimit}
              onChange={(event) => setLatestLimit(Number(event.target.value))}
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
          </div>
        </div>

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