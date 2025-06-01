FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copie du JAR déjà généré (modifiez le nom si besoin)
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
