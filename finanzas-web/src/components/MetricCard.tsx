interface MetricCardProps {
  title: string
  value: string
  className?: string
}

function MetricCard({
  title,
  value,
  className = 'amount'
}: MetricCardProps) {
  return (
    <article className="card">
      <h2>{title}</h2>

      <p className={className}>
        {value}
      </p>
    </article>
  )
}

export default MetricCard