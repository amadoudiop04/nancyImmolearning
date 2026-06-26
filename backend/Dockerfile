# --- Étape 1 : build du jar avec Maven (Java 21) ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache des dépendances : on copie d'abord le pom seul.
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Puis le code source et on package (sans rejouer les tests ici, le CI s'en charge).
COPY src ./src
RUN mvn -q clean package -DskipTests

# --- Étape 2 : image d'exécution légère (JRE seul) ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render (et la plupart des PaaS) fournit le port via la variable $PORT.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
