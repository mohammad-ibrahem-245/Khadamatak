FROM openjdk:25-ea-jdk-slim AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src

RUN chmod +x mvnw && ./mvnw -DskipTests clean package
RUN cp "$(ls target/*.jar | grep -v '.jar.original' | head -n 1)" app.jar

FROM openjdk:25-ea-jdk-slim

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8765

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
