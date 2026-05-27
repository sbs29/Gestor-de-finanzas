import { apiClient } from '../api/apiClient'
import type { CreateTransactionRequest, Transaction } from '../types/Transaction'
import type { PagedResponse } from '../types/PagedResponse'

export async function getTransactions(): Promise<PagedResponse<Transaction>> {
  const response = await apiClient.get<PagedResponse<Transaction>>(
    '/transactions'
  )

  return response.data
}

export async function createTransaction(request: CreateTransactionRequest): Promise<Transaction> {
  const response = await apiClient.post<Transaction>('/transactions', request)

  return response.data
}