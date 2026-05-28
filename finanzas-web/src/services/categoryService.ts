import { apiClient } from '../api/apiClient'
import type { Category, CreateCategoryRequest } from '../types/Category'

export async function getCategories(): Promise<Category[]> {
  const response = await apiClient.get<Category[]>('/categories')

  return response.data
}

export async function createCategory(
  request: CreateCategoryRequest
): Promise<Category> {
  const response = await apiClient.post<Category>(
    '/categories',
    request
  )

  return response.data
}

export async function deleteCategory(id:number): Promise<void> {
  await apiClient.delete(`/categories/${id}`)
}

export async function updateCategory(
  id: number,
  request: CreateCategoryRequest
): Promise<Category> {
  const response = await apiClient.put<Category>(
    `/categories/${id}`,
    request
  )

  return response.data
}