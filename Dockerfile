# docker build -t blk-hacking-ind-karan-seth .
FROM maven:3-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 5477
ENTRYPOINT ["java", "-jar", "app.jar"]
