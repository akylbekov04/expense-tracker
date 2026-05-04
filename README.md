# Expense Tracker SaaS

Mini SaaS-style expense tracker built for a GitHub portfolio. 
The project demonstrates a Spring Boot REST API, PostgreSQL persistence,
JWT authentication with refresh tokens, Dockerized local setup, 
and a lightweight React dashboard.

## Stack

- Spring Boot 3
- Spring Security + JWT + refresh tokens
- PostgreSQL + Spring Data JPA
- React + Vite
- Docker + Docker Compose
- Swagger / OpenAPI

## Features

- User registration and login
- Access token + refresh token flow
- Default and custom expense categories
- Expense creation and deletion
- Weekly and monthly analytics
- CSV and PDF exports
- Swagger UI for API showcase

## Project structure

- `backend` - Spring Boot REST API
- `frontend` - React dashboard
- `docker-compose.yml` - full local environment

## Run locally

### Backend

1. Start PostgreSQL, for example with Docker:

```bash
docker compose up postgres -d
```

2. Run the API:

```bash
cd backend
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/expense-tracker` and Swagger UI at `http://localhost:8080/expense-tracker/swagger-ui/index.html`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The dashboard will be available at `http://localhost:5173`.

## Run with Docker

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080/expense-tracker`
- PostgreSQL: `localhost:5432`

## Key API endpoints

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `GET /api/v1/category`
- `POST /api/v1/category`
- `GET /api/v1/expense`
- `POST /api/v1/expense`
- `DELETE /api/v1/expense/{id}`
- `GET /api/v1/reports/monthly`
- `GET /api/v1/reports/weekly`
- `GET /api/v1/expense/export/csv`
- `GET /api/v1/expense/export/pdf`

## Portfolio angle

This repo is designed to show:

- Clean REST API design
- Token-based auth flow
- Data modeling with relational storage
- Containerized developer workflow
- A simple but usable frontend over the same API
