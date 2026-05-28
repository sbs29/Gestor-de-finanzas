import { useEffect, useState, type SubmitEvent } from 'react'
import { createTransaction, getTransactions } from '../services/transactionService'
import { getCategories } from '../services/categoryService'
import type { Transaction } from '../types/Transaction'
import type { Category } from '../types/Category'

function TransactionsPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [categories, setCategories] = useState<Category[]>([])
  const [description, setDescription] = useState('')
  const [amount, setAmount] = useState('')
  const [date, setDate] = useState('')
  const [categoryId, setCategoryId] = useState('')
  const [formMessage, setFormMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function loadTransactions() {
    try {
      const data = await getTransactions()
      setTransactions(data.content)
    } catch (error) {
      setError('No se pudieron cargar las transacciones')
    } finally {
      setLoading(false)
    }
  }

  async function loadCategories() {
    const data = await getCategories()
    setCategories(data)

    if (data.length > 0) {
      setCategoryId(String(data[0].id))
    }
  }

  useEffect(() => {
    loadTransactions()
    loadCategories()
  }, [])

  async function handleCreateTransaction(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!description.trim()) {
      setFormMessage('La descripción es obligatoria')
      return
    }

    if (!amount || Number(amount) <= 0) {
      setFormMessage('La cantidad debe ser mayor que 0')
      return
    }

    if (!date) {
      setFormMessage('La fecha es obligatoria')
      return
    }

    if (!categoryId) {
      setFormMessage('La categoría es obligatoria')
      return
    }

    try {
      setFormMessage('')
      setSubmitting(true)

      await createTransaction({
        description,
        amount: Number(amount),
        date: new Date(date).toISOString(),
        categoryId: Number(categoryId)
      })

      setDescription('')
      setAmount('')
      setDate('')
      setFormMessage('Transacción creada correctamente')

      await loadTransactions()
    } catch (error) {
      setFormMessage('No se pudo crear la transacción')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <p>Cargando transacciones...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Transacciones</h1>
          <p>Gestiona tus ingresos y gastos registrados.</p>
        </div>

        <span className="badge">Total: {transactions.length}</span>
      </div>

      <section className="card">
        <h2>Nueva transacción</h2>

        <form onSubmit={handleCreateTransaction}>
          <div>
            <label htmlFor="description">Descripción</label>
            <input
              id="description"
              type="text"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Ej: Supermercado, gasolina, nómina..."
            />
          </div>

          <div>
            <label htmlFor="amount">Cantidad</label>
            <input
              id="amount"
              type="number"
              step="0.01"
              value={amount}
              onChange={(event) => setAmount(event.target.value)}
              placeholder="Ej: 25.50"
            />
          </div>

          <div>
            <label htmlFor="date">Fecha</label>
            <input
              id="date"
              type="datetime-local"
              value={date}
              onChange={(event) => setDate(event.target.value)}
            />
          </div>

          <div>
            <label htmlFor="category">Categoría</label>
            <select
              id="category"
              value={categoryId}
              onChange={(event) => setCategoryId(event.target.value)}
            >
              {categories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name} - {category.type}
                </option>
              ))}
            </select>
          </div>

          <button type="submit" disabled={submitting}>
            {submitting ? 'Creando...' : 'Crear transacción'}
          </button>
        </form>

        {formMessage && (
          <p
            className={
              formMessage.includes('correctamente')
                ? 'form-message'
                : 'error-message'
            }
          >
            {formMessage}
          </p>
        )}
      </section>

      <section className="card">
        <h2>Historial</h2>

        {transactions.length === 0 ? (
          <p>No hay transacciones registradas.</p>
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
              {transactions.map(transaction => (
                <tr key={transaction.id}>
                  <td>{transaction.description}</td>
                  <td
                    className={
                      transaction.categoryType === 'INCOME'
                        ? 'amount income'
                        : 'amount expense'
                    }>
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

export default TransactionsPage