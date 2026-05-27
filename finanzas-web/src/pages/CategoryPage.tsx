import { useEffect, useState, type SubmitEvent } from 'react'
import { createCategory, getCategories } from '../services/categoryService'
import type { Category, CategoryType } from '../types/Category'

function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([])
  const [name, setName] = useState('')
  const [type, setType] = useState<CategoryType>('EXPENSE')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formMessage, setFormMessage] = useState('')

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

    try {
      setFormMessage('')

      await createCategory({
        name,
        type
      })

      setName('')
      setType('EXPENSE')
      setFormMessage('Categoría creada correctamente')

      await loadCategories()
    } catch (error) {
      setFormMessage('No se pudo crear la categoría')
    }
  }

  if (loading) {
    return <p>Cargando categorías...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <>
      <h1>Categorías</h1>

      <form onSubmit={handleCreateCategory}>
        <div>
          <label htmlFor="category-name">Nombre</label>
          <input
            id="category-name"
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
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

        <button type="submit">
          Crear categoría
        </button>
      </form>

      {formMessage && <p>{formMessage}</p>}

      <p>Total: {categories.length}</p>

      {categories.length === 0 ? (
        <p>No hay categorías registradas.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Tipo</th>
            </tr>
          </thead>

          <tbody>
            {categories.map(category => (
              <tr key={category.id}>
                <td>{category.name}</td>
                <td>{category.type}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}

export default CategoriesPage