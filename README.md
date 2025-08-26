
# 📦 Project: Auth + Data API (Dockerized)

## 🗂️ Repository Structure

```

/auth-api        – authentication service (Spring Boot)
/data-api        – text‐processing service (Spring Boot)
docker-compose.yml
README.md        – this file

````

---

## 🚀 Quick Start

> **Prerequisites:** Java 17+, Maven, Docker & Docker Compose

### 1️⃣ Build the JARs

```bash
mvn -f pom.xml clean package -DskipTests
````

### 2️⃣ Launch with Docker Compose

```bash
docker compose up -d --build
```

* **auth-api** will listen on `http://localhost:8080/api/auth`
* **data-api** will be called internally at `http://data-api:8081/api/transform`

---

## 🔐 Usage Examples (PowerShell)

> Replace `test@example.com`/`pass123` with your own credentials.


 1) Register a new user
```powershell
$body = @{
    username = "test@example.com"
    password = "pass123"
} | ConvertTo-Json

Invoke-WebRequest `
  -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```
 2) Log in and capture the JWT
```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$token = ($response.Content | ConvertFrom-Json).token
```
3) Call the protected /process endpoint
```powershell
 Invoke-WebRequest `
  -Uri "http://localhost:8080/api/process" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ "Authorization" = "Bearer $token" } `
  -Body '{"text":"hello world"}'
```
 4) Check logs 

```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/process/logs" -Method GET -Headers @{"Authorization" = "Bearer $token"}
$response.Content | ConvertFrom-Json
```
## 🔍 Inspect Records
5) Received result: DLROW OLLEH
```powershell
 id         : cb0d77db-8833-4347-a27a-4e2afc8caf44
   user       : test@example.com
   inputText  : hello world
   outputText : DLROW OLLEH
   createdAt  : 2025-08-25T20:54:15.66569
```
---

## ✅ Acceptance Criteria

* **Register**, **Login**, and **/process** must work via `localhost` after `docker compose up`.
* **Service B** rejects requests missing or with an invalid `X-Internal-Token`.
* **Service A** persists each processed request in the `processing_logs` table.
* README provides clear, end-to-end run & test instructions.

---

## 🛑 Tear Down

```bash
docker compose down
```

---

> For any issues:
> • Ensure ports 8080/8081 are free
> • Docker & Java 17 are installed correctly
> • JWT and internal token environment variables are set in `docker-compose.yml`
