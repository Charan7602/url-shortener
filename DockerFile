# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first (better cache)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies (cached in Docker layers)
RUN ./mvnw dependency:go-offline

# Now copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /app/target/urlShortener-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Spring profile will come from env var on Render (SPRING_PROFILES_ACTIVE=prod)
ENTRYPOINT ["java", "-jar", "app.jar"]
