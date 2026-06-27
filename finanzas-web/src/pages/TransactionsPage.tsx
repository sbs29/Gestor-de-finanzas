import { useEffect, useState, type SubmitEvent } from 'react'
import { createTransaction, deleteTransaction, getTransactions, updateTransaction } from '../services/transactionService'
import { getCategories } from '../services/categoryService'
import type { Transaction } from '../types/Transaction'
import type { Category } from '../types/Category'
import PageHeader from '../components/PageHeader'

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
  const [filterCategoryId, setFilterCategoryId] = useState('ALL')
  const [filterStartDate, setFilterStartDate] = useState('')
  const [filterEndDate, setFilterEndDate] = useState('')

  async function loadTransactions() {
    setLoading(true)
    try {
      const data = await getTransactions(
        page,
        10,
        filterType,
        filterCategoryId,
        filterStartDate ? new Date(filterStartDate).toISOString() : '',
        filterEndDate ? new Date(filterEndDate).toISOString() : ''
      )

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
  }, [page, filterType, filterCategoryId, filterStartDate, filterEndDate])

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
      resetTransactionForm()

      await loadTransactions()
    } catch (error) {
        setFormMessage(
          editingTransactionId
            ? 'No se pudo actualizar la transacción'
            : 'No se pudo crear la transacción'
        )
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
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  function resetTransactionForm() {
    setDescription('')
    setAmount('')
    setDate('')
    setCategoryId(
      categories.length > 0
        ? String(categories[0].id)
        : ''
    )
    setEditingTransactionId(null)
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
      <PageHeader
        title="Transacciones"
        description="Gestiona tus ingresos y gastos registrados."
        badgeText={`Total: ${totalElements}`}
      />

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
              ? editingTransactionId
                  ? 'Actualizando...'
                  : 'Guardando...'
              : editingTransactionId
                  ? 'Actualizar transacción'
                  : 'Crear transacción'}
          </button>
          {editingTransactionId && (
            <button
              type="button"
              className="secondary-button"
              onClick={resetTransactionForm}
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

        <div className="filters-form">
          <div>
            <label htmlFor="type-filter">Tipo</label>
            <select
              id="type-filter"
              value={filterType}
              onChange={(event) => {
                setFilterType(event.target.value)
                setPage(0)
              }}
            >
              <option value="ALL">Todas</option>
              <option value="INCOME">Ingresos</option>
              <option value="EXPENSE">Gastos</option>
            </select>
          </div>

          <div>
            <label htmlFor="category-filter">Categoría</label>
            <select
              id="category-filter"
              value={filterCategoryId}
              onChange={(event) => {
                setFilterCategoryId(event.target.value)
                setPage(0)
              }}
            >
              <option value="ALL">Todas</option>

              {categories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="start-date-filter">Desde</label>
            <input
              id="start-date-filter"
              type="datetime-local"
              value={filterStartDate}
              onChange={(event) => {
                setFilterStartDate(event.target.value)
                setPage(0)
              }}
            />
          </div>

          <div>
            <label htmlFor="end-date-filter">Hasta</label>
            <input
              id="end-date-filter"
              type="datetime-local"
              value={filterEndDate}
              onChange={(event) => {
                setFilterEndDate(event.target.value)
                setPage(0)
              }}
            />
          </div>
          <div className="filters-actions">
            <button
              type="button"
              className="primary-button"
              onClick={() => {
                setFilterType('ALL')
                setFilterCategoryId('ALL')
                setFilterStartDate('')
                setFilterEndDate('')
                setPage(0)
              }}
            >
              Limpiar filtros
            </button>
          </div>
        </div>
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