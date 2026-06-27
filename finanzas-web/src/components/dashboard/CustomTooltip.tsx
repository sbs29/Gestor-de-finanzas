interface CustomTooltipProps {
  active?: boolean
  payload?: any[]
  label?: string
}

export function CustomTooltip({
  active,
  payload,
  label,
}: CustomTooltipProps) {

  if (!active || !payload?.length) {
    return null
  }

  return (
    <div className="custom-tooltip">

      <p className="custom-tooltip-title">
        {label}
      </p>

      <div className="custom-tooltip-row income">
        <span>Ingresos</span>
        <strong>
          +{Number(payload[0].value).toFixed(2)} €
        </strong>
      </div>

      <div className="custom-tooltip-row expense">
        <span>Gastos</span>
        <strong>
          -{Number(payload[1].value).toFixed(2)} €
        </strong>
      </div>  

    </div>
  )
}