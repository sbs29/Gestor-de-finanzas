import {
  PieChart,
  Pie,
  Tooltip,
  ResponsiveContainer,
  Cell,
} from 'recharts'

import type { ExpenseByCategory } from '../../types/ExpenseByCategory'

interface ExpensesByCategoryChartProps {
  data: ExpenseByCategory[]
  year: number
}

const COLORS = ['#60a5fa', '#f87171', '#fbbf24', '#34d399']

export function ExpensesByCategoryChart({
  data,
  year,
}: ExpensesByCategoryChartProps) {
    const total = data.reduce(
    (sum, item) => sum + item.amount,
    0
);
    return (
        <section className="card">
            <h2>Gastos por categoría · {year}</h2>

            <div className="expenses-content">
                <div className="expenses-chart">

                    <ResponsiveContainer width="100%" height="100%">
                        <PieChart>
                            <Pie
                            data={data}
                            dataKey="amount"
                            nameKey="category"
                            innerRadius={75}
                            outerRadius={110}
                            paddingAngle={4}
                            >
                            {data.map((entry, index) => (
                                <Cell
                                key={entry.category}
                                fill={COLORS[index % COLORS.length]}
                                />
                            ))}
                            </Pie>

                            <text
                                x="50%"
                                y="46%"
                                textAnchor="middle"
                                dominantBaseline="middle"
                                fontSize={20}
                                fontWeight="bold"
                                fill="#ef4444"
                            >
                                {total.toFixed(2)} €
                            </text>

                            <text
                                x="50%"
                                y="58%"
                                textAnchor="middle"
                                dominantBaseline="middle"
                                fontSize={13}
                                fontWeight="500"
                                fill="#64748b"
                            >
                                Total
                            </text>

                            <Tooltip
                            formatter={(value) =>
                                typeof value === 'number'
                                ? `${value.toFixed(2)} €`
                                : value
                            }
                            />
                        </PieChart>
                    </ResponsiveContainer>
                </div>
                <ul className="chart-legend-list">
                    {data.map((item, index) => (
                        <li
                            key={item.category}
                            className="chart-legend-item"
                        >
                            <div className="chart-legend-label">
                                <span
                                    className="chart-legend-color"
                                    style={{
                                        backgroundColor:
                                            COLORS[index % COLORS.length]
                                    }}
                                />
                                <span>{item.category}</span>
                            </div>
                            <strong>{item.amount.toFixed(2)} €</strong>
                        </li>
                    ))}
                </ul>
            </div>
        </section>
    )
}