interface PageHeaderProps {
  title: string
  description: string
  badgeText?: string
}

function PageHeader({ title, description, badgeText }: PageHeaderProps) {
  return (
    <div className="page-header">
      <div>
        <h1>{title}</h1>
        <p>{description}</p>
      </div>

      {badgeText && (
        <span className="badge">
          {badgeText}
        </span>
      )}
    </div>
  )
}

export default PageHeader