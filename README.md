# Archify

Archify is a local full-stack project generator:
- `backend/`: Spring Boot API that exposes recipes and returns generated ZIP projects.
- `ui/`: React + Vite UI for selecting a recipe and generating a project.
- `generator-core/`: Core generation engine used by the backend.

## Prerequisites

- Java 17
- Maven 3.9+
- Node.js (tested with v24) and npm

## Local Run

1. Start backend (from repo root):

```bash
mvn -pl backend -am spring-boot:run
```

Backend runs on `http://localhost:8080`.

2. Start UI (new terminal):

```bash
cd ui
npm install
npm run dev
```

UI runs on `http://localhost:5173`.

3. Open the app:
- Visit `http://localhost:5173`
- Select a recipe, edit config (form or YAML), click **Generate**, and download the ZIP.

## Useful Commands

From repo root:

```bash
# Build all Maven modules
mvn clean install
```

From `ui/`:

```bash
# Production build
npm run build

# Preview production build
npm run preview
```

## API Endpoints (Backend)

- `GET /recipes` - list available generation recipes
- `POST /generate` - generate and download ZIP (`application/zip`)

Note: backend CORS currently allows `http://localhost:5173`.
