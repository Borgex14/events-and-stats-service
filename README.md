# Events and Stats Service 🎭
#### Микросервисное приложение для управления событиями, пользователями и статистикой. Платформа позволяет создавать мероприятия, управлять участием, собирать аналитику и предоставлять персонализированные подборки событий.
## 📋 Содержание
### Функциональность
### Технологии
### Архитектура
### API Документация
### Установка и запуск
### База данных
## 🚀 Функциональность
### 👥 Управление пользователями
Регистрация и управление профилями
Ролевая модель (пользователь, администратор)
Поиск и фильтрация пользователей
### 🎪 Управление событиями
Создание и редактирование мероприятий
Модерация событий администраторами
Публикация и отмена событий
Расширенный поиск с фильтрацией по категориям, датам, местоположению
Система статусов (PENDING, PUBLISHED, CANCELED)
### 📊 Статистика и аналитика
Сбор статистики посещений эндпоинтов
Анализ популярности событий
Учет уникальных посетителей
Гибкая фильтрация данных по времени и URI
### 💬 Система комментариев
Добавление комментариев к событиям
Модерация контента администраторами
Поиск и фильтрация комментариев
### 📑 Подборки событий
Создание тематических подборок
Закрепление популярных подборок
Публичный доступ к подборкам
### 🏷️ Категории событий
Иерархическая система категорий
Гибкое управление администраторами
Публичный каталог категорий
### 📝 Запросы на участие
Подача заявок на участие в событиях
Управление заявками организаторами
Автоматическое подтверждение/отклонение
Система лимитов участников
## 🛠️ Технологии
### Backend
Java 17 - основной язык программирования
Spring Boot 3 - фреймворк для создания приложения
Spring MVC - веб-слой приложения
Spring Data JPA - работа с базой данных
Hibernate - ORM для работы с PostgreSQL
### База данных
PostgreSQL - основная реляционная БД
### Валидация и безопасность
Jakarta Validation - валидация входных данных
Spring Security - аутентификация и авторизация (при необходимости)
### Документация
OpenAPI 3.0 - спецификация API
Swagger UI - интерактивная документация
### Тестирование
JUnit 5 - модульное тестирование
Testcontainers - интеграционное тестирование
Mockito - мокирование зависимостей
## 🏗️ Архитектура
### Микросервисная архитектура
#### graph LR
#### A[Main Service] <--> B[Stats Service]
    subgraph A [Main Service]
        A1[Users]
        A2[Events]
        A3[Categories]
        A4[Requests]
        A5[Compilations]
        A6[Comments]
    end
    subgraph B [Stats Service]
        B1[stats-client]
        B2[Stats-dto]
        B3[Stats-server]
    end
#### Компоненты:
Main Service - основной сервис с бизнес-логикой
Stats Service - сервис статистики и аналитики
Двусторонняя связь между сервисами через REST/gRPC
#### Main Service модули:
Users - управление пользователями
Events - работа с событиями
Categories - категории событий
Requests - система запросов
Compilations - подборки событий
Comments - система комментариев
#### Stats Service модули:
stats-client - клиент для внешних вызовов
Stats-dto - объекты передачи данных
Stats-server - API для сбора статистики
### Слои приложения
Controller - обработка HTTP запросов
Service - бизнес-логика
Repository - доступ к данным
DTO - передача данных между слоями
Model - сущности предметной области
## 📖 API Документация
### Базовый URL
http://localhost:8080
### Основные группы эндпоинтов
#### 📊 Статистика
POST /hit - Зарегистрировать обращение к эндпоинту
GET /stats - Получить статистику по обращениям
#### 👥 Пользователи (Admin)
GET /admin/users - Получить список пользователей
POST /admin/users - Создать нового пользователя
DELETE /admin/users/{userId} - Удалить пользователя
#### 🎪 События
Публичные:
GET /events - Поиск событий с фильтрацией
GET /events/{id} - Получить событие по ID
Приватные:
GET /users/{userId}/events - События пользователя
POST /users/{userId}/events - Создать событие
PATCH /users/{userId}/events/{eventId} - Обновить событие
Административные:
GET /admin/events - Поиск событий (админ)
PATCH /admin/events/{eventId} - Редактирование события
#### 📑 Подборки событий
Публичные:
GET /compilations - Получить подборки
GET /compilations/{compId} - Подборка по ID
Административные:
POST /admin/compilations - Создать подборку
DELETE /admin/compilations/{compId} - Удалить подборку
PATCH /admin/compilations/{compId} - Обновить подборку
#### 🏷️ Категории
Публичные:
GET /categories - Получить категории
GET /categories/{catId} - Категория по ID
Административные:
POST /admin/categories - Создать категорию
DELETE /admin/categories/{catId} - Удалить категорию
PATCH /admin/categories/{catId} - Обновить категорию
#### 📝 Запросы на участие
GET /users/{userId}/requests - Запросы пользователя
POST /users/{userId}/requests - Создать запрос
PATCH /users/{userId}/requests/{requestId}/cancel - Отменить запрос
#### 💬 Комментарии
GET /comments/events/{eventId} - Комментарии события
POST /users/{userId}/events/{eventId}/comments - Создать комментарий
PATCH /users/{userId}/events/{eventId}/comments/{commId} - Обновить комментарий
DELETE /admin/comments/{commentId} - Удалить комментарий (админ)
## 🚀 Установка и запуск
### Предварительные требования
Java 17 или выше
Maven 3.6+
PostgreSQL 12+
Docker (опционально)
### Локальная установка
#### Клонирование репозитория
git clone https://github.com/Borgex14/events-and-stats-service.git
cd events-and-stats-service
#### Настройка базы данных
Создание базы данных в PostgreSQL
createdb events_service
Или использование Docker
docker run --name main-service -e POSTGRES_DB=main \
-e POSTGRES_USER=main -e POSTGRES_PASSWORD=main \
-p 5433:5432 -d postgres:14
#### Настройка приложения
spring.datasource.url=jdbc:postgresql://ewm-db:5432/main
spring.datasource.username=main
spring.datasource.password=main
spring.datasource.driverClassName=org.postgresql.Driver
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=true
spring.jpa.database=postgresql
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
logging.level.org.hibernate.SQL=DEBUG
logging.level.root=DEBUG
logging.level.org.springframework=DEBUG
logging.level.org.hibernate=DEBUG
logging.level.org.springframework.web.client.RestTemplate=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
server.port=8080
stats-server.url=http://stats-server:9090
stats-server.enabled=true
#### Сборка и запуск
Сборка приложения
mvn clean package
Запуск
java -jar target/events-and-stats-service-1.0.0.jar
Или запуск через Maven
mvn spring-boot:run
### Запуск через Docker
#### Сборка образа
docker build -t events-and-stats-service
#### Запуск контейнеров
docker-compose up -d
## 🗃️ База данных
### Основные сущности
users - Пользователи системы
events - События и мероприятия
categories - Категории событий
requests - Запросы на участие
compilations - Подборки событий
comments - Комментарии к событиям
endpoint_hits - Статистика посещений
## 📄 Лицензия
Этот проект лицензирован под MIT License - смотрите файл LICENSE для деталей.
## 👥 Команда разработки
Разработано в рамках учебного проекта Яндекс. Практикум.