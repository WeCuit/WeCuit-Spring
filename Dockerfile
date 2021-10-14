FROM openjdk:8-jdk-alpine
COPY ./target/*.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java","-jar", "/app/app.jar"]