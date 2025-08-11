# Breakable Toy I: Inventory Manager

## 🚀 Overview

Inventory Manager is a full‑stack app to manage products and categories: create/update/delete items, filter/sort/paginate listings, toggle stock status, and view inventory metrics. It’s built with React + TypeScript (frontend) and Spring Boot 3 (backend). Data is stored in memory and seeded from JSON files, leaving room for a future database.

---

## Quick start

- Requirements
    - Java 21 (Azul Zulu, Temurin, or similar)
    - Node.js + npm
    - Git (optional for cloning)

- Start backend
    ```
    cd backend
    mvn clean install
    mvn clean test
    mvn spring-boot:run
    ```
    Backend boots at http://localhost:9090

- Start frontend
    ```powershell
    cd frontend
    npm install
    npm test
    npm start
    ```
    Frontend runs at http://localhost:8080

---

## Environment variables

- Backend (`backend/src/main/resources/application.properties`)
    - server.port=9090
    - app.cors.allowed-origins=http://localhost:8080
    - app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
    - app.cors.allowed-headers=*
    - app.cors.allow-credentials=true

    Adjust allowed origins if you serve the frontend from a different host/port.

- Frontend (`frontend/.env`)
    - PORT=8080
    - REACT_APP_API_URL=http://localhost:9090

    Point REACT_APP_API_URL to your backend if the port/host changes.

---

## Run, build, and test

### Backend

```bash
cd backend
mvn clean install
mvn test
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm test
npm start
```

---

## Swagger and OpenAPI

- Swagger UI: http://localhost:9090/swagger-ui/index.html
- OpenAPI JSON: http://localhost:9090/v3/api-docs

There’s also a Postman collection in `backend/Inventory Manager.postman_collection.json`.

---

## API endpoints (summary)

All endpoints are prefixed with `/api`.

| Model | Method | Endpoint | Description |
| --- | --- | --- | --- |
| Product | GET | `/api/products` | List products with filtering (name, categories, availability), sorting, and pagination. |
| Product | POST | `/api/products` | Create a product (validated DTO). |
| Product | PUT | `/api/products/{id}` | Update a product. |
| Product | PUT | `/api/products/{id}/instock` | Mark a product as in stock. |
| Product | PUT | `/api/products/{id}/outofstock` | Mark a product as out of stock. |
| Product | DELETE | `/api/products/{id}` | Delete a product. |
| Product | DELETE | `/api/products/clear` | Delete all products. |
| Product | GET | `/api/products/metrics` | Inventory metrics (overall and by category). |
| Category | GET | `/api/categories` | List active categories. |
| Category | POST | `/api/categories` | Create a category (validated). |
| Category | PUT | `/api/categories/{id}` | Update a category (name). |
| Category | DELETE | `/api/categories/{id}` | Delete a category. |
| Category | DELETE | `/api/categories/clear` | Delete all categories. |

Products listing supports query parameters: `name` (string), `categories` (multi: `?categories=1&categories=2`), `available` (`instock`|`outofstock`), `page`, `size`, `primarySortBy`/`primarySortDirection`, `secondarySortBy`/`secondarySortDirection`.

Errors follow consistent 400/404 semantics with validation via `@Valid` and a global exception handler.

---

## Folder structure

```
inventory-manager/
├─ backend/
│  ├─ src/main/java/com/example/inventory/...  # Controllers, services, repositories, models, DTOs
│  ├─ src/main/resources/                      # application.properties, seed JSON
│  ├─ src/test/java/com/example/inventory/...  # Unit + integration tests (MockMvc)
│  ├─ pom.xml                                  # Spring Boot 3.4.5, springdoc 2.7.0
│  └─ Inventory Manager.postman_collection.json
└─ frontend/
     ├─ src/
     │  ├─ components/                           # Product & Category UI (tables, forms, filters)
     │  ├─ context/                              # ProductContext & CategoryContext
     │  ├─ services/                             # API client and endpoints
     │  ├─ hooks/                                # useAsync, useDialog, usePagination
     │  ├─ pages/                                # ProductPage, CategoryPage
     │  └─ types/                                # TS types & DTOs
     ├─ .env                                     # REACT_APP_API_URL, PORT
     └─ package.json
```

---

## Design overview

### Backend

- Spring Boot 3 (Java 21). Thin controllers annotated with OpenAPI docs.
- Service layer encapsulates business rules and 404 flows; repositories are in‑memory with soft‑delete (`active` flag).
- Data seeding from `products.json` and `categories.json` on startup.
- Validation via `spring-boot-starter-validation` (`@Validated` controllers, `@Valid` DTOs).
- Global exception handling: 400 for validation, 404 for not found; consistent error response shape.
- OpenAPI via `springdoc-openapi-starter-webmvc-ui` with Swagger UI.
- Tests: JUnit 5, Mockito, MockMvc integration tests for filters/sorts/pagination/CRUD.

### Frontend

- React 19 + TypeScript, Material UI.
- Context providers for products and categories with `useCallback`/`useMemo` to stabilize references.
- Components: filter, table (sortable/paginated), forms, modals; a11y improvements.
- API client consolidates fetch logic, serializes query params, and normalizes errors.
- Tests: React Testing Library for services, components, and context behavior.

---

## Tips & troubleshooting

- If CORS blocks requests, update `app.cors.allowed-origins` in the backend to match your frontend origin.
- Swagger UI shows the effective request/response models and helps verify query parameters.

---

## License

Educational/breakable toy project. Use at your own discretion.