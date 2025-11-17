# Аргумент для выбора собираемого микросервиса (e.g., auth-service).
# !!! ЭТОТ АРГУМЕНТ ДОЛЖЕН БЫТЬ ПЕРЕДАН ПРИ СБОРКЕ через --build-arg SERVICE_NAME=...
ARG SERVICE_NAME

# ----------------------------------------------------
# 1. СТРОИТЕЛЬНАЯ СТАДИЯ: Сборка Maven
# ----------------------------------------------------
# Используем стандартный образ Maven
FROM maven:3.8.5-openjdk-17-slim AS builder
# Внутри стадии переопределяем ARG
ARG SERVICE_NAME
WORKDIR /app

# Шаг 1: Копируем общие файлы (они кэшируются)
COPY .mvn .mvn/
COPY pom.xml .

# Шаг 2: Копируем mvnw ИЗ КОРНЯ КОНТЕКСТА СБОРКИ (предполагая, что он лежит рядом с папкой microservices/)
COPY mvnw .
RUN chmod +x ./mvnw # Добавим права на выполнение

# Шаг 3: Копируем зависимый модуль event-contracts (он редко меняется)
COPY event-contracts event-contracts/

# Шаг 4: Копируем КОНКРЕТНЫЙ микросервис (этот слой меняется чаще всего)
COPY ${SERVICE_NAME} ${SERVICE_NAME}/

# Шаг 5: Запуск сборки
# (Удалили лишний шаг apt-get, так как в образе maven все есть)
RUN ./mvnw -B clean package -pl ${SERVICE_NAME} -am -DskipTests

# ----------------------------------------------------
# 2. ПРОД-СТАДИЯ: Запуск JAR
# ----------------------------------------------------
FROM amazoncorretto:17-alpine
WORKDIR /app

# Копируем только финальный JAR-файл указанного сервиса
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]