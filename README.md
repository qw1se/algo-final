# 🎓 English Platform — Frontend

video  https://youtu.be/9K0yG8yNazU

presentation  https://drive.google.com/file/d/1wfUHZe4RfuOfgBv-QqYBmID1oCiEs2HK/view?usp=sharing

Made by Tulkiev Alen 



screenshots
<img width="1434" height="844" alt="image" src="https://github.com/user-attachments/assets/47c5fe5b-baf8-46e2-89cc-302c57ba38a1" />

<img width="1407" height="867" alt="image" src="https://github.com/user-attachments/assets/b79d85a9-69be-4a5b-9c36-d046aea46516" />

<img width="870" height="745" alt="Снимок экрана 2026-04-27 185020" src="https://github.com/user-attachments/assets/26a2244d-e8a8-4774-acd8-d69e1b45e081" />

<img width="1909" height="897" alt="Снимок экрана 2026-04-27 194734" src="https://github.com/user-attachments/assets/b4dd00b5-779c-4857-9e16-26722790646b" />





React SPA для визуального тестирования English Learning Platform API.

## Стек

| Технология       | Версия  |
|-----------------|---------|
| React           | 18.x    |
| React Router    | 6.x     |
| Vite            | 5.x     |
| Tailwind CSS    | 3.x     |
| Axios           | 1.x     |
| lucide-react    | 0.383   |

## Страницы и доступ

| Страница         | URL             | Роли              |
|-----------------|-----------------|-------------------|
| Каталог курсов  | `/courses`      | Публичный         |
| Детали курса    | `/courses/:id`  | Публичный         |
| Мои курсы       | `/enrollments`  | USER+             |
| Управление      | `/manage`       | MANAGER, ADMIN    |
| Администрация   | `/admin`        | ADMIN             |

## Запуск

### Требования
- **Node.js 18+**
- **npm 9+**
- Запущенный backend на `http://localhost:8080`

### Установка и запуск

```bash
cd english-platform-frontend
npm install
npm run dev
```

Откроется на `http://localhost:3000`

Vite автоматически проксирует `/api/*` → `http://localhost:8080/api/*`

### Сборка для продакшена

```bash
npm run build
npm run preview
```

## Функциональность

### Для USER
- Просмотр каталога курсов с фильтрацией по уровню
- Просмотр деталей курса и списка уроков
- Запись на курс одной кнопкой
- Отслеживание прогресса через слайдер (0–100%)
- Отмена записи на курс

### Для MANAGER / ADMIN
- Всё что доступно USER
- Создание / редактирование / удаление курсов
- Управление уроками внутри курса (порядок, контент, длительность)
- Публикация / снятие с публикации уроков
- Включение / отключение активности курса

### Для ADMIN
- Всё что доступно MANAGER
- Таблица всех пользователей
- Изменение роли и статуса пользователей
- Удаление пользователей
- Управление статусами всех записей

## Структура

