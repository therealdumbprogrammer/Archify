FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY generator-core/pom.xml generator-core/pom.xml
COPY backend/pom.xml backend/pom.xml
COPY generator-core/src generator-core/src
COPY backend/src backend/src

RUN mvn -B -DskipTests clean package -pl backend -am

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --create-home --uid 10001 appuser

COPY --from=build /app/backend/target/backend-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080
USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
