# Music Shop Application

Modernized full-stack music shop application with a Spring Boot backend and React frontend.

## Functionalities & API Endpoints

### Common Features
- **User Authentication**: JWT + cookie-backed session flow with `USER` and `ADMIN` roles.
- **Product Exploration**: Filter/sort product listing and open product details.
  - **Home Page**: Product list uses lightweight DTO/page responses.
  - **Product Details**: Detailed product view with images and inventory metadata.
- **Standardized Error Contract**: Backend returns consistent error response shape and codes.

### User Specific
- **Shopping Cart Management**: Add/update/remove items from authenticated user cart.
- **Checkout**: Validated checkout flow with stock and payment checks.
- **User Profile Management**: Read/update profile by ownership policy.
- **Live Notifications**: SSE notifications for cart/product-related events.

### Admin Specific
- **Product Management**: Create/update/delete products, apply discounts, manage product images.
- **Category Management**: Create subcategories under parent categories.
- **Image Management**: Upload, delete, reorder, and assign primary product images.
- **Role-Restricted Mutations**: Sensitive endpoints protected via role checks + method-level authorization.

## Project Architecture

### Backend Layers
- **Controller Layer**: API contracts only (request/response DTOs).
- **Application Layer (Use Cases)**: Orchestration and access-policy flow.
- **Service Layer**: Business/domain logic.
- **Repository Layer**: Persistence and ownership lookups.

### Frontend Layers
- **UI Layer**: Pages/components.
- **Hook/Context Layer**: State ownership and orchestration.
- **API Layer**: Domain API modules + shared transport.
- **Mapper/Type Layer**: OpenAPI DTO -> frontend domain models.

## API Endpoints

### Auth
- **POST** `/api/auth/register`: Register user.
- **POST** `/api/auth/login`: Login user.
- **POST** `/api/auth/logout`: Logout user.
- **GET** `/api/auth/me`: Current session user.

### Product
- **GET** `/api/products`: List all products.
- **GET** `/api/products/{id}`: Get product details.
- **POST** `/api/products`: Add a new product (ADMIN).
- **PUT** `/api/products/{id}`: Update product information.
- **PATCH** `/api/products/{id}`: Partially update product (ADMIN).
- **DELETE** `/api/products/{id}`: Delete product (ADMIN).
- **PATCH** `/api/products/{id}/apply-discount`: Apply discount (ADMIN).

### Product Images (ADMIN)
- **POST** `/api/products/{productId}/images`: Upload image.
- **DELETE** `/api/products/{productId}/images/{imageId}`: Delete image.
- **PATCH** `/api/products/{productId}/images/{imageId}/primary`: Set primary image.
- **PUT** `/api/products/{productId}/images/reorder`: Reorder images.

### Catalog
- **GET** `/api/categories`: List categories.
- **POST** `/api/categories`: Create category/subcategory (ADMIN).
- **GET** `/api/categories/{slug}/reviews`: Category reviews.
- **GET** `/api/brands`: List brands.

### Cart
- **POST** `/api/carts/my/products/{productId}`: Add product to current user cart.
- **GET** `/api/carts/my/details`: Get current user cart details.
- **PUT** `/api/carts/details/{detailId}`: Update cart item quantity.
- **DELETE** `/api/carts/details/{detailId}`: Remove cart item.
- **DELETE** `/api/carts/my/clear`: Clear cart.

### Orders
- **POST** `/api/orders/checkout`: Checkout current user cart.

### User Management
- **GET** `/api/users/{userId}`: Retrieve user details (self/admin).
- **PUT** `/api/users/{userId}`: Update user details (self/admin).

### Notification System
- **GET** `/api/notifications/stream`: Open SSE stream.
- **GET** `/api/notifications`: Get current user notifications.
- **PATCH** `/api/notifications/{notificationId}/read`: Mark notification as read.
- **PATCH** `/api/notifications/read-all`: Mark all notifications read.
- **DELETE** `/api/notifications/{notificationId}`: Delete notification.

## Design Patterns
1. **Use-Case/Application Layer**: Explicit orchestration boundary between controllers and services.
2. **Factory + Strategy**: Discount application logic.
3. **Adapter**: Payment gateway adapters (e.g., Stripe/PayPal behind a common interface).
4. **Decorator**: Checkout pricing pipeline (tax/shipping/coupon decorators).
5. **Observer/Event-driven updates**: Notification and cart/product change propagation.
6. **Mapper Boundary (MapStruct + frontend mappers)**: Contract-safe DTO to domain conversion.
7. **Repository + Specification**: Query filtering/sorting and ownership lookups.

## Running the Project

### Option A: Docker Compose (Recommended Quick Start)
1. Run:
```bash
docker compose up --build
```
2. Services:
- Frontend: `http://localhost`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Notes:
- Docker sets `SPRING_DATASOURCE_*` and `APP_UPLOAD_DIR=/app/uploads`.
- Uploaded images persist in Docker volume `uploads-data`.

### Option B: Local Development

1. Backend config:
- Copy `backend/src/main/resources/application.properties.example` values into your local setup.
- Ensure PostgreSQL is available at configured `spring.datasource.*`.

2. Start backend:
```bash
cd backend
mvn spring-boot:run
```

3. Start frontend:
```bash
cd frontend
npm install
npm run dev
```

## Technical Aspects
- **Backend**: Spring Boot, Spring Security, JPA/Hibernate, MapStruct, SpringDoc OpenAPI.
- **Frontend**: React, Vite, TypeScript, React Query, Vitest, Playwright.
- **Architecture Enforcement**: ArchUnit (backend), ESLint + dependency-cruiser (frontend).
- **API Documentation**: Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

## Testing & Quality Gates

### Backend
```bash
cd backend
mvn test -Dmaven.repo.local=/tmp/m2
```

### Frontend
```bash
cd frontend
npm run check:all
npm run e2e
```

### OpenAPI Type Generation (Frontend)
```bash
cd backend
mvn -q -Dtest=OpenApiSnapshotGeneratorTest -Dopenapi.output=../frontend/src/types/generated/openapi.json test -Dmaven.repo.local=/tmp/m2
cd ../frontend
npx openapi-typescript src/types/generated/openapi.json -o src/types/generated/openapi.ts
```
