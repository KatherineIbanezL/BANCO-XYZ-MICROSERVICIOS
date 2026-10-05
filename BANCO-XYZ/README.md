# BANCO-XYZ: Sistema de Microservicios Resilientes y Seguros en la Nube

**Asignatura:** Desarrollo Backend III (PBY2203)
**Institución:** Duoc UC
**Evaluación:** "Desarrollando microservicios y resiliencia en la nube con Spring Cloud"

---

## 1. Descripción y Objetivo del Proyecto

El proyecto **BANCO-XYZ** es una solución bancaria basada en una arquitectura de microservicios distribuida, resiliente y segura. Permite la gestión de cuentas, consultas de saldo, transacciones (retiros/depósitos) y procesamiento por lotes.

### Objetivos Clave:
* **Seguridad Centralizada:** Implementación del protocolo OAuth2.0 y JWT mediante Spring Cloud Security y un Servidor de Autorización (`auth-server`).
* **Contenedorización y Orquestación:** Empaquetado de microservicios con Docker multi-stage y orquestación unificada mediante `docker-compose.yaml`.
* **Tolerancia a Fallos:** Implementación de patrones Circuit Breaker y Fallback con Resilience4j en los BFFs.
* **Mensajería Asíncrona:** Integración de Apache Kafka para la publicación y consumo asíncrono de eventos transaccionales.

---

## 2. Arquitectura del Sistema

```text
                        +----------------------+
                        |    config-server     | (Puerto 8888)
                        +----------+-----------+
                                   |
                        +----------v-----------+
                        |   discovery-server   | (Eureka - Puerto 8761)
                        +----------+-----------+
                                   |
                        +----------v-----------+
                        |      auth-server     | (OAuth2 / JWT - Puerto 9000)
                        +----------+-----------+
                                   |
                            Valida JWT/Scopes
                                   |
        +--------------------------+--------------------------+
        |                          |                          |
+-------v--------+        +--------v-------+        +---------v--------+
|    bff-web     |        |   bff-mobile   |        |     bff-atm      |
| (Puerto 8081)  |        | (Puerto 8082)  |        | (Puerto 8083)    |
+-------+--------+        +--------+-------+        +---------+--------+
        |                          |                          |
        | Rest Client + CB         | Resilience4j Fallback    | Kafka Producer
        |                          |                          |
        +--------------------------+--------------------------+
                                   |
                        +----------v-----------+
                        |      banco-core      | <--- Eventos Kafka
                        |   Resource Server    |      (transacciones-topic)
                        |    (Puerto 8080)     |
                        +----------+-----------+
                                   |
                        +----------v-----------+
                        |       mysql-db       | (Puerto 3306)
                        +----------------------+
```

---

## 3. Estructura del Código

```text
BANCO-XYZ/
├── auth-server/            # Servidor de Autorización OAuth2 (Generación de JWT)
├── config-server/          # Servidor de Configuración Centralizada (Spring Cloud Config)
├── discovery-server/       # Servidor de Descubrimiento de Servicios (Eureka)
├── banco-core/             # Microservicio Núcleo (Resource Server, Batch, Kafka Consumer)
├── bff-atm/                # BFF para Canal Cajero Automático (Kafka Producer)
├── bff-mobile/             # BFF para Canal Móvil (Resilience4j Circuit Breaker)
├── bff-web/                # BFF para Canal Web
├── evidencias/             # Capturas de pantalla y evidencias de ejecución
├── docker-compose.yaml     # Archivo de orquestación de contenedores
├── pom.xml                 # POM padre del proyecto Maven
└── README.md               # Documentación del proyecto
```

---

## 4. Tecnologías Utilizadas

