# 🎓 English Platform — Frontend

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
src/
├── App.jsx                   # Роутинг + protected routes
├── main.jsx
├── index.css                 # Глобальные стили + Tailwind
├── context/
│   └── AuthContext.jsx       # JWT auth state
├── services/
│   └── api.js                # Axios + все API-методы
├── components/
│   ├── Navbar.jsx
│   └── UI.jsx                # Переиспользуемые компоненты
└── pages/
    ├── LoginPage.jsx
    ├── RegisterPage.jsx
    ├── CoursesPage.jsx
    ├── CourseDetailPage.jsx
    ├── EnrollmentsPage.jsx
    ├── ManagePage.jsx
    └── AdminPage.jsx
```

## Дизайн

Строгая чёрно-белая палитра (`#0a0a0a` фон, `#f0f0f0` текст).
Типографика: **DM Serif Display** (заголовки) + **JetBrains Mono** (данные) + **DM Sans** (текст).
Тонкий шум-оверлей для текстуры. Минималистичные анимации появления.
