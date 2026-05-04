export default function ExpenseTable({ expenses, onDelete }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <h3>Recent expenses</h3>
      </div>
      {expenses.length === 0 ? (
        <p className="empty">Add the first expense to populate the dashboard.</p>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Title</th>
                <th>Category</th>
                <th>Amount</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {expenses.map((expense) => (
                <tr key={expense.id}>
                  <td>{expense.expenseDate}</td>
                  <td>{expense.title}</td>
                  <td>
                    <span className="category-pill" style={{ backgroundColor: expense.category.color }}>
                      {expense.category.name}
                    </span>
                  </td>
                  <td>${Number(expense.amount).toFixed(2)}</td>
                  <td>
                    <button className="ghost-button" onClick={() => onDelete(expense.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
