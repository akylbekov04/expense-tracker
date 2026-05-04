import { useEffect, useState } from "react";
import ChartCard from "./components/ChartCard";
import ExpenseTable from "./components/ExpenseTable";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const today = new Date().toISOString().slice(0, 10);
const firstDayOfMonth = `${today.slice(0, 8)}01`;

function parseJwt(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

function App() {
  const [authMode, setAuthMode] = useState("login");
  const [authForm, setAuthForm] = useState({ name: "", email: "", password: "" });
  const [categoryForm, setCategoryForm] = useState({ name: "", color: "#ef476f" });
  const [expenseForm, setExpenseForm] = useState({
    title: "",
    amount: "",
    expenseDate: today,
    note: "",
    categoryId: ""
  });
  const [tokens, setTokens] = useState(() => ({
    accessToken: localStorage.getItem("accessToken") || "",
    refreshToken: localStorage.getItem("refreshToken") || ""
  }));
  const [user, setUser] = useState(() => {
    const accessToken = localStorage.getItem("accessToken");
    return accessToken ? parseJwt(accessToken) : null;
  });
  const [categories, setCategories] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [monthlyReport, setMonthlyReport] = useState(null);
  const [weeklyReport, setWeeklyReport] = useState(null);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!tokens.accessToken) {
      return;
    }
    loadDashboard();
  }, [tokens.accessToken]);

  async function api(path, options = {}, retry = true) {
    const headers = {
      "Content-Type": "application/json",
      ...(options.headers || {})
    };
    if (tokens.accessToken) {
      headers.Authorization = `Bearer ${tokens.accessToken}`;
    }

    const response = await fetch(`${API_URL}${path}`, {
      ...options,
      headers
    });

    if (response.status === 401 && tokens.refreshToken && retry) {
      const refreshed = await refreshSession();
      if (refreshed) {
        return api(path, options, false);
      }
    }

    if (!response.ok) {
      let errorMessage = "Request failed";
      try {
        const payload = await response.json();
        errorMessage = payload.message || errorMessage;
      } catch {
        errorMessage = response.statusText;
      }
      throw new Error(errorMessage);
    }

    if (response.headers.get("content-type")?.includes("application/json")) {
      return response.json();
    }

    return response.blob();
  }

  async function refreshSession() {
    try {
      const response = await fetch(`${API_URL}/api/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken: tokens.refreshToken })
      });
      if (!response.ok) {
        handleLogout();
        return false;
      }
      const data = await response.json();
      applyAuth(data);
      return true;
    } catch {
      handleLogout();
      return false;
    }
  }

  function applyAuth(data) {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    setTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken });
    setUser(data.user || parseJwt(data.accessToken));
  }

  function handleLogout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setTokens({ accessToken: "", refreshToken: "" });
    setUser(null);
    setCategories([]);
    setExpenses([]);
    setMonthlyReport(null);
    setWeeklyReport(null);
  }

  async function loadDashboard() {
    try {
      const month = Number(today.slice(5, 7));
      const year = Number(today.slice(0, 4));
      const [categoryData, expenseData, monthlyData, weeklyData] = await Promise.all([
        api("/api/categories"),
        api(`/api/expenses?startDate=${firstDayOfMonth}&endDate=${today}`),
        api(`/api/reports/monthly?year=${year}&month=${month}`),
        api(`/api/reports/weekly?date=${today}`)
      ]);
      setCategories(categoryData);
      setExpenses(expenseData.reverse());
      setMonthlyReport(monthlyData);
      setWeeklyReport(weeklyData);
      if (categoryData.length && !expenseForm.categoryId) {
        setExpenseForm((current) => ({ ...current, categoryId: String(categoryData[0].id) }));
      }
    } catch (error) {
      setMessage(error.message);
    }
  }

  async function submitAuth(event) {
    event.preventDefault();
    setMessage("");
    try {
      const endpoint = authMode === "login" ? "/api/auth/login" : "/api/auth/register";
      const payload =
        authMode === "login"
          ? { email: authForm.email, password: authForm.password }
          : authForm;
      const data = await api(endpoint, {
        method: "POST",
        body: JSON.stringify(payload)
      });
      applyAuth(data);
      setAuthForm({ name: "", email: "", password: "" });
    } catch (error) {
      setMessage(error.message);
    }
  }

  async function submitCategory(event) {
    event.preventDefault();
    try {
      await api("/api/categories", {
        method: "POST",
        body: JSON.stringify(categoryForm)
      });
      setCategoryForm({ name: "", color: "#ef476f" });
      await loadDashboard();
    } catch (error) {
      setMessage(error.message);
    }
  }

  async function submitExpense(event) {
    event.preventDefault();
    try {
      await api("/api/expenses", {
        method: "POST",
        body: JSON.stringify({
          ...expenseForm,
          amount: Number(expenseForm.amount),
          categoryId: Number(expenseForm.categoryId)
        })
      });
      setExpenseForm({
        title: "",
        amount: "",
        expenseDate: today,
        note: "",
        categoryId: categories[0] ? String(categories[0].id) : ""
      });
      await loadDashboard();
    } catch (error) {
      setMessage(error.message);
    }
  }

  async function deleteExpense(id) {
    try {
      await api(`/api/expenses/${id}`, { method: "DELETE" });
      await loadDashboard();
    } catch (error) {
      setMessage(error.message);
    }
  }

  async function download(path, filename) {
    try {
      const blob = await api(path, { headers: { Accept: "*/*" } });
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = filename;
      link.click();
      URL.revokeObjectURL(url);
    } catch (error) {
      setMessage(error.message);
    }
  }

  if (!tokens.accessToken) {
    return (
      <main className="auth-shell">
        <section className="auth-card">
          <div>
            <p className="eyebrow">Portfolio project</p>
            <h1>Expense Tracker SaaS</h1>
            <p className="supporting">
              JWT auth, refresh tokens, category-based tracking, weekly and monthly analytics, CSV/PDF exports.
            </p>
          </div>

          <div className="toggle">
            <button className={authMode === "login" ? "active" : ""} onClick={() => setAuthMode("login")}>
              Login
            </button>
            <button className={authMode === "register" ? "active" : ""} onClick={() => setAuthMode("register")}>
              Register
            </button>
          </div>

          <form className="stack" onSubmit={submitAuth}>
            {authMode === "register" ? (
              <input
                placeholder="Full name"
                value={authForm.name}
                onChange={(event) => setAuthForm({ ...authForm, name: event.target.value })}
              />
            ) : null}
            <input
              placeholder="Email"
              type="email"
              value={authForm.email}
              onChange={(event) => setAuthForm({ ...authForm, email: event.target.value })}
            />
            <input
              placeholder="Password"
              type="password"
              value={authForm.password}
              onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })}
            />
            <button className="primary-button" type="submit">
              {authMode === "login" ? "Sign in" : "Create account"}
            </button>
          </form>
          {message ? <p className="error-text">{message}</p> : null}
        </section>
      </main>
    );
  }

  return (
    <main className="dashboard-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Mini SaaS dashboard</p>
          <h1>Welcome, {user?.name || user?.sub || "User"}</h1>
        </div>
        <div className="topbar-actions">
          <a className="ghost-button" href={`${API_URL}/swagger-ui/index.html`} target="_blank" rel="noreferrer">
            Swagger
          </a>
          <button className="ghost-button" onClick={() => download(`/api/expenses/export/csv?startDate=${firstDayOfMonth}&endDate=${today}`, "expenses.csv")}>
            CSV
          </button>
          <button className="ghost-button" onClick={() => download(`/api/expenses/export/pdf?startDate=${firstDayOfMonth}&endDate=${today}`, "expenses.pdf")}>
            PDF
          </button>
          <button className="ghost-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {message ? <p className="error-text inline">{message}</p> : null}

      <section className="grid-layout">
        <section className="panel">
          <div className="panel-header">
            <h3>Create category</h3>
          </div>
          <form className="stack" onSubmit={submitCategory}>
            <input
              placeholder="Category name"
              value={categoryForm.name}
              onChange={(event) => setCategoryForm({ ...categoryForm, name: event.target.value })}
            />
            <input
              type="color"
              value={categoryForm.color}
              onChange={(event) => setCategoryForm({ ...categoryForm, color: event.target.value })}
            />
            <button className="primary-button" type="submit">
              Add category
            </button>
          </form>
        </section>

        <section className="panel">
          <div className="panel-header">
            <h3>New expense</h3>
          </div>
          <form className="stack" onSubmit={submitExpense}>
            <input
              placeholder="Title"
              value={expenseForm.title}
              onChange={(event) => setExpenseForm({ ...expenseForm, title: event.target.value })}
            />
            <input
              type="number"
              min="0.01"
              step="0.01"
              placeholder="Amount"
              value={expenseForm.amount}
              onChange={(event) => setExpenseForm({ ...expenseForm, amount: event.target.value })}
            />
            <input
              type="date"
              value={expenseForm.expenseDate}
              onChange={(event) => setExpenseForm({ ...expenseForm, expenseDate: event.target.value })}
            />
            <select
              value={expenseForm.categoryId}
              onChange={(event) => setExpenseForm({ ...expenseForm, categoryId: event.target.value })}
            >
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
            <textarea
              placeholder="Note"
              rows="3"
              value={expenseForm.note}
              onChange={(event) => setExpenseForm({ ...expenseForm, note: event.target.value })}
            />
            <button className="primary-button" type="submit">
              Save expense
            </button>
          </form>
        </section>

        <section className="summary panel">
          <p>Total this month</p>
          <strong>${Number(monthlyReport?.total || 0).toFixed(2)}</strong>
          <span>
            Week total: ${Number(weeklyReport?.total || 0).toFixed(2)}
          </span>
        </section>
      </section>

      <section className="charts-grid">
        <ChartCard title="Monthly categories" items={monthlyReport?.byCategory || []} mode="bar" />
        <ChartCard title="Weekly trend" items={weeklyReport?.trend || []} mode="line" />
      </section>

      <ExpenseTable expenses={expenses} onDelete={deleteExpense} />
    </main>
  );
}

export default App;