* **Java 21 / Spring Boot 3.x**
* **Spring Cloud:** Eureka Server, Config Server, OpenFeign
* **Spring Security & OAuth2:** JWT, Resource Server
* **Resilience4j:** Circuit Breaker, Fallback
* **Apache Kafka:** Event-Driven Architecture
* **Spring Batch:** Procesamiento masivo por lotes
* **MySQL 8.0 & Spring Data JPA**
* **Docker & Docker Compose**

---

## 5. Puertos y Servicios

| Servicio | Puerto | Descripción / Acceso |
| :--- | :--- | :--- |
| `config-server` | `8888` | Servidor de configuración centralizada |
| `discovery-server` | `8761` | Dashboard Eureka (`http://localhost:8761`) |
| `auth-server` | `9000` | Servidor de Autorización OAuth2 |
| `banco-core` | `8080` | Core Bancario (Resource Server) |
| `bff-web` | `8081` | BFF Canal Web |
| `bff-mobile` | `8082` | BFF Canal Móvil |
| `bff-atm` | `8083` | BFF Canal Cajero Automático |
| `mysql-db` | `3306` | Base de Datos MySQL |

---

## 6. Datos de Prueba y Credenciales

* **Credenciales Client Credentials (OAuth2):**
  * `client-id`: `demo-client`
  * `client-secret`: `demo-secret`
* **Cuentas de Prueba Pre-cargadas (`mysql-db`):**
  * `ID 106`: Cuenta activa asociada a canal ATM.
  * `ID 103`: Cuenta activa asociada a canal Móvil.

---

## 7. Instrucciones de Ejecución

### Requisitos Previos:
* **Docker Desktop** activo.
* **Docker Compose v2** instalado.

### Despliegue Unificado:

```bash
# 1. Clonar el repositorio y posicionarse en la carpeta raíz del proyecto
cd BANCO-XYZ

# 2. Compilar y levantar la infraestructura con Docker Compose
docker-compose up --build

# 3. Verificar el estado de los contenedores
docker-compose ps
```

---

## 8. Guía de Pruebas y Validación

### A. Validación de Seguridad (OAuth2 / Scopes)

```bash
# 1. Rechazo sin autenticación (401 Unauthorized)
curl -i http://localhost:8083/api/atm/saldo/106

# 2. Generación de Token JWT (scope=atm)
curl -u demo-client:demo-secret \
  -d "grant_type=client_credentials&scope=atm" \
  http://localhost:9000/oauth2/token

# 3. Acceso denegado por Scope Cruzado (403 Forbidden)
curl -i -H "Authorization: Bearer <TOKEN_SCOPE_MOBILE>" \
  http://localhost:8083/api/atm/saldo/106

# 4. Acceso Exitoso (200 OK)
curl -H "Authorization: Bearer <TOKEN_SCOPE_ATM>" \
  http://localhost:8083/api/atm/saldo/106
```

### B. Validación de Resiliencia (Resilience4j)

```bash
# 1. Detener el microservicio central banco-core
docker stop banco-core

# 2. Realizar petición desde bff-mobile (Respuesta esperada: 200 OK con Fallback activo)
curl -H "Authorization: Bearer <TOKEN_MOBILE>" \
  http://localhost:8082/api/mobile/resumen/103

# 3. Restablecer el servicio
docker start banco-core
```

### C. Validación de Mensajería Asíncrona (Kafka)

```bash
# 1. Publicar una transacción desde bff-atm
curl -X PUT -H "Authorization: Bearer <TOKEN_ATM>" \
  "http://localhost:8083/api/atm/retirar/106?monto=1000"

# 2. Verificar el procesamiento del evento en los logs del consumidor banco-core
docker logs --tail 30 banco-core
```

### D. Validación de Procesamiento por Lotes (Spring Batch)

```bash
# 1. Ejecutar el Job de procesamiento masivo
curl -X POST -H "Authorization: Bearer <TOKEN_ATM>" \
  http://localhost:8080/api/batch/ejecutar-cierre

# 2. Verificar en los logs la ejecución del Job
docker logs --tail 50 banco-core
```