# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached in Docker layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the source and build the jar
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Adjust the jar name if needed (check your target/ folder)
COPY --from=build /app/target/urlShortener-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
