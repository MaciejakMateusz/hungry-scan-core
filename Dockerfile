FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app

COPY --from=build /app/target/hungry-scan-core-0.0.1.war app.war

EXPOSE 8082
EXPOSE 5005

ENV PORT=8082
ENV DEBUG_PORT=5005
ENV ENABLE_DEBUG=false

ENTRYPOINT ["sh", "-c", "\
  if [ \"$ENABLE_DEBUG\" = \"true\" ]; then \
    echo \"Starting with remote debugging on port $DEBUG_PORT\"; \
    exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEBUG_PORT -jar /app/app.war; \
  else \
    echo \"Starting without remote debugging\"; \
    exec java -jar /app/app.war; \
  fi \
"]