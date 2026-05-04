function maxValue(items) {
  const values = items.map((item) => Number(item.total));
  return values.length ? Math.max(...values, 1) : 1;
}

export default function ChartCard({ title, items, mode = "bar" }) {
  const max = maxValue(items);

  return (
    <section className="panel">
      <div className="panel-header">
        <h3>{title}</h3>
      </div>
      {items.length === 0 ? (
        <p className="empty">No data yet for this period.</p>
      ) : mode === "bar" ? (
        <div className="bars">
          {items.map((item) => (
            <div className="bar-row" key={item.category}>
              <span>{item.category}</span>
              <div className="bar-track">
                <div
                  className="bar-fill"
                  style={{
                    width: `${(Number(item.total) / max) * 100}%`,
                    background: item.color
                  }}
                />
              </div>
              <strong>${Number(item.total).toFixed(2)}</strong>
            </div>
          ))}
        </div>
      ) : (
        <svg className="trend-chart" viewBox="0 0 600 220" preserveAspectRatio="none">
          <polyline
            fill="none"
            stroke="#073b4c"
            strokeWidth="4"
            points={items
              .map((item, index) => {
                const x = items.length === 1 ? 300 : (index / (items.length - 1)) * 560 + 20;
                const y = 190 - (Number(item.total) / max) * 150;
                return `${x},${y}`;
              })
              .join(" ")}
          />
          {items.map((item, index) => {
            const x = items.length === 1 ? 300 : (index / (items.length - 1)) * 560 + 20;
            const y = 190 - (Number(item.total) / max) * 150;
            return (
              <g key={item.date}>
                <circle cx={x} cy={y} r="5" fill="#ef476f" />
                <text x={x} y="210" textAnchor="middle" className="chart-label">
                  {item.date.slice(5)}
                </text>
              </g>
            );
          })}
        </svg>
      )}
    </section>
  );
}
