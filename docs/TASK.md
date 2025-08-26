Тестове завдання на волонтерську посаду “Backend Developer” в WinWin Travel


Ми не обмежуємо Вас по часу, але було б дуже добре, якби Ви зробили тестове завдання та надіслали  https://t.me/WWW_BE_Bot  протягом 3 днів.


Удачі з виконанням ТЗ! 🎯
Впевнена, що твої навички засяють. Пам’ятай, головне - увага до деталей. Тримайся, і нехай натхнення буде з тобою! 💡


Mini Test: Two Spring Boot Apps with Postgres and Docker
Goal: Build two small Spring Boot apps (Service A and Service B) that run via docker-compose. Service A handles simple auth with Postgres and exposes a client endpoint that calls Service B. Service B processes the input and returns a result. Service A saves a small record about the processed request.
Architecture
• Service A (auth-api): Spring Boot (Web + Security + JPA), connects to Postgres, exposes endpoints for register/login and a protected /process endpoint.
• Service B (data-api): Spring Boot (Web), exposes /transform endpoint. Only accepts requests from Service A via shared header.
• Postgres: stores users and a simple processing log table.
Minimal Functional Scope
1) Auth in Service A
- Register: POST /api/auth/register { email, password } → 201
- Login: POST /api/auth/login { email, password } → 200 { token }
- Use a simple JWT (preferred) or Basic session; password must be hashed (BCrypt).
2) Protected processing in Service A
- Endpoint: POST /api/process { text: string } (Authorization required).
- Service A calls Service B: POST http://data-api:8081/api/transform with header X-Internal-Token=<secret>.
- Service A receives the processed payload and stores a small record in Postgres (e.g., userId, input, output, timestamp). Returns the processed result to the client.
3) Service B behavior
- Endpoint: POST /api/transform { text }.
- Validate X-Internal-Token from env. If missing/invalid → 403.
- Simple transform logic (example): reverse the text, uppercase it, or append a suffix. Return { result }.
  Data Model (Minimum)
  • users: id (UUID), email (unique), password_hash
  • processing_log: id (UUID), user_id, input_text, output_text, created_at
  Docker Requirements
  • One docker-compose.yml that starts:
    - postgres
    - auth-api (expose localhost:8080 → 8080)
    - data-api (expose localhost:8081 → 8081)
      • Both services must run on the same Docker network; Service A must reach Service B at http://data-api:8081
      • Config by env vars: POSTGRES_URL/USER/PASSWORD, JWT_SECRET, INTERNAL_TOKEN
      What to Submit
      • Repo structure:
      /auth-api (Spring Boot project)
      /data-api (Spring Boot project)
      docker-compose.yml
      README.md with run instructions
      (Optional) Flyway migrations or init SQL for users and processing_log
      Run Example
1) Build:
   mvn -f auth-api/pom.xml clean package -DskipTests
   mvn -f data-api/pom.xml clean package -DskipTests
   docker compose up -d --build
2) Register & Login:
   curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{"email":"a@a.com","password":"pass"}"
   curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{"email":"a@a.com","password":"pass"}"
   # Save token from response
3) Process:
   curl -X POST http://localhost:8080/api/process -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d "{"text":"hello"}"
   # Expected: { "result": "transformed text" } and a row in processing_log
Acceptance Criteria
• Register, login, and /process work via localhost with docker-compose up.
• Service B rejects requests without a valid X-Internal-Token.
• Service A stores processing logs in Postgres.
• Clear README with commands to run and test.
Notes
• Keep it simple—no need for advanced patterns. Focus on working auth, basic transform, and Docker setup.
• Passwords must be hashed; do not log secrets or tokens.
