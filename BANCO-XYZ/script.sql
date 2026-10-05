CREATE DATABASE IF NOT EXISTS banco_xyz;
USE banco_xyz;

CREATE TABLE IF NOT EXISTS transacciones (
    id BIGINT PRIMARY KEY,
    fecha VARCHAR(50),
    monto DOUBLE,
    tipo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS intereses (
    cuenta_id BIGINT PRIMARY KEY,
    nombre VARCHAR(100),
    saldo DOUBLE,
    edad DOUBLE,
    tipo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS cuentas_anuales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id BIGINT,
    fecha VARCHAR(50),
    transaccion VARCHAR(50),
    monto DOUBLE,
    descripcion VARCHAR(255)
);