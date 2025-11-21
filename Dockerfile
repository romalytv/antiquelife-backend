# 1. Етап збірки (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Збираємо jar файл, пропускаючи тести для швидкості
RUN mvn clean package -DskipTests

# 2. Етап запуску (Run)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Копіюємо зібраний файл з першого етапу
COPY --from=build /app/target/*.jar app.jar
# Відкриваємо порт 8080
EXPOSE 8080
# Команда запуску
ENTRYPOINT ["java", "-jar", "app.jar"]