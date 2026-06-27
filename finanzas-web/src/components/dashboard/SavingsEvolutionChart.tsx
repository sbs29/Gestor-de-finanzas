import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'

import type { MonthlySummary } from '../../types/MonthlySummary'

interface SavingsEvolutionChartProps {
  data: MonthlySummary[]
  year: number
}

const monthNames = [
  'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
  'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic',
]

export function SavingsEvolutionChart({
  data,
  year,
}: SavingsEvolutionChartProps) {
  let accumulatedSavings = 0

  const chartData = data.map((item) => {
    accumulatedSavings += item.balance

    return {
      month: monthNames[item.month - 1],
      monthlyBalance: item.balance,
      accumulatedSavings,
    }
  })

  return (
    <section className="card">
        <h2>Patrimonio acumulado · {year}</h2>

        <div style={{ width: '100%', height: 320 }}>
            <ResponsiveContainer>
                <AreaChart data={chartData}>
                <defs>
                    <linearGradient id="savingsGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#22c55e" stopOpacity={0.35} />
                    <stop offset="95%" stopColor="#22c55e" stopOpacity={0.03} />
                    </linearGradient>
                </defs>

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
                    formatter={(value) =>
                    typeof value === 'number'
                        ? `${value.toFixed(2)} €`
                        : value
                    }
                />

                <Area
                    type="monotone"
                    dataKey="accumulatedSavings"
                    stroke="#16a34a"
                    strokeWidth={3}
                    fill="url(#savingsGradient)"
                    name="Ahorro acumulado"
                    dot={{ r: 4 }}
                    activeDot={{ r: 6 }}
                />
                </AreaChart>
            </ResponsiveContainer>
        </div>
    </section>
  )
}