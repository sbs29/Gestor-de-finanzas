const TOKEN_KEY = "token";

export const sessionService = {
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  },

  isAuthenticated(): boolean {
    return Boolean(localStorage.getItem(TOKEN_KEY));
  },

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
  },
};