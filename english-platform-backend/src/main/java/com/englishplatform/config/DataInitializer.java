package com.englishplatform.config;

import com.englishplatform.entity.*;
import com.englishplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Заполняет БД начальными данными при первом запуске.
 * Пропускает инициализацию, если данные уже существуют.
 *
 * ┌─────────────────────────────────────────────────────┐
 * │             ТЕСТОВЫЕ АККАУНТЫ                       │
 * ├──────────────┬──────────────┬───────────────────────┤
 * │ username     │ password     │ role                  │
 * ├──────────────┼──────────────┼───────────────────────┤
 * │ admin        │ admin123     │ ADMIN                 │
 * │ manager      │ manager123   │ MANAGER               │
 * │ student      │ student123   │ USER                  │
 * │ alice        │ alice123     │ USER                  │
 * └──────────────┴──────────────┴───────────────────────┘
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository       userRepository;
    private final CourseRepository     courseRepository;
    private final LessonRepository     lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder      passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("=== DataInitializer: данные уже существуют, пропускаю ===");
            return;
        }

        log.info("=== DataInitializer: начинаю заполнение базы данных ===");

        // ─── Users ──────────────────────────────────────────────────────────
        User admin = createUser("admin",   "admin123",   "Администратор Системы",  "admin@english.kz",   Role.ADMIN);
        User mgr   = createUser("manager", "manager123", "Менеджер Курсов",         "manager@english.kz", Role.MANAGER);
        User stu1  = createUser("student", "student123", "Нурсултан Бекжанов",      "student@mail.ru",    Role.USER);
        User stu2  = createUser("alice",   "alice123",   "Алия Смагулова",          "alice@mail.ru",      Role.USER);

        userRepository.saveAll(List.of(admin, mgr, stu1, stu2));
        log.info("  ✓ Пользователи созданы: admin / manager / student / alice");

        // ─── Courses ─────────────────────────────────────────────────────────
        Course c1 = createCourse(
                "English for Absolute Beginners",
                "Полный курс с нуля. Алфавит, базовые фразы, приветствия и повседневный словарный запас.",
                Level.BEGINNER, true);

        Course c2 = createCourse(
                "Elementary Grammar Essentials",
                "Артикли, времена Present Simple и Past Simple, базовые предлоги и структура предложения.",
                Level.ELEMENTARY, true);

        Course c3 = createCourse(
                "Business English Intermediate",
                "Деловая переписка, проведение встреч, переговоры и профессиональная лексика.",
                Level.INTERMEDIATE, true);

        Course c4 = createCourse(
                "Upper-Intermediate Speaking Club",
                "Разговорная практика на сложные темы: политика, экономика, культура.",
                Level.UPPER_INTERMEDIATE, true);

        Course c5 = createCourse(
                "Advanced Academic Writing",
                "Эссе, аргументация, академический стиль и работа с источниками.",
                Level.ADVANCED, true);

        Course c6 = createCourse(
                "IELTS Preparation Full Course",
                "Полная подготовка к IELTS: Reading, Writing, Listening, Speaking. Все 4 модуля.",
                Level.UPPER_INTERMEDIATE, true);

        Course c7 = createCourse(
                "Pronunciation & Phonetics",
                "Фонетика, транскрипция, интонация и избавление от акцента.",
                Level.ELEMENTARY, true);

        Course c8 = createCourse(
                "C2 Proficiency Masterclass",
                "Для тех, кто хочет довести английский до уровня носителя. Идиомы, нюансы, стиль.",
                Level.PROFICIENCY, false); // неактивный — для демонстрации

        courseRepository.saveAll(List.of(c1, c2, c3, c4, c5, c6, c7, c8));
        log.info("  ✓ Курсы созданы: {} шт.", 8);

        // ─── Lessons для курса 1: English for Absolute Beginners ─────────────
        lessonRepository.saveAll(List.of(
                createLesson(c1, 1,  "The English Alphabet",              "Изучаем 26 букв, произношение каждой. Аудио-упражнения.", 20, true),
                createLesson(c1, 2,  "Numbers 1–100",                     "Числительные, счёт, возраст и цены.", 25, true),
                createLesson(c1, 3,  "Greetings & Introductions",         "Hello, Hi, Good morning. Как представиться на английском.", 30, true),
                createLesson(c1, 4,  "Colors, Shapes & Sizes",            "Цвета, формы и размеры — базовая лексика.", 25, true),
                createLesson(c1, 5,  "Days, Months & Seasons",            "Дни недели, месяцы, времена года. Как спросить дату.", 30, true),
                createLesson(c1, 6,  "Common Verbs: to be / to have",     "Глаголы to be и to have, утверждения и отрицания.", 40, true),
                createLesson(c1, 7,  "My Family",                         "Члены семьи, притяжательные местоимения my/your/his/her.", 35, true),
                createLesson(c1, 8,  "Food & Drinks",                     "Еда и напитки. Диалог в кафе.", 30, true),
                createLesson(c1, 9,  "Beginner Test & Review",            "Финальный тест: закрепление всех тем курса.", 45, false)
        ));

        // ─── Lessons для курса 2: Elementary Grammar ──────────────────────────
        lessonRepository.saveAll(List.of(
                createLesson(c2, 1, "Articles: A, An, The",              "Определённый и неопределённый артикль. Когда не нужен артикль.", 35, true),
                createLesson(c2, 2, "Present Simple",                    "Образование, утверждение, отрицание и вопрос. Наречия частоты.", 45, true),
                createLesson(c2, 3, "Past Simple",                       "Regular & irregular verbs. Ed-окончания и исключения.", 50, true),
                createLesson(c2, 4, "Future: will & going to",           "Планы и спонтанные решения. Разница между will и going to.", 40, true),
                createLesson(c2, 5, "Prepositions of Time & Place",      "In/on/at для времени и места. Типичные ошибки.", 35, true),
                createLesson(c2, 6, "Countable & Uncountable Nouns",     "Исчисляемые и неисчисляемые существительные. Some/any/much/many.", 40, true),
                createLesson(c2, 7, "Grammar Test",                      "Финальный тест по всем темам.", 50, false)
        ));

        // ─── Lessons для курса 3: Business English ────────────────────────────
        lessonRepository.saveAll(List.of(
                createLesson(c3, 1, "Professional Introductions",        "Как представиться на деловой встрече. Small talk.", 30, true),
                createLesson(c3, 2, "Writing Business Emails",           "Структура делового письма. Формальные фразы и шаблоны.", 50, true),
                createLesson(c3, 3, "Meetings & Presentations",          "Фразы для ведения собраний. Как делать презентацию.", 55, true),
                createLesson(c3, 4, "Negotiations & Agreements",         "Язык переговоров. Компромисс и согласование условий.", 60, true),
                createLesson(c3, 5, "Finance & Business Vocabulary",     "P&L, KPI, ROI — ключевые бизнес-термины на английском.", 45, true),
                createLesson(c3, 6, "Phone Calls & Conference Calls",    "Деловые звонки, голосовые сообщения, международные конференции.", 40, true)
        ));

        // ─── Lessons для курса 6: IELTS ───────────────────────────────────────
        lessonRepository.saveAll(List.of(
                createLesson(c6, 1, "IELTS Overview & Band Scores",     "Структура экзамена, критерии оценки и стратегии.", 30, true),
                createLesson(c6, 2, "Reading: Skimming & Scanning",     "Техники быстрого чтения. True/False/Not Given.", 60, true),
                createLesson(c6, 3, "Writing Task 1: Graphs & Charts",  "Как описывать графики, таблицы и диаграммы.", 60, true),
                createLesson(c6, 4, "Writing Task 2: Opinion Essays",   "Структура эссе. Аргументация и примеры.", 75, true),
                createLesson(c6, 5, "Listening: Multiple Choice",       "Стратегии для секции аудирования.", 50, true),
                createLesson(c6, 6, "Speaking: Part 1, 2, 3",          "Как отвечать на каждой части. Типичные темы.", 60, true),
                createLesson(c6, 7, "Mock Test + Feedback",             "Полный пробный экзамен с разбором ошибок.", 120, false)
        ));

        // ─── Lessons для курса 7: Pronunciation ───────────────────────────────
        lessonRepository.saveAll(List.of(
                createLesson(c7, 1, "IPA: International Phonetic Alphabet", "Все символы транскрипции. Как читать словарь.", 40, true),
                createLesson(c7, 2, "Vowel Sounds",                         "12 английских гласных звуков. Минимальные пары.", 45, true),
                createLesson(c7, 3, "Consonant Sounds",                     "Th, W, V, R — самые сложные для русскоязычных.", 40, true),
                createLesson(c7, 4, "Word Stress & Sentence Rhythm",        "Ударение в словах и ритм английского предложения.", 35, true),
                createLesson(c7, 5, "Intonation Patterns",                  "Восходящая и нисходящая интонация. Вопросы и утверждения.", 40, true)
        ));

        log.info("  ✓ Уроки созданы");

        // ─── Enrollments ──────────────────────────────────────────────────────
        // student: записан на 3 курса с разным прогрессом
        Enrollment e1 = createEnrollment(stu1, c1, EnrollmentStatus.COMPLETED, 100);
        e1.setCompletedAt(LocalDateTime.now().minusDays(10));

        Enrollment e2 = createEnrollment(stu1, c2, EnrollmentStatus.ACTIVE, 60);
        Enrollment e3 = createEnrollment(stu1, c6, EnrollmentStatus.ACTIVE, 15);

        // alice: записана на 2 курса
        Enrollment e4 = createEnrollment(stu2, c1, EnrollmentStatus.ACTIVE, 40);
        Enrollment e5 = createEnrollment(stu2, c3, EnrollmentStatus.ACTIVE, 0);

        // manager тоже учится
        Enrollment e6 = createEnrollment(mgr, c5, EnrollmentStatus.ACTIVE, 25);

        enrollmentRepository.saveAll(List.of(e1, e2, e3, e4, e5, e6));
        log.info("  ✓ Записи на курсы созданы");

        log.info("=========================================================");
        log.info("  DataInitializer завершён успешно!");
        log.info("  Тестовые аккаунты:");
        log.info("    admin    / admin123    → ADMIN");
        log.info("    manager  / manager123  → MANAGER");
        log.info("    student  / student123  → USER  (есть прогресс)");
        log.info("    alice    / alice123    → USER");
        log.info("=========================================================");
    }

    // ─── Вспомогательные методы ────────────────────────────────────────────

    private User createUser(String username, String rawPassword, String fullName, String email, Role role) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .email(email)
                .role(role)
                .enabled(true)
                .build();
    }

    private Course createCourse(String title, String description, Level level, boolean active) {
        return Course.builder()
                .title(title)
                .description(description)
                .level(level)
                .active(active)
                .build();
    }

    private Lesson createLesson(Course course, int order, String title, String content,
                                int durationMinutes, boolean published) {
        return Lesson.builder()
                .course(course)
                .orderIndex(order)
                .title(title)
                .content(content)
                .durationMinutes(durationMinutes)
                .published(published)
                .build();
    }

    private Enrollment createEnrollment(User user, Course course, EnrollmentStatus status, int progress) {
        return Enrollment.builder()
                .user(user)
                .course(course)
                .status(status)
                .progressPercent(progress)
                .build();
    }
}
