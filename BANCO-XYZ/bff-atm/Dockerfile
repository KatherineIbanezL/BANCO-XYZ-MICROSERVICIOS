# Etapa 1: Compilación con Maven
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de configuración y código fuente
COPY . .

# Compilar el proyecto omitiendo las pruebas para agilizar el build
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto por defecto
EXPOSE 8081

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]