# Token Bucket Rate Limiter 🚦

A production-ready **Token Bucket Rate Limiter** built with **Spring Boot** and **Redis**, supporting tiered rate limiting for FREE and PREMIUM users.

---

## 🧠 What is Token Bucket Rate Limiting?

Token Bucket is a rate limiting algorithm where:
- Each user has a **bucket** with a fixed number of tokens
- Every request **consumes 1 token**
- Tokens **refill over time** at a fixed rate
- If bucket is **empty** → request is **blocked**

---

<img width="1536" height="1024" alt="ChatGPT Image Jun 29, 2026, 02_32_46 AM" src="https://github.com/user-attachments/assets/5f3c5470-f315-4dae-baaa-754ccd3dc2e7" />


## ✨ Features

- ✅ Tiered rate limiting (FREE & PREMIUM users)
- ✅ Per-user token tracking via Redis
- ✅ Automatic token refill logic
- ✅ Request interception before hitting controller
- ✅ Clean separation of concerns (Interceptor → Service → Redis)

---

## 👥 User Tiers

| Tier    | Max Tokens | Refill Rate        |
|---------|------------|--------------------|
| FREE    | 5 tokens   | 1 token per 1 hour |
| PREMIUM | 12 tokens  | 1 token per 45 min |

---

## 🛠️ Tech Stack

| Technology      | Purpose                        |
|-----------------|--------------------------------|
| Java 17         | Core language                  |
| Spring Boot     | Backend framework              |
| Spring Web      | REST API                       |
| Spring Data Redis | Redis integration            |
| Redis           | Token storage & tracking       |
| Lombok          | Boilerplate reduction          |
| Postman         | API testing                    |

---

## 📁 Project Structure

```
src/main/java/RateLimiter/Bucket/
│
├── config/
│   └── WebConfig.java
│
├── controller/
│   └── ApiController.java
│
├── filter/
│   └── RateLimitInterceptor.java
│
├── model/
│   ├── User.java
│   └── UserTier.java
│
└── service/
    └── TokenBucketService.java
```

## ⚙️ Configuration

`src/main/resources/application.properties`

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379

# FREE user
rate.limit.free.capacity=5
rate.limit.free.refill.interval.seconds=3600

# PREMIUM user
rate.limit.premium.capacity=12
rate.limit.premium.refill.interval.seconds=2700
```

---

## 🔄 Data Flow

| Step | Component | Action |
|------|-----------|--------|
| 1 | Client | Sends GET /api/hello with X-Client-ID + X-User-Tier headers |
| 2 | RateLimitInterceptor | Intercepts request before controller |
| 3 | RateLimitInterceptor | Extracts clientId and tier from headers |
| 4 | TokenBucketService | allowRequest(clientId, tier) called |
| 5 | Redis | GET token_bucket:{clientId} |
| 6 | TokenBucketService | Refill logic — add tokens if interval passed |
| 7 | TokenBucketService | Tokens > 0? |
| 8 ✅ | ApiController | Token available → decrement → 200 OK |
| 8 ❌ | Interceptor | No token → 429 Too Many Requests |

## 🗃️ Redis Keys

| Key | Value |
|-----|-------|
| `token_bucket:{clientId}` | Current token count |
| `last_refill:{clientId}` | Last refill timestamp (seconds) |

---

## 🚀 How to Run

**1. Start Redis:**
```bash
redis-server
```

**2. Run Spring Boot app:**
```bash
mvn spring-boot:run
```

**3. Test via Postman:**
GET http://localhost:8080/api/hello
Headers:

X-Client-ID: aman123

X-User-Tier: FREE

---

## 📡 API Endpoints

| Method | Endpoint    | Description         |
|--------|-------------|---------------------|
| GET    | /api/hello  | Sample hello API    |
| GET    | /api/data   | Sample data API     |

**Required Headers:**

| Header       | Values           | Description        |
|--------------|------------------|--------------------|
| X-Client-ID  | any string       | Unique user ID     |
| X-User-Tier  | FREE or PREMIUM  | User tier          |

**Responses:**

| Status | Meaning                        |
|--------|--------------------------------|
| 200 OK | Request allowed                |
| 429    | Rate limit exceeded            |

---

## 🧪 Test Results

| User     | Tier    | Requests Allowed | 429 On    |
|----------|---------|------------------|-----------|
| aman123  | FREE    | 5                | 6th req   |
| rahul455 | PREMIUM | 12               | 13th req  |

---

## 💡 Concepts Learned

- ✅ **Token Bucket Algorithm** — how rate limiting works internally
- ✅ **Redis as a fast in-memory store** — storing and retrieving tokens
- ✅ **Spring Boot Interceptors** — preHandle() to intercept requests before controller
- ✅ **HandlerInterceptor vs Filter** — difference and when to use which
- ✅ **StringRedisTemplate** — bridge between Java and Redis
- ✅ **@Value annotation** — reading config from application.properties
- ✅ **Tiered system design** — different limits for different user types
- ✅ **Single Responsibility Principle (SOLID)** — each class has one job
- ✅ **Separation of Concerns** — filter, service, controller all separate
- ✅ **Lombok** — @RequiredArgsConstructor, @Data to reduce boilerplate
- ✅ **HTTP Status Codes** — 200 OK, 429 Too Many Requests

---

## 👨‍💻 Author

**Aman**
- Backend Developer | Java Spring Boot | Redis | System Design
