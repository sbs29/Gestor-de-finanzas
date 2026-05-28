import { useEffect, useState, type SubmitEvent } from 'react'
import { createCategory, deleteCategory, getCategories, updateCategory } from '../services/categoryService'
import type { Category, CategoryType } from '../types/Category'

function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([])
  const [name, setName] = useState('')
  const [type, setType] = useState<CategoryType>('EXPENSE')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formMessage, setFormMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [editingCategoryId, setEditingCategoryId] = useState<number | null>(null)

  async function loadCategories() {
    try {
      const data = await getCategories()
      setCategories(data)
    } catch (error) {
      setError('No se pudieron cargar las categorías')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCategories()
  }, [])

  async function handleCreateCategory(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!name.trim()) {
      setFormMessage('El nombre es obligatorio')
      return
    }

    try {
      setSubmitting(true)
      setFormMessage('')

      if (editingCategoryId) {
        await updateCategory(editingCategoryId, {
          name,
          type
        })

        setFormMessage('Categoría actualizada correctamente')
      } else {
        await createCategory({
          name,
          type
        })

        setFormMessage('Categoría creada correctamente')
      }

      setName('')
      setType('EXPENSE')
      setEditingCategoryId(null)

      await loadCategories()
    } catch (error) {
      setFormMessage('No se pudo crear la categoría')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDeleteCategory(id: number) {
    const confirmed = window.confirm(
      '¿Seguro que deseas eliminar esta categoría?'
    )

    if (!confirmed) {
      return
    }

    try {
      await deleteCategory(id)

      await loadCategories()
    } catch (error) {
      setFormMessage('No se puede eliminar una categoría con transacciones asociadas')
    }
  }

  function handleStartEditCategory(category: Category) {
    setEditingCategoryId(category.id)
    setName(category.name)
    setType(category.type)
    setFormMessage('')
  }

  if (loading) {
    return <p>Cargando categorías...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Categorías</h1>
          <p>Organiza tus ingresos y gastos por tipo.</p>
        </div>

        <span className="badge">Total: {categories.length}</span>
      </div>

      <section className="card">
        <h2>{editingCategoryId ? 'Editar categoría' : 'Nueva categoría'}</h2>

        <form onSubmit={handleCreateCategory}>
          <div>
            <label htmlFor="category-name">Nombre</label>
            <input
              id="category-name"
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Ej: Comida, transporte, salario..."
            />
          </div>

          <div>
            <label htmlFor="category-type">Tipo</label>
            <select
              id="category-type"
              value={type}
              onChange={(event) => setType(event.target.value as CategoryType)}
            >
              <option value="EXPENSE">Gasto</option>
              <option value="INCOME">Ingreso</option>
            </select>
          </div>

          <button type="submit" disabled={submitting}>
            {submitting
              ? 'Guardando...'
              : editingCategoryId
                ? 'Actualizar categoría'
                : 'Crear categoría'}
          </button>
          {editingCategoryId && (
            <button
              type="button"
              onClick={() => {
                setEditingCategoryId(null)
                setName('')
                setType('EXPENSE')
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
        <h2>Listado de categorías</h2>

        {categories.length === 0 ? (
          <p>No hay categorías registradas.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {categories.map(category => (
                <tr key={category.id}>
                  <td>{category.name}</td>
                  <td>
                    <span
                      className={
                        category.type === 'INCOME'
                          ? 'type-badge income-badge'
                          : 'type-badge expense-badge'
                      }
                    >
                      {category.type}
                    </span>
                  </td>
                  <td>
                    <button
                      type="button"
                      className="danger-button"
                      onClick={() => handleDeleteCategory(category.id)}
                    >
                      Eliminar
                    </button>
                    <button
                      type='button'
                      onClick={() => handleStartEditCategory(category)}
                    >
                      Editar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </section>
  )
}

export default CategoriesPage