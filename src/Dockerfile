# Use official Maven image to build the app
FROM maven:3.8.8-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY main ./main
COPY test ./test
RUN mvn -B package --file pom.xml -DskipTests

# Use a smaller JRE image for running
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
