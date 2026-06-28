import { apiClient } from '../api/apiClient'
import type { UserProfile } from '../types/UserProfile'

export async function getCurrentUserProfile(): Promise<UserProfile> {
  const response = await apiClient.get<UserProfile>('/users/me')
  return response.data
}