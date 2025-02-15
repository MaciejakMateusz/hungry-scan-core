FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

COPY --from=build /app/target/hungry-scan-core-0.0.1.war app.war

EXPOSE 8082

ENV PORT=8082

ENTRYPOINT ["java","-jar","/app/app.war"]