# 🎓 English Learning Platform API

> **Author:** Tulkiev Alen

A REST API platform for learning English, built with **Spring Boot 3**, **Java 21**, JWT authentication, and a layered Controller–Service–Repository architecture.

---

## 📋 Project Description

The platform provides a REST API for managing online English courses:

- **Users** can browse and enroll in courses, and track their progress.
- **Managers** can manage courses and lessons.
- **Administrators** control users and all platform content.

---

## ⚙️ Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.2.3 |
| Spring Security | 6.x |
| MySQL | 8.x |
| JWT (jjwt) | 0.12.5 |
| Lombok | latest |
| JUnit 5 / Mockito | latest |

---

## 🗄️ Database Schema

```
users
├── id (PK, AUTO_INCREMENT)
├── username (UNIQUE, NOT NULL)
├── email    (UNIQUE, NOT NULL)
├── password (NOT NULL)
├── full_name
├── role     (ADMIN | MANAGER | USER)
├── enabled
├── created_at
└── updated_at

courses
├── id (PK, AUTO_INCREMENT)
├── title       (UNIQUE, NOT NULL)
├── description
├── level       (BEGINNER | ELEMENTARY | INTERMEDIATE | UPPER_INTERMEDIATE | ADVANCED | PROFICIENCY)
├── active
├── created_at
└── updated_at

lessons
├── id (PK, AUTO_INCREMENT)
├── title
├── content
├── order_index
├── duration_minutes
├── published
├── course_id (FK → courses.id)
├── created_at
└── updated_at

enrollments
├── id (PK, AUTO_INCREMENT)
├── user_id         (FK → users.id)
├── course_id       (FK → courses.id)
├── status          (ACTIVE | COMPLETED | SUSPENDED | CANCELLED)
├── progress_percent
├── enrolled_at
└── completed_at
```

---

## 🔐 Roles & Access

| Endpoint | USER | MANAGER | ADMIN |
|---|:---:|:---:|:---:|
| POST `/api/auth/**` | ✅ | ✅ | ✅ |
| GET `/api/courses/**` | ✅ | ✅ | ✅ |
| GET `/api/lessons/**` | ✅ | ✅ | ✅ |
| POST/PUT `/api/courses/**` | ❌ | ✅ | ✅ |
| DELETE `/api/courses/**` | ❌ | ❌ | ✅ |
| POST/PUT/DELETE `/api/lessons/**` | ❌ | ✅ | ✅ |
| POST/GET `/api/enrollments/my` | ✅ | ✅ | ✅ |
| GET `/api/enrollments` (all) | ❌ | ✅ | ✅ |
| GET/PUT `/api/admin/users/**` | ❌ | ❌ | ✅ |

---

## 📡 API Endpoints (25+)

### 🔑 Auth — `/api/auth`

| Method | URL | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |

### 👤 Users — `/api`

| Method | URL | Description | Access |
|---|---|---|---|
| GET | `/api/users/me` | Get current profile | USER+ |
| GET | `/api/admin/users` | Get all users | ADMIN |
| GET | `/api/admin/users/{id}` | Get user by ID | ADMIN |
| GET | `/api/admin/users/role/{role}` | Filter users by role | ADMIN |
| PUT | `/api/admin/users/{id}` | Update user | ADMIN |
| DELETE | `/api/admin/users/{id}` | Delete user | ADMIN |

### 📚 Courses — `/api/courses`

| Method | URL | Description | Access |
|---|---|---|---|
| GET | `/api/courses` | Get all active courses | Public |
| GET | `/api/courses/all` | Get all courses (including inactive) | MANAGER+ |
| GET | `/api/courses/{id}` | Get course by ID | Public |
| GET | `/api/courses/level/{level}` | Get courses by level | Public |
| POST | `/api/courses` | Create a course | MANAGER+ |
| PUT | `/api/courses/{id}` | Update a course | MANAGER+ |
| DELETE | `/api/courses/{id}` | Delete a course | ADMIN |
| PATCH | `/api/courses/{id}/toggle` | Toggle course activity | MANAGER+ |

