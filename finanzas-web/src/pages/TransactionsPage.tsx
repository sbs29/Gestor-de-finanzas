import { useEffect, useState, type SubmitEvent } from 'react'
import { createTransaction, deleteTransaction, getTransactions, updateTransaction } from '../services/transactionService'
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
  const [editingTransactionId, setEditingTransactionId] = useState<number | null>(null)
  const [filterType, setFilterType] = useState('ALL')
  const filteredTransactions =
    filterType === 'ALL'
      ? transactions
      : transactions.filter(
          transaction => transaction.categoryType === filterType
        )
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  async function loadTransactions() {
    try {
      const data = await getTransactions(page)

      setTransactions(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
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
  }, [page])

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

      if (editingTransactionId) {
        await updateTransaction(editingTransactionId, {
          description,
          amount: Number(amount),
          date: new Date(date).toISOString(),
          categoryId: Number(categoryId)
        })

        setFormMessage('Transacción actualizada correctamente')
      } else {
        await createTransaction({
          description,
          amount: Number(amount),
          date: new Date(date).toISOString(),
          categoryId: Number(categoryId)
        })

        setFormMessage('Transacción creada correctamente')
      }
      setDescription('')
      setAmount('')
      setDate('')
      setEditingTransactionId(null)

      await loadTransactions()
    } catch (error) {
      setFormMessage('No se pudo crear la transacción')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDeleteTransaction(id:number) {
    const confirmed = window.confirm(
      '¿Seguro que deseas eliminar esta transacción?'
    )

    if (!confirmed) {
      return
    }

    try {
      await deleteTransaction(id)

      setFormMessage('Transacción eliminada correctamente')

      await loadTransactions()
    } catch (error) {
      setFormMessage('No se pudo eliminar la transacción')
    }
  }

  function handleStartEditTransaction(transaction: Transaction) {
    setEditingTransactionId(transaction.id)

    setDescription(transaction.description)

    setAmount(String(transaction.amount))

    setDate(transaction.date.slice(0,16))

    setCategoryId(String(transaction.categoryId))

    setFormMessage('')

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

        <span className="badge">Total: {totalElements}</span>
      </div>

      <section className="card">
        <h2>
          {
            editingTransactionId
            ? 'Editar transacción'
            : 'Nueva transacción'
          }
        </h2>

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

          <button type="submit" className='primary-button' disabled={submitting}>
            {submitting
              ? 'Guardando...'
              : editingTransactionId
                ? 'Actualizar transacción'
                : 'Crear transacción'}
          </button>
          {editingTransactionId && (
            <button
              type="button"
              onClick={() => {
                setEditingTransactionId(null)
                setDescription('')
                setAmount('')
                setDate('')
                setFormMessage('')
              }}
            >
              Cancelar edición
            </button>
          )}
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
        <h2>Filtros</h2>

        <select
          value={filterType}
          onChange={(event) => setFilterType(event.target.value)}
        >
          <option value="ALL">Todas</option>
          <option value="INCOME">Ingresos</option>
          <option value="EXPENSE">Gastos</option>
        </select>
      </section>

      <section className="card">
        <h2>Historial</h2>

        {filteredTransactions.length === 0 ? (
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
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {filteredTransactions.map(transaction => (
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
                  <td>
                    <div className='table-actions'>
                      <button
                        type="button"
                        className='edit-button'
                        onClick={() => handleStartEditTransaction(transaction)}
                      >
                        Editar
                      </button>
                      <button
                        type='button'
                        className='danger-button'
                        onClick={() => handleDeleteTransaction(transaction.id)}
                      >
                        Eliminar
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <div className="pagination">
            <button
              type="button"
              className="primary-button"
              disabled={page === 0}
              onClick={() => setPage(page - 1)}
            >
              Anterior
            </button>

            <span>
              Página {page + 1} de {totalPages}
            </span>

            <button
              type="button"
              className="primary-button"
              disabled={page + 1 >= totalPages}
              onClick={() => setPage(page + 1)}
            >
              Siguiente
            </button>
          </div>
      </section>
    </section>
  )
}

export default TransactionsPage