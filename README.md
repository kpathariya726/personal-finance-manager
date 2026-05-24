# Personal Finance Manager Backend

A complete, production-grade Spring Boot 3.x backend application for personal finance management. The system is designed under robust architecture patterns featuring dynamic data isolation, custom savings goals calculations, category usage validation, global HTTP 4xx error maps, and secure session-cookie based authentication.

This backend successfully compiles and passes **100% of the 86 E2E E2E E2E tests** in the verification suite.

---

## Technical Features

### 1. Security & Authentication
- **Session-Based Security**: Built natively on top of Spring Security 6 without any JWT complexities. Enforces stateful, cookie-based session management.
- **REST Auth Flows**: Handled programmatically via `POST /api/auth/register` and `POST /api/auth/login`. Saves context to `HttpSessionSecurityContextRepository`, immediately issuing standard `JSESSIONID` cookies to clients.
- **Authentication Handlers**: Custom Spring Security entry points translate unauthenticated REST API calls into standard `401 Unauthorized` JSON bodies.

### 2. Category Rules & Default Categories
- **Pre-seeded Categories**: Automatically populates global defaults on application boot:
  - **INCOME**: `Salary`
  - **EXPENSE**: `Food`, `Rent`, `Transportation`, `Entertainment`, `Healthcare`, `Utilities`
- **Protection Logic**: 
  - Prevents the deletion of default global categories.
  - Custom category names are unique per user.
  - Prevents deleting custom categories that are actively linked to user transactions (returns `409 Conflict`).

### 3. Transaction Rules
- **Creation Validations**: Strictly validates amounts (must be `> 0`) and dates (must not be in the future).
- **Date Protection**: Implements write-once dates. Transaction dates are strictly immutable; incoming dates in update `PUT` payloads are safely ignored without blocking updates to description or amount.
- **Filtering & Sorting**: 
  - Retrieves transactions sorted newest first (`date` descending, then `id` descending).
  - Supports combined optional filters: `startDate`, `endDate`, and `category`.

### 4. Savings Goals Calculations
- **Calculations Formula**:
  - `currentProgress` = `Total Income - Total Expenses` for all transactions of that user where `date >= goal.startDate`.
  - Automatically filters out deleted or other users' transactions.
  - `progressPercentage` = `(currentProgress / targetAmount) * 100`.
  - `remainingAmount` = `targetAmount - currentProgress`.
- **Dynamic Formats**: Formats progress dynamically using double values to accurately match JSON requirements (e.g., scale `0` when progress is `0`, and scale `2` otherwise).

### 5. Consolidated Reports
- **Breakdown aggregation**:
  - `GET /api/reports/monthly/{year}/{month}`
  - `GET /api/reports/yearly/{year}`
  - Automatically isolates data by authenticated user, excluding deleted entries and grouping totals by category with standard 2 decimal places.

---

## Local Compilation & Build Instructions

### Prerequisites
- **Java 17**
- **Maven** (version 3.6 or later)

### Compile and Build
To compile the source code and run unit tests:
```bash
mvn clean test
```

### Start Server
To start the application locally on `port 8080` with the `/api` context path:
```bash
mvn spring-boot:run
```
The server will bind to `http://localhost:8080/api` and expose the H2 console at `http://localhost:8080/api/h2-console` (credentials: Username: `sa`, Password: ``, JDBC URL: `jdbc:h2:mem:financedb`).

---

## Verification Results
Our backend passes **86/86 assertions** from the E2E verification bash test suite:
```
Base URL: http://localhost:8080/api
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%

🎉 ALL TESTS PASSED! 🎉
The Personal Finance Manager API is working correctly.
```

---

## Render Deployment Instructions

To deploy this production-grade Maven Spring Boot 3.x project to **Render** as a web service:

### Step 1: Create a PostgreSQL Database (Optional)
If you wish to scale beyond H2 in-memory:
1. In the Render Dashboard, click **New** and select **PostgreSQL**.
2. Name the database (e.g. `finance-db`) and create it.
3. Save the **Internal Database URL** or **External Database URL**.

### Step 2: Configure a Web Service on Render
1. In the Render Dashboard, click **New** and select **Web Service**.
2. Connect your Git repository.
3. Configure the following web service settings:
   - **Name**: `personal-finance-manager`
   - **Environment**: `Docker` or `Java` (Select **Java** for native Spring Boot hosting).
   - **Region**: Select your preferred region (e.g., `Oregon (US West)`).
   - **Branch**: `main` (or your active branch).
   - **Runtime**: `Java`
   - **Build Command**:
     ```bash
     mvn clean package -DskipTests
     ```
   - **Start Command**:
     ```bash
     java -jar target/finance-manager-0.0.1-SNAPSHOT.jar
     ```
   - **Plan**: `Free` or `Individual`

### Step 3: Configure Environment Variables
In the Render Web Service settings, go to the **Environment** tab and add the following variables:
- `PORT`: `8080`
- `SPRING_PROFILES_ACTIVE`: `prod`
- If using Render PostgreSQL, configure:
  - `SPRING_DATASOURCE_URL`: `<Your_Render_PostgreSQL_Connection_String>`
  - `SPRING_DATASOURCE_USERNAME`: `<Your_Render_PostgreSQL_Username>`
  - `SPRING_DATASOURCE_PASSWORD`: `<Your_Render_PostgreSQL_Password>`

Render will automatically fetch the code, run the Maven build, and launch the Spring Boot jar file. Once running, your backend will be live on your custom Render subdomain (`https://<app-name>.onrender.com/api`).
