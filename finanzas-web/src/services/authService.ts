import { apiClient } from '../api/apiClient'
import type { AuthResponse, LoginRequest } from '../types/Auth'

export async function login(request: LoginRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>(
    '/auth/login',
    request
  )

  return response.data
}