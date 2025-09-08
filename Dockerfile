FROM gradle:7.4-jdk11 AS builder
WORKDIR /app
COPY . /app
RUN gradle clean build

FROM openjdk:11-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/LoginChallenge-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]