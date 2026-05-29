import { apiClient } from '../api/apiClient'
import type { CreateTransactionRequest, Transaction } from '../types/Transaction'
import type { PagedResponse } from '../types/PagedResponse'

export async function getTransactions(
  page = 0,
  size = 10,
  type = 'ALL',
  categoryId = 'ALL',
  start = '',
  end = ''
): Promise<PagedResponse<Transaction>> {
  const response = await apiClient.get<PagedResponse<Transaction>>(
    '/transactions',
    {
      params: {
        page,
        size,
        ...(type !== 'ALL' && { type }),
        ...(categoryId !== 'ALL' && { categoryId }),
        ...(start && { start }),
        ...(end && { end })
      }
    }
  )

  return response.data
}

export async function createTransaction(request: CreateTransactionRequest): Promise<Transaction> {
  const response = await apiClient.post<Transaction>('/transactions', request)

  return response.data
}

export async function deleteTransaction(id:number): Promise<void> {
  await apiClient.delete(`/transactions/${id}`)
}

export async function updateTransaction(
  id:number,
  request: CreateTransactionRequest
): Promise<Transaction> {
  const response = await apiClient.put<Transaction>(
    `/transactions/${id}`,
    request
  )

  return response.data
}