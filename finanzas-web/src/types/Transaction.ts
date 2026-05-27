export type CategoryType = 'INCOME' | 'EXPENSE'

export interface Transaction {
  id: number
  amount: number
  description: string
  date: string
  categoryId: number
  categoryName: string
  categoryType: CategoryType
}

export interface CreateTransactionRequest {
  amount: number
  description: string
  date: string
  categoryId: number
}