# FreightFox – Equation Solver API

A RESTful backend application built using Java and Spring Boot to store, retrieve, and evaluate algebraic equations. The application converts mathematical expressions into postfix notation, builds an expression tree, and evaluates expressions using variable substitution.

## 1. Project Overview

The Equation Solver API allows users to:

- Store algebraic equations.
- Convert infix expressions into postfix (Reverse Polish Notation).
- Construct an expression tree from postfix notation.
- Retrieve stored equations by reconstructing their infix representation from the expression tree.
- Evaluate equations by substituting variable values.
- Handle invalid expressions, missing variables, and mathematical errors.

The application uses **in-memory storage**, so no external database is required.

## 2. Technologies Used

- **Programming Language:** Java
- **Framework:** Spring Boot 4.1.2
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **API Documentation:** Swagger / OpenAPI
- **Validation:** Jakarta Bean Validation
- **Data Storage:** ConcurrentHashMap (In-memory)
- **JSON Processing:** Jackson

## 3. Project Architecture

The application follows a layered architecture to maintain separation of concerns.

```text
src/main/java/com/freightfox/equationsolver
│
├── controller
│   └── EquationController.java
│
├── service
│   ├── EquationService.java
│   └── ExpressionEvaluator.java
│
├── repository
│   └── EquationRepository.java
│
├── model
│   ├── Equation.java
│   └── ExpressionNode.java
│
├── dto
│   ├── CreateEquationRequest.java
│   ├── StoreEquationResponse.java
│   ├── EquationResponse.java
│   ├── EquationListResponse.java
│   ├── EvaluateEquationRequest.java
│   └── EvaluateEquationResponse.java
    └── ApiErrorResponse.java
│
├── parser
│   ├── Tokenizer.java
│   ├── PostfixConverter.java
│   ├── ExpressionTreeBuilder.java
│   └── ExpressionTreeFormatter.java
│
└── exception
    ├── EquationNotFoundException.java
    └── GlobalExceptionHandler.java
```

### Architecture Responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and responses |
| Service | Implements equation storage, retrieval, and evaluation logic |
| Repository | Manages in-memory equation storage |
| Model | Represents equations and expression tree nodes |
| DTO | Defines API request and response structures |
| Parser | Tokenizes expressions, converts them into postfix notation, builds and reconstructs expression trees |
| Exception | Handles application errors and returns appropriate HTTP responses |

## 4. Expression Processing

The application uses an expression tree to represent algebraic equations.

The processing flow is:

1. Tokenize the input expression.
2. Convert the infix expression into postfix notation using the Shunting Yard algorithm.
3. Build an expression tree from the postfix expression.
4. Store the expression tree in the repository.
5. Traverse the expression tree to reconstruct the infix representation when retrieving an equation.
6. Evaluate the expression tree recursively when variable values are provided.

### Example

Input expression:

```text
3x + 2y - z
```

Postfix representation:

```text
3 x * 2 y * + z -
```

Expression tree structure:

```text
          -
         / \
        +   z
       / \
      *   *
     / \ / \
    3  x 2  y
```

For variable values:

```text
x = 2
y = 3
z = 1
```

Calculation:

```text
(3 * 2) + (2 * 3) - 1
= 6 + 6 - 1
= 11
```

Result:

```text
11
```

## 5. REST API Endpoints

Base URL:

```text
http://localhost:8080
```

### 1. Store an Equation

**Endpoint:**

```http
POST /api/equations/store
```

Request body:

```json
{
  "equation": "3x + 2y - z"
}
```

Response:

```json
{
  "message": "Equation stored successfully",
  "equationId": "generated-uuid"
}
```

HTTP Status: `201 Created`

### 2. Retrieve All Equations

**Endpoint:**

```http
GET /api/equations
```

Response:

```json
{
  "equations": [
    {
      "equationId": "generated-uuid",
      "equation": "(((3 * x) + (2 * y)) - z)"
    }
  ]
}
```

The equation is reconstructed by traversing the stored expression tree.

HTTP Status: `200 OK`

### 3. Retrieve an Equation by ID

**Endpoint:**

```http
GET /api/equations/{equationId}
```

Response:

```json
{
  "equationId": "generated-uuid",
  "equation": "(x + 5)"
}
```

HTTP Status: `200 OK`

### 4. Evaluate an Equation

**Endpoint:**

```http
POST /api/equations/{equationId}/evaluate
```

Request body:

```json
{
  "variables": {
    "x": 2,
    "y": 3,
    "z": 1
  }
}
```

Response:

```json
{
  "equationId": "generated-uuid",
  "equation": "(((3 * x) + (2 * y)) - z)",
  "variables": {
    "x": 2.0,
    "y": 3.0,
    "z": 1.0
  },
  "result": 11.0
}
```

HTTP Status: `200 OK`

## 6. Error Handling

The application handles invalid input and evaluation errors.

Examples include:

- Empty or blank equations.
- Invalid mathematical characters.
- Malformed expressions.
- Mismatched parentheses.
- Missing variable values.
- Invalid variable values.
- Division by zero.
- Equation ID not found.

Errors are handled through a centralized exception handler and returned with appropriate HTTP status codes.

## 7. Running the Application

### Prerequisites

- Java Development Kit (JDK 21 or later).
- Maven Wrapper (included in the project).
- IntelliJ IDEA or another Java IDE (optional).

### Clone the Repository

```bash
git clone <your-github-repository-url>
```

Navigate into the project directory:

```bash
cd equation-solver
```

### Run the Application

On Windows:

```cmd
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

## 8. API Documentation – Swagger

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

Use Swagger UI to explore and test the REST API endpoints directly from the browser.

## 9. Running Unit and Integration Tests

The project includes unit tests and integration tests covering the main application components.

Run all tests on Windows:

```cmd
.\mvnw.cmd clean test
```

Run all tests on Linux or macOS:

```bash
./mvnw clean test
```

### Test Coverage Areas

- Tokenizer tests.
- Postfix conversion tests.
- Expression tree builder tests.
- Repository tests.
- Equation service tests.
- Expression evaluator tests.
- REST controller integration tests.

**Latest test execution:**

```text
Tests run: 56
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

The REST API endpoints have also been manually verified using Swagger UI.

## 10. Storage

The application uses an in-memory `ConcurrentHashMap` to store equations.

Each stored equation contains:

- Unique equation ID (UUID).
- Original equation.
- Expression tree.
- Creation timestamp.

Since the application uses in-memory storage, stored equations are not persisted after the application is restarted.

## 11. Future Improvements

- Persistent storage using a relational database.
- Additional mathematical functions.
- More comprehensive expression validation.
- Performance optimizations for complex expressions.

---

**Developed as part of the FreightFox Backend Assignment.**