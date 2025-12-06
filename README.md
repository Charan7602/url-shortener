# URL Shortener – Spring Boot, Redis, Postgres, AI Checks

A production-style URL shortener built with **Spring Boot**, **PostgreSQL**, and **Redis**, featuring:

- URL shortening & redirects
- Caching with Redis
- Click tracking & analytics
- AI-based URL safety checks (currently mocked, pluggable for OpenAI)
- Redis-backed rate limiting
- Global error handling

---

## Live Demo

Base URL (Render):

> https://url-shortener-4-n4va.onrender.com

Example flow:

1. `POST /api/shorten` → returns a `shortCode` and `shortUrl`
2. `GET /r/{shortCode}` → redirects to original URL
3. `GET /analytics/{shortCode}` → shows clicks + metadata
4. `GET /summary/{shortCode}` → shows AI-generated summary

---

## Features

- 🔗 **Shorten URLs**
    - `POST /api/shorten`
    - Returns `shortCode` and full `shortUrl` using environment-aware `baseUrl`.

- 🚀 **Fast redirects with Redis cache**
    - First lookup hits Postgres
    - Result is cached in Redis
    - Subsequent redirects served from cache

- 📊 **Analytics**
    - Total clicks stored using Redis counters (`clicks::<shortCode>`)
    - `GET /analytics/{shortCode}` shows:
        - longUrl
        - totalClicks
        - createdAt
        - expiresAt
        - lastAccessedAt
        - lastUserAgent (optional)

- 🧠 **AI URL Safety & Summary (Mocked)**
    - On shorten:
        - Mock AI checks if URL is safe (architecture ready for real LLM)
        - Mock AI returns a human-readable summary
        - Summary stored in Postgres and exposed via `/summary/{shortCode}`

- 🛡️ **Rate Limiting with Redis**
    - Redis-based **fixed-window** limiter on `POST /api/shorten`
    - Example: 2 requests / minute / IP (configurable)
    - On exceeding limit, API returns **HTTP 429** with structured error.

- 🧱 **Global Error Handling**
    - All errors return a consistent JSON structure
    - No raw stacktraces are leaked to clients

---

## Tech Stack

- **Backend:** Spring Boot (4.x), Spring Web, Spring Data JPA, Spring Cache
- **Database:** PostgreSQL
- **Cache / Counters:** Redis (`StringRedisTemplate`)
- **Build:** Maven
- **Deploy:** Render (free tier)
- **Language:** Java (17+)

---

## Architecture Overview

High-level flow:

- **Shorten URL:**
    1. Client → `POST /api/shorten`
    2. Rate limiter checks Redis
    3. AI layer (mock) classifies + summarizes URL
    4. URL + summary saved in Postgres
    5. Response includes `shortCode` and full `shortUrl`

- **Redirect:**
    1. Client → `GET /r/{shortCode}`
    2. Service resolves shortCode (Postgres + Redis cache)
    3. Click counter incremented in Redis
    4. `lastAccessedAt` + `lastUserAgent` updated in Postgres
    5. HTTP redirect to original URL

Redis is used for:
- Caching URL mappings (fast redirects)
- Counting clicks per `shortCode`
- Rate limiting per client IP

---

## Configuration & Profiles

The app is profile-aware:

- `application.properties` → default / local dev
- `application-prod.properties` → production (Render)

Key properties:

```properties
# Local (application.properties)
spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.redis.host=localhost
spring.redis.port=6379

# Base URL
app.base-url=http://localhost:8080

# Prod (application-prod.properties)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.redis.host=${REDIS_HOST}
spring.redis.port=${REDIS_PORT:6379}
spring.redis.password=${REDIS_PASSWORD:}

spring.jpa.hibernate.ddl-auto=validate

# Base URL for live environment
app.base-url=https://url-shortener-4-n4va.onrender.com

# Activate prod profile:

SPRING_PROFILES_ACTIVE=prod
java -jar target/urlShortener-0.0.1-SNAPSHOT.jar

```
---

## API Endpoints

Base URL:

- Local: `http://localhost:8080`
- Production: `https://url-shortener-4-n4va.onrender.com`

---

### 1. Shorten URL

Creates a new short URL.

**Endpoint**

POST /api/shorten

**Request Body**

```json
{
  "longUrl": "https://example.com",
  "expiryDays": 30
}

```

**Response**
```json
{
"shortUrl": "https://url-shortener-3-h3m0.onrender.com/r/XddC4dzp",
"shortCode": "XddC4dzp"
}

```

2. Redirect to Original URL

Redirects to the original long URL using the short code.

**Endpoint**
```json
GET /r/{shortCode}

Response

302 FOUND → Redirects to original URL

404 NOT_FOUND → Short code does not exist

410 GONE → URL expired or inactive
```

3. Get Analytics

Returns analytics information for a given short code.

**Endpoint**
```json
GET /analytics/{shortCode}


Example:
GET /analytics/XddC4dzp


Response

{
"shortCode": "XddC4dzp",
"longUrl": "https://example.com",
"totalClicks": 42,
"createdAt": "2025-12-06T09:11:35.288201400Z",
"expiresAt": "2026-01-05T09:11:35.288201400Z",
"lastAccessedAt": "2025-12-06T10:22:11.123456Z",
"lastUserAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)..."
}
```

4. Get URL Summary (AI - Mocked)

Returns the AI-generated summary for a short URL.

**Endpoint**
```json
GET /summary/{shortCode}


Example:
GET /summary/XddC4dzp


Response

{
"shortCode": "XddC4dzp",
"longUrl": "https://example.com",
"aiSummary": "This website provides educational content related to software development and programming."
}
```

5. Error Response Format

All errors follow a consistent JSON structure.

```json
Example (Rate Limit Exceeded)

{
"status": 429,
"errorCode": "RATE_LIMIT_EXCEEDED",
"message": "Too many requests. Please try again after some time",
"path": "/api/shorten",
"details": null,
"timestamp": "2025-12-06T09:11:35.288201400Z"
}
```

---




