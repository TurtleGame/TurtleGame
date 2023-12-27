# Use an official OpenJDK runtime as a parent image
FROM openjdk:18-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/turtlegame-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]