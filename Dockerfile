FROM openjdk:17-jdk-alpine
LABEL authors="Burlaka Oleksandr"

ARG JAR_FILE=target/*.jar

COPY ./target/app-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]