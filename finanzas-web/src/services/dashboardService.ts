import { apiClient } from "../api/apiClient";
import type { MonthlySummary } from "../types/MonthlySummary";

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