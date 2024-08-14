# Buid
FROM maven:3.8.4-openjdk-17-slim as builder
WORKDIR /src
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean package -DskipTests


# # Multi-staging
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /src/target/jobportal-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

CMD ["java","-jar","app.jar"]
