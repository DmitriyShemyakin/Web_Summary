# Web Summary

Веб-приложение для **учета аварий на сети**: регистрация и сопровождение инцидентов, привязка к позициям и сменам, справочники, администрирование пользователей и ролей.

## Возможности

- **Аварии** — список, создание, редактирование, закрытие; фильтры и отображение длительности; данные по позиции, типу оборудования, категориям и типам аварий.
- **Смены** — открытие/закрытие смены, статистика, просмотр аварий по смене, выгрузка в **Excel** (Apache POI).
- **Справочники** — типы сети, категории и типы аварий, роли и направления (админ-раздел).
- **Пользователи** — CRUD, смена пароля, назначение ролей и направлений (Spring Security, form login).
- **Позиции** — загрузка из `bs.json` при первом старте; опциональная синхронизация по расписанию или из внешнего URL (см. конфигурацию).

## Стек


| Компонент   | Технология                                               |
| ----------- | -------------------------------------------------------- |
| Runtime     | Java **17**                                              |
| Framework   | **Spring Boot 3.1** (Web, Data JPA, Security, Thymeleaf) |
| База данных | **PostgreSQL**                                           |
| Сборка      | **Maven**                                                |
| UI          | Thymeleaf, **Bootstrap 5**, **Alpine.js** (CDN)          |
| Прочее      | Lombok, Apache POI (XLSX)                                |


> В проекте подключены **JJWT** и свойства `jwt.`* — при необходимости их можно использовать для отдельного REST-аутентификации; текущий вход в систему реализован через **сессию и form login**.

## Требования

- JDK 17+
- PostgreSQL 12+ (или совместимая версия)
- Maven 3.8+

## Быстрый старт

1. **Создайте базу** и пользователя в PostgreSQL, например:
  ```sql
   CREATE DATABASE testdatabase;
   CREATE USER test_user WITH PASSWORD 'test';
   GRANT ALL PRIVILEGES ON DATABASE testdatabaseTO test_user ;
  ```
2. **Настройте подключение** в `src/main/resources/application.properties` (`spring.datasource.`*).
3. **Запуск:**
  ```bash
   mvn spring-boot:run
  ```
4. Откройте в браузере: **[http://localhost:8080](http://localhost:8080)** (порт задается `server.port`).

При первом запуске Hibernate создаст/обновит схему (`spring.jpa.hibernate.ddl-auto=update`), выполнится **инициализация данных** (`DataInitializer`): роли, справочники, тестовые пользователи, импорт сущнойстей из `src/main/resources/bs.json` при пустой таблице позиций (загрузка тестовых данных).

## Учетные записи по умолчанию


| Пользователь | Пароль  | Назначение                   |
| ------------ | ------- | ---------------------------- |
| `admin`      | `admin` | Администратор (роль `admin`) |


Дополнительно при отсутствии в БД создаются тестовые учетки дежурных с паролем по умолчанию `**password`** — см. лог приложения при старте. **В продакшене отключите или смените эти данные.**

## Конфигурация

Файл: `application.properties`.


| Параметр                           | Описание                                                                                                                                                |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `server.port`                      | HTTP-порт (по умолчанию `8080`)                                                                                                                         |
| `server.servlet.session.timeout`   | Таймаут сессии; `-1` — без истечения по простою (см. комментарий в файле)                                                                               |
| `spring.datasource.`*              | JDBC к PostgreSQL                                                                                                                                       |
| `spring.jpa.hibernate.ddl-auto`    | Для прода обычно `validate`                                                                                                                             |
| `url`, `access`                    | Если заданы тогда используются планировщиком для **синхронизации позиций** с внешней системой; Если пусто, тогда только локальный сценарий из `bs.json` |
| `scheduler.cron`, `scheduler.zone` | Расписание фоновой синхронизации позиций (API)                                                                                                          |
| `app.runOnStartup`                 | `true -` один раз запустить синхронизацию после старта приложения                                                                                       |
| `output.path`                      | Путь для сохранения выгрузки базовых станций (JSON)                                                                                                     |
| `jwt.secret`, `jwt.expiration`     | Резерв под JWT (если будет реализован отдельный API-login)                                                                                              |


## Структура репозитория (кратко)

```
src/main/java/com/example/web_summaryy/
├── controller/     # MVC (ViewController) и REST (/api/*)
├── config/         # Security, мапперы DTO, инициализация данных
├── model/          # JPA-сущности
├── repository/     # Spring Data JPA
├── service/        # Бизнес-логика
├── scheduler/      # Планировщик синхронизации позиций
└── security/       # Spring Security

src/main/resources/
├── templates/      # Thymeleaf (incidents, shifts, admin, login)
├── static/         # CSS, JS (в т.ч. incident-duration.js)
└── *.properties, bs.json
```

## Основные URL (UI)


| Путь                     | Назначение         |
| ------------------------ | ------------------ |
| `/login`                 | Вход               |
| `/incidents`             | Список аварий      |
| `/incidents/{id}/edit`   | Карточка аварии    |
| `/shifts`                | Смены              |
| `/shifts/{id}/incidents` | Аварии смены       |
| `/admin/users`           | Пользователи       |
| `/admin/roles`           | Роли и направления |
| `/admin/dictionaries`    | Справочники        |


REST API для тех же разделов доступен под префиксами `/api/incidents`, `/api/shifts`, `/api/users`, `/api/dictionaries` (для страниц админки и списков часто используется `fetch` с `credentials: 'same-origin'`). CSRF для `/api/**` отключен в конфигурации.

## Сборка JAR

```bash
mvn -DskipTests package
java -jar target/Web_summaryy-0.0.1-SNAPSHOT.jar
```

*Web Summary — система учета аварий на сети.*
