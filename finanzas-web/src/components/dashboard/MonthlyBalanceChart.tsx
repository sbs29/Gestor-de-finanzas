import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  Bar
} from 'recharts'

import type { MonthlySummary } from '../../types/MonthlySummary'
import { Legend } from 'recharts'

interface MonthlyBalanceChartProps {
  data: MonthlySummary[]
}

const monthNames = [
  'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
  'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'
]

export function MonthlyBalanceChart({ data }: MonthlyBalanceChartProps) {
  const chartData = data.map((item) => ({
    month: monthNames[item.month - 1],
    income: item.income,
    expense: item.expense,
    balance: item.balance
    }))

  return (
    <section className="card">
      <h2>Evolución mensual del balance</h2>

      <div style={{ width: '100%', height: 300 }}>
        <ResponsiveContainer>
          <LineChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip
                formatter={(value, name) => {
                    const labels: Record<string, string> = {
                    income: 'Ingresos',
                    expense: 'Gastos',
                    balance: 'Balance',
                    }

                    const formattedValue =
                    typeof value === 'number'
                        ? `${value.toFixed(2)} €`
                        : value

                    return [formattedValue, labels[String(name)] ?? name]
                }}
            />
            <Bar
                dataKey="income"
                fill="#86efac"
                name="Ingresos"
            />

            <Bar
                dataKey="expense"
                fill="#f87171"
                name="Gastos"
            />

            <Line
                type="monotone"
                dataKey="balance"
                stroke="#3b82f6"
                strokeWidth={3}
                dot={{ r: 4 }}
                name="Balance"
            />
            <Legend />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </section>
  )
}