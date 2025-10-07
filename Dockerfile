# Build Stage
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew build -x test --dry-run


COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon


# Run Stage
FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
