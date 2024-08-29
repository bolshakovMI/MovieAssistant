FROM maven:3.8.7-openjdk-18-slim as builder
WORKDIR /opt/app
COPY mvnw pom.xml ./
COPY ./src ./src
RUN mvn install -DskipTests

FROM eclipse-temurin:18-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar"]
