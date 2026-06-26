import { apiClient } from "../api/apiClient";
import type { MonthlySummary } from "../types/MonthlySummary";
import type { ExpenseByCategory } from "../types/ExpenseByCategory";

export const getMonthlySummary = async (
    year: number
): Promise<MonthlySummary[]> => {

    const response = await apiClient.get<MonthlySummary[]>(
        `/dashboard/monthly-summary`,
        {
            params: {
                year
            }
        }
    );

    return response.data;
};

export const getExpensesByCategory = async (
    year: number
): Promise<ExpenseByCategory[]> => {

    const response = await apiClient.get<ExpenseByCategory[]>(
        "/dashboard/expenses-by-category",
        {
            params: {
                year
            }
        }
    );

    return response.data;
};