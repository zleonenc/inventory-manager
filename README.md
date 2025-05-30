# Breakable Toy I: Inventory Manager

## 🚀 Overview

This is an Inventory Management Application designed to help users efficiently manage products and categories. It allows creating, editing, deleting, filtering, sorting, and tracking inventory data. The application is built with React and Spring Boot, using in-memory storage for now, but structured to support future database integration.

---

## Functional Requirements

*   **Product Management**:
    *   Create, edit, and delete products.
    *   Fields: name, category, quantity, unit price, expiration date.
*   **Filtering & Sorting**:
    *   Filter by name (partial match), category (multi-select), and stock availability.
    *   Sort by columns: name, category, price, stock, expiration date. The user should have the ability to sort by two different columns at the same time.
*   **Out of Stock Toggle**:
    *   Mark/unmark products as in or out of stock.
*   **Pagination**:
    *   10 products per page.
*   **Inventory Metrics**:
    *   Total products in stock.
    *   Total inventory value.
    *   Average price of in-stock products.
    *   Metrics shown by category and overall.

---

## UI Requirements

*   **Controls**:
    *   Filters for name, category, availability.
    *   Button to create a product via modal.
*   **Table**:
    *   Sortable columns with icons.
    *   Toggle button to mark product out of stock (quantity set to 0 / reset to 10).
    *   Edit and delete buttons.
    *   Pagination controls.
*   **Visual Cues**:
    *   Row background color based on expiration:
        *   No date: none
        *   \<1 week: red
        *   1–2 weeks: yellow
        *   \>2 weeks: green
    *   Stock color:
        *   \>10: none
        *   5–10: orange
        *   \<5: red
        *   Out of stock: strike-through text

---

## 🌐 API Endpoints

| Model | Method | Endpoint | Description |
| --- | --- | --- | --- |
| Product | GET | /products | List products with support for filtering by name, category, and availability. Supports pagination and sorting. |
| Product | PUT | /products/{id} | Update a product (name, category, price, stock, expiration date). |
| Product | PUT | /products/{id}/instock | Mark a product as out of stock (no inventory). |
| Product | PUT | /products/{id}/outofstock | Mark a product as in stock (restore inventory). |
| Product | DEL | /products/{id} | Deletes a product. |
| Product | DEL | /products/clear | Deletes all products. |
| Product | GET | /products/metrics | Gets inventory metrics as per the requiremnts. |
| Category | GET | /categories | List categories. |
| Category | POST | /categories | Create a new category with validation. |
| Category | PUT | /categories/{id} | Update a category (name). |
| Category | DEL | /categories/{id} | Deletes a category. |
| Category | DEL | /categories/clear | Deletes all categories. |

**You can quickly test the APIs using the Postman Collection inside the backend folder.**

---

## Technologies Used

### Backend

*   Java 21 LTS
*   SpringBoot
*   Maven
*   Runs on port **9090**

### Frontend

*   TypeScript
*   ReactJS
*   React Context
*   Material UI 
*   Runs on port **8080**

---

## Running the Application

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run # Runs the backend on http://localhost:9090
mvn test # Runs backend tests
```

### Frontend

```bash
cd frontend
npm install
npm run start # Runs the app on http://localhost:8080
npm run tests # Runs tests
```