```
├── 📁 english-platform-backend/
│   │
│   ├── 📄 pom.xml
│   ├── 📄 README.md
│   │
│   └── 📁 src/
│       │
│       ├── 📁 main/
│       │   ├── 📁 java/
│       │   │   └── 📁 com/
│       │   │       └── 📁 englishplatform/
│       │   │           │
│       │   │           ├── 📄 EnglishLearningPlatformApplication.java
│       │   │           │
│       │   │           ├── 📁 config/
│       │   │           │   ├── 📄 DataInitializer.java
│       │   │           │   └── 📄 SecurityConfig.java
│       │   │           │
│       │   │           ├── 📁 controller/
│       │   │           │   ├── 📄 AuthController.java
│       │   │           │   ├── 📄 CourseController.java
│       │   │           │   ├── 📄 EnrollmentController.java
│       │   │           │   ├── 📄 LessonController.java
│       │   │           │   └── 📄 UserController.java
│       │   │           │
│       │   │           ├── 📁 dto/
│       │   │           │   ├── 📁 request/
│       │   │           │   └── 📁 response/
│       │   │           │
│       │   │           ├── 📁 entity/
│       │   │           │   ├── 📄 Course.java
│       │   │           │   ├── 📄 Enrollment.java
│       │   │           │   ├── 📄 EnrollmentStatus.java (enum)
│       │   │           │   ├── 📄 Lesson.java
│       │   │           │   ├── 📄 Level.java (enum)
│       │   │           │   ├── 📄 Role.java (enum)
│       │   │           │   └── 📄 User.java
│       │   │           │
│       │   │           ├── 📁 exception/
│       │   │           │   ├── 📄 AccessDeniedException.java
│       │   │           │   ├── 📄 DuplicateResourceException.java
│       │   │           │   ├── 📄 GlobalExceptionHandler.java
│       │   │           │   └── 📄 ResourceNotFoundException.java
│       │   │           │
│       │   │           ├── 📁 repository/
│       │   │           │   ├── 📄 CourseRepository.java
│       │   │           │   ├── 📄 EnrollmentRepository.java
│       │   │           │   ├── 📄 LessonRepository.java
│       │   │           │   └── 📄 UserRepository.java
│       │   │           │
│       │   │           ├── 📁 security/
│       │   │           │   ├── 📄 JwtAuthenticationFilter.java
│       │   │           │   └── 📄 JwtUtils.java
│       │   │           │
│       │   │           └── 📁 service/
│       │   │               ├── 📄 AuthService.java
│       │   │               ├── 📄 CourseService.java
│       │   │               ├── 📄 EnrollmentService.java
│       │   │               ├── 📄 LessonService.java
│       │   │               └── 📁 impl/
│       │   │                   ├── 📄 AuthServiceImpl.java
│       │   │                   ├── 📄 CourseServiceImpl.java
│       │   │                   ├── 📄 EnrollmentServiceImpl.java
│       │   │                   └── 📄 LessonServiceImpl.java
│       │   │
│       │   └── 📁 resources/
│       │       └── 📄 application.properties
│       │
│       └── 📁 test/
│           ├── 📁 java/
│           │   └── 📁 com/
│           │       └── 📁 englishplatform/
│           │           ├── 📄 EnglishLearningPlatformApplicationTest.java
│           │           └── 📁 service/
│           │
│           └── 📁 resources/
│               └── 📄 application-test.properties
│
├── 📁 english-platform-frontend/
│   │
│   ├── 📄 index.html
│   ├── 📄 package.json
│   ├── 📄 postcss.config.js
│   ├── 📄 README.md
│   ├── 📄 tailwind.config.js
│   ├── 📄 vite.config.js
│   │
│   └── 📁 src/
│       ├── 📄 App.jsx
│       ├── 📄 index.css
│       ├── 📄 main.jsx
│       │
│       ├── 📁 components/
│       │   ├── 📄 Navbar.jsx
│       │   └── 📄 UI.jsx
│       │
│       ├── 📁 context/
│       │   └── 📄 AuthContext.jsx
│       │
│       ├── 📁 pages/
│       │   ├── 📄 AdminPage.jsx
│       │   ├── 📄 CourseDetailPage.jsx
│       │   ├── 📄 CoursesPage.jsx
│       │   ├── 📄 EnrollmentsPage.jsx
│       │   ├── 📄 LoginPage.jsx
│       │   ├── 📄 ManagePage.jsx
│       │   └── 📄 RegisterPage.jsx
│       │
│       └── 📁 services/
```

## Дизайн

Строгая чёрно-белая палитра (`#0a0a0a` фон, `#f0f0f0` текст).
Типографика: **DM Serif Display** (заголовки) + **JetBrains Mono** (данные) + **DM Sans** (текст).
Тонкий шум-оверлей для текстуры. Минималистичные анимации появления.📦 english-platform/

│           └── 📄 api.js
│
└── 📄 learning_platform_english.postman_collection.json
