# Proyecto Banco XYZ - Arquitectura de Microservicios y Migración Batch

Este repositorio contiene el código fuente del proyecto de modernización del sistema de gestión bancaria para el **Banco XYZ**. La solución migra un sistema monolítico legacy hacia una arquitectura distribuida basada en microservicios, implementando orquestación, mensajería asíncrona, resiliencia y procesamiento de datos masivos.

**🔗 Enlace al Repositorio Oficial:** https://github.com/KatherineIbanezL/BANCO-XYZ-MICROSERVICIOS/tree/main

## Stack Tecnológico
* **Backend Core:** Java 17, Spring Boot 3.x
* **Infraestructura Spring Cloud:** Netflix Eureka (Service Discovery), Spring Cloud Config Server
* **Integración y BFF:** Spring Cloud Gateway, Resilience4j (Circuit Breaker)
* **Seguridad:** Spring Security, OAuth2 (Resource Server & JWT)
* **Persistencia:** Spring Data JPA, Hibernate, MySQL 8
* **Mensajería Asíncrona:** Apache Kafka
* **Procesamiento de Datos (ETL):** Spring Batch (Chunk-oriented processing)
* **Contenerización y Orquestación:** Docker, Docker Compose

## Arquitectura del Sistema
El ecosistema se compone de las siguientes piezas principales:
* **Microservicios Core:** `ms-gestion-clientes`, `ms-gestion-cuentas`, `ms-procesamiento-pagos`.
* **Capas BFF (Backend for Frontend):** `bff-web`, `bff-mobile`, `bff-atm` (Optimizados por canal y protegidos con tokens JWT).
* **Servicios Base:** Servidor de Descubrimiento (Eureka) y Servidor de Configuración centralizado.
* **Bus de Eventos:** Apache Kafka para garantizar la consistencia eventual entre los pagos y la actualización de saldos en las cuentas.

## Requisitos Previos
1. Java 17 instalado en el entorno local (para desarrollo o compilación manual).
2. Maven 3.8+ (o utilizar el wrapper `mvnw` incluido en el proyecto).
3. Docker y Docker Compose instalados y en ejecución.
4. Postman (o similar) para probar los endpoints y simular peticiones con tokens JWT.

## Estructura del Proyecto
* `/discovery-server`: Servidor Eureka (Puerto 8761).
* `/config-server`: Servidor de configuraciones centralizado (Puerto 8888).
* `/auth-server`: Servidor de autorización para emitir tokens OAuth2 (Puerto 9000).
* `/ms-gestion-clientes`: Microservicio de clientes y Job Batch de migración (Puerto 8084).
* `/ms-gestion-cuentas`: Microservicio de saldos y consumidor de eventos Kafka (Puerto 8085).
* `/ms-procesamiento-pagos`: Microservicio de transacciones y productor de eventos Kafka (Puerto 8086).
* `/bff-web`, `/bff-mobile`, `/bff-atm`: Puertas de enlace seguras para cada plataforma.
* `/data`: Directorio mapeado como volumen en Docker para la ingesta de archivos CSV legacy.

## Despliegue Rápido (Local)
Para levantar toda la infraestructura del banco en contenedores aislados, sigue estos pasos desde la raíz del proyecto:
1. Asegúrate de tener los puertos requeridos (3306, 8761, 8888, 9092, etc.) libres en tu máquina.
2. Abre una terminal en la raíz del proyecto (donde se ubica el archivo `docker-compose.yml`).
3. Ejecuta el comando: `docker compose up -d` (esto construirá las imágenes y levantará la red interna).
4. Verifica que todos los contenedores estén saludables ejecutando: `docker compose ps`.
5. Accede a `http://localhost:8761` para visualizar el panel de Eureka y confirmar que los servicios se han registrado correctamente.

*Nota: Para instrucciones detalladas sobre cómo probar los procesos de Spring Batch y consumir las APIs a través de los BFF, consulta el archivo `instrucciones.md`.*