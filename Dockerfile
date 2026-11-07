# ---- Tahap 1: build (Vaadin production) ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
COPY src/ src/
RUN ./mvnw -B -Pproduction -DskipTests clean package

# ---- Tahap 2: runtime ----
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/ifs25002-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]