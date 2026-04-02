# 📧 Email Notification Service

A Spring Boot microservice for sending email notifications asynchronously with retry support, template rendering, and delivery tracking.

---

## 🚀 Features

- REST API to trigger email notifications
- Queue-based processing (DB-backed)
- Retry logic for failed emails
- Delivery status tracking (PENDING, PROCESSING, SENT, FAILED)
- Thymeleaf HTML email templates
- PostgreSQL persistence
- Unit + Controller + Integration tests

---

## 🧱 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- JavaMailSender (SMTP)
- JUnit + Mockito

---

## 📡 API

### Create Email Notification

```http
POST /api/notifications/email
```

### Request Body

```json
{
  "to": "user@example.com",
  "subject": "Welcome!",
  "templateId": "welcome-email",
  "templateData": "{\"firstName\":\"John\"}"
}
```

### Response

```json
{
  "id": 1,
  "to": "user@example.com",
  "subject": "Welcome!",
  "templateId": "welcome-email",
  "status": "PENDING",
  "retryCount": 0,
  "lastError": null,
  "createdAt": "2026-03-30T12:00:00Z"
}
```

---

### Get Notification Status

```http
GET /api/notifications/{id}
```

---

## 🧠 How It Works

1. API receives request  
2. Saves notification to DB (PENDING)  
3. Background worker polls DB every few seconds  
4. Email is rendered using Thymeleaf templates  
5. Email is sent via SMTP  
6. Status is updated (SENT or FAILED)  
7. Retries up to 3 times if needed  

---

## 📂 Templates

Templates are stored in:

```bash
src/main/resources/templates/
```

Example files:
- welcome-email.html
- password-reset.html

Templates use Thymeleaf:

```html
Hello <span th:text="${firstName}">User</span>
```

---

## 🧪 Testing

This project includes:

- Unit tests (service layer)  
- Controller tests (MockMvc)  
- Integration test (DB → worker → send)  

External dependencies (SMTP) are mocked in integration tests.

---

## ⚙️ Run Locally

```bash
./mvnw spring-boot:run
```

---

## 📌 Example Curl
## LOCAL CURL
curl -X POST http://localhost:8080/api/notifications/email \
-H "Content-Type: application/json" \
-d '{
"to":"j_n_camp@hotmail.com",
"subject":"Welcome test",
"templateId":"welcome-email",
"templateData":"{\"name\":\"John\"}"
}'

curl -X POST http://localhost:8080/api/notifications/email \
-H "Content-Type: application/json" \
-d '{
"to":"j_n_camp@hotmail.com",
"subject":"Password Reset",
"templateId":"password-reset",
"templateData":"{\"resetLink\":\"https://example.com/reset\"}"
}'
curl http://localhost:8080/api/notifications/50

```bash
curl -X POST https://email-notification-service-production.up.railway.app/api/notifications/email   -H "Content-Type: application/json"   -d '{
"to": "your_target_email@example.com",
"subject": "your subject line",
"templateId": "welcome-email",
"templateData": "{\"firstName\":\"addresee first name\"}"
}'

curl -X POST https://email-notification-service-production.up.railway.app/api/notifications/email \
  -H "Content-Type: application/json" \
  -d '{
    "to": "your_email@example.com",
    "subject": "Password Reset",
    "templateId": "password-reset",
    "templateData": "{\"resetLink\":\"https://example.com/reset?token=abc123\"}"
  }'

## 🎯 Purpose

This project demonstrates a production-style notification microservice with:

- asynchronous processing  
- retry logic  
- template-based email rendering  
- clean REST API design  

---

## 👤 Author

John Camp
