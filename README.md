# LoginChallenge Microservice

A Spring Boot-based user management service with JWT authentication, secure password handling, and RESTful endpoints for user registration and login.

---

## Features

* **User Sign-Up**: Register new users with validation and encrypted passwords.
* **User Login**: Authenticate users with JWT tokens.
* **Secure Passwords**: Passwords are hashed and encrypted with AES/GCM and validated with a `PasswordEncoder`.
* **JWT-Based Authentication**: Stateless authentication using JWT tokens.
* **Exception Handling**: Centralized exception handling via `GlobalAdvisor`.
* **H2 Console Access**: In-memory database console available for testing.
* **Validation**: Request payloads are validated with annotations such as `@NotEmpty`, `@Size`, `@Email`, and `@Pattern` to ensure proper data.

---

## Endpoints

### Sign-Up

**URL**: `/api/user/sign-up`
**Method**: `POST`
**Content-Type**: `application/json`
**Description**: Registers a new user. Password must meet validation rules and fields are validated to enforce proper formatting.

**Request Body Example**:

```json
{
  "name": "solid state",
  "email": "Simaone45@yahoo.com",
  "password": "ab4cdAefg4a",
  "phones": [
    {
      "number": 123123123,
      "cityCode": 123,
      "countryCode": "123"
    }
  ]
}
```

**Validation Rules:**

* `name`: Required, max 36 characters.
* `email`: Required, valid email format, max 255 characters.
* `password`: Required, alphanumeric 8-12 characters. Only lowercase and uppercase characters plus only 2 numbers at any position.
* `phones.number`: Max 50.
* `phones.cityCode`: Max 50.
* `phones.countryCode`: Max 50.

**cURL Example**:

```bash
curl --location 'localhost:8080/api/user/sign-up' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=E1D118EC262BDCC44C3BC1D8C6C1D5BE' \
--data-raw '{
  "name": "solid state",
  "email": "Simaone45@yahoo.com",
  "password": "ab4cdAefg4a",
  "phones": [
    {
      "number": 123123123,
      "cityCode": 123,
      "countryCode": "123"
    }
  ]
}'
```

---

### Login

**URL**: `/api/user/login`
**Method**: `POST`
**Headers**:

* `Authorization: Bearer <JWT_TOKEN>`

**Request Body Example**:

```json
{
  "password": "ab4cdAefg4a"
}
```

**Validation Rules:**

* `password`: Required and not empty.

**cURL Example**:

```bash
curl --location 'localhost:8080/api/user/login' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--header 'Cookie: JSESSIONID=E1D118EC262BDCC44C3BC1D8C6C1D5BE' \
--data '{
  "password": "ab4cdAefg4a"
}'
```

---

## Security

* **Endpoints `/api/user/sign-up` and `/h2-console` are public**.
* **Other endpoints require a valid JWT token**.
* Spring Security handles stateless session management.

---

## Technology Stack

* Java 11 / Spring Boot 2.5.14
* Spring Security
* JWT (io.jsonwebtoken 0.9.1)
* H2 Database (for testing)
* Lombok 1.18.40
* Gradle 7.4 / Gradle wrapper
* Docker & Docker Compose

---

## Build and Run

### Using Gradle Wrapper

1. Clone the repository:

```bash
git clone <repo-url>
cd loginchallenge
```

2. Build the project:

```bash
./gradlew clean build
```

3. Run the application:

```bash
./gradlew bootRun
```

4. Run tests and generate coverage reports:

```bash
./gradlew clean test intTest pitest sonarlintmain sonarlinttest sonarlintinttest jacocoTestReport
```

5. Access the H2 console at: `http://localhost:8080/h2-console`
6. Use the provided cURL examples to test endpoints.

### Using Docker & Docker Compose

1. Build and start containers:

```bash
docker-compose up --build
```

2. The application will be available at `http://localhost:8080`.
3. H2 console will be available at `http://localhost:8082`.
4. Use the same cURL examples to interact with the running container.

---

## Notes

* Passwords are limited to a maximum of 2 numbers; exceeding that will throw a `BadClientException`.
* JWT tokens expire after **1 hour**.
* All exceptions are returned in a structured JSON format with a timestamp, HTTP status code, and message.
* Validations are enforced on all request models to ensure correct and secure data input.

---

## Authors

* José G.C. (Developer)
