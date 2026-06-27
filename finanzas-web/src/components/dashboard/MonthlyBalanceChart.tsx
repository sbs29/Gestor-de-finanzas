import {
  ComposedChart,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  Bar,
} from 'recharts'

import type { MonthlySummary } from '../../types/MonthlySummary'
import { CustomTooltip } from './CustomTooltip'

interface MonthlyBalanceChartProps {
  data: MonthlySummary[]
  year: number
}

const monthNames = [
  'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
  'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic',
]

export function MonthlyBalanceChart({ data , year, }: MonthlyBalanceChartProps) {
  const chartData = data.map((item) => ({
    month: monthNames[item.month - 1],
    income: item.income,
    expense: item.expense,
    balance: item.balance,
  }))

  return (
    <section className="card">
      <h2>Ingresos, gastos y balance · {year}</h2>

      <div style={{ width: '100%', height: 300 }}>
        <ResponsiveContainer>
          <ComposedChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="month"
              tickLine={false}
              axisLine={false}
            />
            <YAxis
              tickLine={false}
              axisLine={false}
              tickFormatter={(value) => `${value} €`}
            />
            <Tooltip
              content={<CustomTooltip />}
              itemSorter={(item) => {
                const order: Record<string, number> = {
                  income: 0,
                  expense: 1,
                  balance: 2,
                }

                return order[String(item.dataKey)] ?? 99
              }}

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

            <Bar dataKey="income" fill="#86efac" name="Ingresos" />
            <Bar dataKey="expense" fill="#f87171" name="Gastos" />
            <Bar dataKey="balance" fill="#3b82f6" name="Balance" />

          </ComposedChart>
        </ResponsiveContainer>
      </div>
      <div className="chart-custom-legend">
        <div className="chart-custom-legend-item">
          <span className="legend-dot legend-income" />
          <span>Ingresos</span>
        </div>

        <div className="chart-custom-legend-item">
          <span className="legend-dot legend-expense" />
          <span>Gastos</span>
        </div>

        <div className="chart-custom-legend-item">
          <span className="legend-line legend-balance" />
          <span>Balance</span>
        </div>
      </div>
    </section>
  )
}