### 📖 Lessons — `/api/lessons`

| Method | URL | Description | Access |
|---|---|---|---|
| GET | `/api/lessons/course/{courseId}` | Get published lessons | Public |
| GET | `/api/lessons/course/{courseId}/all` | Get all lessons | MANAGER+ |
| GET | `/api/lessons/{id}` | Get lesson by ID | Public |
| POST | `/api/lessons` | Create a lesson | MANAGER+ |
| PUT | `/api/lessons/{id}` | Update a lesson | MANAGER+ |
| DELETE | `/api/lessons/{id}` | Delete a lesson | MANAGER+ |
| PATCH | `/api/lessons/{id}/toggle-publish` | Publish / unpublish a lesson | MANAGER+ |

### 🎯 Enrollments — `/api/enrollments`

| Method | URL | Description | Access |
|---|---|---|---|
| POST | `/api/enrollments` | Enroll in a course | USER+ |
| GET | `/api/enrollments/my` | Get my enrollments | USER+ |
| GET | `/api/enrollments` | Get all enrollments | MANAGER+ |
| GET | `/api/enrollments/{id}` | Get enrollment by ID | MANAGER+ |
| GET | `/api/enrollments/course/{courseId}` | Get enrollments by course | MANAGER+ |
| PATCH | `/api/enrollments/{id}/progress` | Update progress | USER+ |
| PATCH | `/api/enrollments/{id}/cancel` | Cancel enrollment | USER+ |
| PATCH | `/api/enrollments/{id}/status` | Update status (admin) | MANAGER+ |

---

## 🚀 Getting Started

### Requirements

- Java 21 (Amazon Corretto / Eclipse Temurin)
- Maven 3.9+
- MySQL 8.x
- IntelliJ IDEA 2024+

### 1. Clone the repository

```bash
git clone https://github.com/your-org/english-learning-platform.git
cd english-learning-platform
```

### 2. Set up MySQL

```sql
CREATE DATABASE english_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'platform_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON english_platform.* TO 'platform_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/english_platform?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=platform_user
spring.datasource.password=your_password
```

### 4. Build & Run

**IntelliJ IDEA:**
1. `File → Open` → select the project folder
2. Wait for Maven dependencies to load
3. Open `EnglishLearningPlatformApplication.java`
4. Click ▶️ Run

**Command line:**
```bash
mvn clean install
mvn spring-boot:run
```

Server will run at: **http://localhost:8080**

### 5. Run Tests

```bash
mvn test
```

---

## 📦 Project Structure

```
src/
├── main/java/com/englishplatform/
│   ├── EnglishLearningPlatformApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── CourseController.java
│   │   ├── EnrollmentController.java
│   │   ├── LessonController.java
│   │   └── UserController.java
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   ├── security/
│   └── service/
```

---

## 🧪 Request Examples

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "secret123",
  "fullName": "Alice Smith"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "secret123"
}
```

### Create a Course

```http
POST /api/courses
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Business English",
  "description": "English for professionals",
  "level": "INTERMEDIATE",
  "active": true
}
```

### Enroll in a Course

```http
POST /api/enrollments
Authorization: Bearer <token>
Content-Type: application/json

{
  "courseId": 1
}
```

---

## 🔒 JWT Authentication

All protected endpoints require the following header:

```
Authorization: Bearer <your_jwt_token>
```

The token is returned after login or registration. Default lifetime — **24 hours** (configurable).

---

## 📬 Postman Collection

File: `English_Learning_Platform.postman_collection.json`

**Steps:**
1. Open Postman
2. Import the file
3. Set `base_url = http://localhost:8080`
4. Save the token after login

---

## ⚙️ Configuration

| Parameter | Default | Description |
|---|---|---|
| `server.port` | `8080` | Server port |
| `spring.datasource.url` | `localhost:3306/english_...` | Database URL |
| `jwt.secret` | *(see properties)* | JWT secret key |
| `jwt.expiration` | `86400000` | Token lifetime (ms) |
| `spring.jpa.hibernate.ddl-auto` | `update` | DDL strategy |