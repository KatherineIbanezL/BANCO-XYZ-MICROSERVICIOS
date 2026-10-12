# ==========================================
# Etapa 1: Compilación general con Maven
# ==========================================
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar el archivo pom.xml padre y todos los pom.xml de los módulos
COPY pom.xml .
COPY shared-events/pom.xml shared-events/
COPY auth-server/pom.xml auth-server/
COPY ms-gestion-clientes/pom.xml ms-gestion-clientes/
COPY ms-gestion-cuentas/pom.xml ms-gestion-cuentas/
COPY ms-procesamiento-pagos/pom.xml ms-procesamiento-pagos/
COPY bff-web/pom.xml bff-web/
COPY bff-mobile/pom.xml bff-mobile/
COPY bff-atm/pom.xml bff-atm/
COPY config-server/pom.xml config-server/
COPY discovery-server/pom.xml discovery-server/

# Copiar el wrapper de Maven y otorgar permisos explícitos de ejecución
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw

# Copiar el resto del código fuente
COPY . .

# Argumento para indicar qué módulo compilar
ARG MODULE_NAME=discovery-server
RUN ./mvnw clean package -pl ${MODULE_NAME} -am -DskipTests

# ==========================================
# Etapa 2: Imagen ligera de ejecución
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG MODULE_NAME=discovery-server
COPY --from=build /app/${MODULE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]