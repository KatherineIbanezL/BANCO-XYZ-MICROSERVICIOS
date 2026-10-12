# Guía de Despliegue - Banco XYZ

Este documento detalla los comandos necesarios para levantar la infraestructura en un entorno local mediante Docker Compose, así como la estrategia conceptual para migrar la arquitectura hacia un entorno en la nube utilizando Amazon Web Services (AWS).

## 1. Despliegue Local (Entorno de Desarrollo/Pruebas)

Para ejecutar el ecosistema completo en tu máquina local, asegúrate de tener Docker Desktop en ejecución y sigue estos pasos:

1. Abre una terminal (PowerShell o CMD) y navega hasta la carpeta raíz del proyecto (donde se encuentra el archivo `docker-compose.yml`).
2. Ejecuta el comando de compilación y levantamiento en segundo plano: `docker compose up -d --build`.
3. Verifica que los contenedores (Eureka, Config, Kafka, MySQL, Microservicios y BFF) estén en estado "Up" (saludables) con el comando: `docker compose ps`.
4. Para detener la infraestructura y liberar los recursos de tu máquina local, ejecuta: `docker compose down`.
5. (Opcional) Para revisar los logs de un servicio específico en tiempo real y monitorear eventos, utiliza: `docker logs -f <nombre_del_contenedor>` (ej. `docker logs -f ms-procesamiento-pagos`).

## 2. Despliegue en la Nube (AWS EC2)
Siga estos pasos para replicar el despliegue en un entorno de producción utilizando Amazon Web Services y WinSCP:
1. Ingrese a la consola de AWS y lance una nueva instancia EC2 seleccionando la imagen de sistema operativo Ubuntu.
2. Seleccione el tipo de instancia `t2.medium` para garantizar la memoria y capacidad de procesamiento que requieren los contenedores.
3. Genere y descargue un par de llaves (Key Pair) en formato `.ppk` al momento de crear la instancia para asegurar el acceso remoto.
4. Diríjase a la sección de Grupos de Seguridad (Security Groups) asociados a su instancia y agregue reglas de entrada (Inbound Rules) para habilitar los puertos 8084, 8085 y 8086 correspondientes a los microservicios, además del puerto 22 para SSH.
5. Abra el programa WinSCP en su computador local para iniciar la transferencia segura de archivos.
6. Configure una nueva sesión en WinSCP ingresando la IP Pública de su instancia EC2, el nombre de usuario administrador (por defecto `ubuntu`) y adjunte su archivo `.ppk` en la configuración avanzada de autenticación SSH.
7. Conéctese al servidor mediante WinSCP y copie la carpeta completa del proyecto (código y configuración) desde su máquina local hacia el directorio raíz del servidor Ubuntu.
8. Abra la consola de comandos SSH integrada en WinSCP (o utilice PuTTY), navegue hasta el directorio donde copió el proyecto y ejecute `sudo docker compose up -d` para poner el sistema bancario en línea.