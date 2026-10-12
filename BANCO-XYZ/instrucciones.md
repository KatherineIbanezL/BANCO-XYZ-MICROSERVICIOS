# Instrucciones de Ejecución y Pruebas - Banco XYZ

Este documento detalla los pasos necesarios para probar el correcto funcionamiento de la arquitectura de microservicios, la ejecución del proceso Batch y la validación de los endpoints protegidos (BFF). Se asume que los contenedores ya están en ejecución (ver `despliegue.md`).

## 1. Obtención del Token de Seguridad (OAuth2)
Todos los endpoints de los BFF están protegidos. Para consumirlos, primero debes obtener un token JWT desde el servidor de autorización.
1. Abre Postman y crea una nueva petición **POST** hacia `http://localhost:9000/oauth2/token`.
2. En la pestaña **Authorization**, selecciona **Basic Auth**.
3. Ingresa las credenciales del cliente (ej. Username: `web-client`, Password: `web-secret`).
4. En la pestaña **Body**, selecciona `x-www-form-urlencoded` y agrega la llave `grant_type` con el valor `client_credentials`.
5. Ejecuta la petición. Copia el valor del campo `"access_token"` que recibirás en el JSON de respuesta.

## 2. Ejecución de la Migración Batch (Spring Batch)
Para cargar los datos desde los archivos CSV legacy hacia las bases de datos MySQL, debes invocar el endpoint del microservicio de clientes.
1. Abre una terminal (o usa Postman).
2. Ejecuta el siguiente comando `curl` para procesar los datos de la semana 1:
`curl -X POST "http://localhost:8084/api/batch/ejecutar-carga-completa?semana=semana_1"`
3. Verifica la respuesta HTTP 200 OK indicando que el Job Multi-Step se inició exitosamente.
4. (Opcional) Revisa la base de datos MySQL (tabla `clientes` y `cuentas`) para verificar que los datos reales reemplazaron a los temporales.

## 3. Pruebas de los Endpoints BFF (Backend for Frontend)
Utiliza el token obtenido en el Paso 1 para autorizar las siguientes peticiones en Postman (pestaña **Authorization** -> **Bearer Token**).

### A. Prueba del BFF Web (Puerto 8081)
Retorna la información consolidada del cliente, su cuenta y sus transacciones.
1. Método: **GET**
2. URL: `http://localhost:8081/api/web/dashboard/115`
3. Agrega el Bearer Token en los headers.
4. Resultado esperado: Un JSON extenso con los datos personales, el saldo total y el historial de transacciones.

### B. Prueba del BFF Móvil (Puerto 8082)
Retorna una vista simplificada y ligera para ahorrar ancho de banda.
1. Método: **GET**
2. URL: `http://localhost:8082/api/mobile/resumen/110`
3. Agrega el Bearer Token en los headers.
4. Resultado esperado: Un JSON reducido solo con el número de cuenta, el saldo actual y los últimos movimientos.

### C. Prueba del BFF Cajeros / ATM (Puerto 8083)
Simula las operaciones críticas en un cajero automático.
1. **Consultar Saldo:** Realiza un **GET** a `http://localhost:8083/api/atm/saldo/115`.
2. **Procesar Retiro:** Realiza un **POST** a `http://localhost:8083/api/atm/retiro?cuentaId=115&monto=2000.0`. 
3. Recuerda incluir el Bearer Token en ambas peticiones. 
4. Resultado esperado: El retiro devolverá un mensaje de éxito, y si revisas los logs de Docker (`docker logs -f ms-gestion-cuentas`), verás que Kafka consumió el evento y actualizó el saldo.