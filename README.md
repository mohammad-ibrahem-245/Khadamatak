# Khadamatak Microservices

This repository contains a Spring Boot microservices system with service discovery and a gateway.

## Services

- `Eureka server` (`8761`) - service discovery
- `Gateway` (`8765`) - auth + JWT + routing
- `User-api` (`8080`) - users
- `Posts` (`8081`) - posts, reviews, favorites
- `Bookings` (`8083`) - bookings lifecycle
- `notifications` (`8084`) - notifications

## Architecture Notes

- Gateway uses service discovery routing with lowercase service ids.
- Public auth endpoints are under `/auth/**`.
- Other endpoints require Bearer JWT.
- Gateway injects trusted headers to downstream services:
  - `X-User-Name`
  - `X-User-Id`
  - `X-Is-Admin`
  - `X-Is-Provider`

## Run With Docker Compose

### 1) Start everything (one command)

```bash
cd /Users/mohammadali/Desktop/khadamatak
docker compose up --build
```

### 2) Stop

```bash
docker compose down
```

## Authentication Flow

### Signup

`POST /auth/signup`

```json
{
  "email": "user@example.com",
  "phoneNumber": "0500000000",
  "name": "User One",
  "password": "123456",
  "bio": "...",
  "image": "...",
  "career": "..."
}
```

### Login

`POST /auth/login`

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

Response:

```json
{
  "token": "<jwt>"
}
```

Use token in protected routes:

```text
Authorization: Bearer <jwt>
```

---

## Endpoints (By Service)

Base URL for external calls: `http://localhost:8765`

Because gateway uses discovery locator, call service routes with service prefix:
- User API: `/user-api/**`
- Posts: `/posts/**`
- Bookings: `/bookings/**`
- Notifications: `/notifications/**`

> Auth endpoints are direct on gateway: `/auth/**` (no service prefix).

## Gateway (`/auth`)

### `POST /auth/signup`
Creates user through `User-api`.

Body:

```json
{
  "email": "user@example.com",
  "phoneNumber": "0500000000",
  "name": "User One",
  "password": "123456",
  "bio": "...",
  "image": "...",
  "career": "..."
}
```

### `POST /auth/login`
Returns JWT with claims: `sub`, `userId`, `isAdmin`, `isProvider`.

Body:

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

### `GET /auth/test`
Health check for gateway.

---

## User API (`/user-api`)

### `GET /user-api/search/{email}`
Get user by email.

### `GET /user-api/getall/{isProvider}`
Get all users filtered by provider flag.

Headers:
- `Authorization: Bearer <jwt>` (admin only)

### `POST /user-api/signup`
Create user.

Body:

```json
{
  "email": "provider@example.com",
  "phoneNumber": "0500000001",
  "name": "Provider",
  "password": "123456",
  "bio": "provider bio",
  "image": "...",
  "career": "plumber"
}
```

### `PUT /user-api/update`
Update current user data (or admin can update any user).

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "email": "user@example.com",
  "name": "New Name",
  "password": "newpass",
  "bio": "updated",
  "image": "...",
  "career": "..."
}
```

### `DELETE /user-api/delete/{email}`
Delete self or admin delete.

Headers:
- `Authorization: Bearer <jwt>`

---

## Posts (`/posts`)

### Post endpoints

### `POST /posts/add`
Create post (owner comes from JWT user unless admin override).

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "title": "Fix AC",
  "content": "AC maintenance service",
  "image": "https://...",
  "price": 120
}
```

### `GET /posts/all`
Get current user posts (or all for admin).

Headers:
- `Authorization: Bearer <jwt>`

Optional query:
- `owner=<userId>`

### `GET /posts/{id}`
Get post by id (owner/admin constrained).

Headers:
- `Authorization: Bearer <jwt>`

### `PUT /posts/{id}`
Update post (owner/admin only).

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "title": "Fix AC - updated",
  "content": "new details",
  "image": "https://...",
  "price": 150
}
```

### `DELETE /posts/{id}`
Delete post (owner/admin only).

Headers:
- `Authorization: Bearer <jwt>`

### Review endpoints

### `POST /posts/reviews/add`
Create review.

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "content": "Great service",
  "postId": 1,
  "rating": 4.5
}
```

### `GET /posts/reviews/allreviews`
Get all reviews.

### `GET /posts/reviews/{id}`
Get review by id.

### `PUT /posts/reviews/{id}`
Update review (author/admin only).

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "content": "Updated comment",
  "postId": 1,
  "rating": 5.0
}
```

### `DELETE /posts/reviews/{id}`
Delete review (author/admin only).

Headers:
- `Authorization: Bearer <jwt>`

### `GET /posts/reviews/post/{id}`
Get all reviews for post id.

### Favorite endpoints

### `POST /posts/favirote`
Add favorite.

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "userId": 10,
  "postId": 1
}
```

`userId` is enforced from JWT for non-admin users.

### `DELETE /posts/favirote/{userId}/{postId}`
Delete favorite (self/admin only).

Headers:
- `Authorization: Bearer <jwt>`

---

## Bookings (`/bookings`)

### `POST /bookings/`
Create booking.

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "providerID": 20,
  "bookingDate": "2026-04-12T00:00:00.000+00:00",
  "bookingTime": "10:30:00",
  "longitude": "46.6753",
  "latitude": "24.7136"
}
```

`userId` is enforced from JWT for non-admin users.

### `GET /bookings/`
List bookings with role-aware visibility.

Headers:
- `Authorization: Bearer <jwt>`

Optional query:
- `userId=<id>`
- `providerId=<id>`

### `GET /bookings/{id}`
Get one booking (owner/provider/admin).

Headers:
- `Authorization: Bearer <jwt>`

### `PUT /bookings/{id}`
Update booking (owner/provider/admin with rules).

Headers:
- `Authorization: Bearer <jwt>`

Body:

```json
{
  "providerID": 20,
  "bookingDate": "2026-04-15T00:00:00.000+00:00",
  "bookingTime": "11:00:00",
  "longitude": "46.6753",
  "latitude": "24.7136"
}
```

### `PUT /bookings/{id}/status?status=ACCEPTED`
Update booking status.

Allowed values:
- `ACCEPTED`
- `REJECTED`
- `PENDING`
- `CANCELLED`
- `COMPLETED`

Headers:
- `Authorization: Bearer <jwt>`

Rules:
- user can cancel own booking
- assigned provider can accept/reject/complete
- admin can override

### `DELETE /bookings/{id}`
Delete booking (owner/admin).

Headers:
- `Authorization: Bearer <jwt>`

---

## Notifications (`/notifications`)

### `POST /notifications/notifications`
Create notification.

Body:

```json
{
  "userId": 10,
  "message": "Your booking was accepted"
}
```

> Used internally by Feign from other services.

### `GET /notifications/notifications/user/{userId}`
Get notifications for a user.

Headers:
- `Authorization: Bearer <jwt>`

Rule:
- only same `userId` can read their notifications.

### `DELETE /notifications/notifications/{id}`
Delete notification by id.

---

## Docker Files Included

- `Eureka server/Dockerfile`
- `Gateway/Dockerfile`
- `User-api/Dockerfile`
- `Posts/Dockerfile`
- `Bookings/Dockerfile`
- `notifications/Dockerfile`
- `docker-compose.yml`

## Notes

- Dockerfiles are multi-stage and build each service internally with Maven.
- In Docker Compose, each service uses its own Postgres container.
- Feign auto-notification is currently enabled in `Posts` and `Bookings`.

