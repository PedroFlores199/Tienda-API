# Tienda API

![CI](https://github.com/PedroFlores199/Tienda_API/actions/workflows/ci.yml/badge.svg)

Esta es una API para gestionar clientes, productos, pedidos y facturación de una pequeña tienda online. 

## Requisitos Previos
- Docker y Docker Compose
- Java 21

## Levantar el entorno
Para levantar la base de datos PostgreSQL y Mailpit (para atrapar los correos electrónicos simulados):
```bash
docker compose up -d
```

Para levantar la aplicación:
```bash
./mvnw spring-boot:run
```

## OpenAPI
La documentación de la API está disponible en:
[http://localhost:8080/docs](http://localhost:8080/docs)

## Decisiones Técnicas

1. **Numeración de facturas sin huecos ni saltos (Test de concurrencia)**
   Para evitar problemas de huecos o duplicados al generar facturas concurrentemente, se ha optado por implementar una tabla de secuencias personalizada (`secuencias_factura`). Al momento de emitir la factura, se busca la fila correspondiente al año actual y a la serie utilizando bloqueo pesimista en base de datos (`@Lock(LockModeType.PESSIMISTIC_WRITE)`). De este modo, garantizamos que las transacciones en paralelo esperen a obtener su número de manera segura y ordenada antes de hacer el `INSERT` final.

2. **Copia de los datos del cliente en la factura**
   Para garantizar la inmutabilidad legal de las facturas (si el cliente cambia su dirección en el futuro, la factura antigua no debe cambiar), la entidad `Factura` no guarda únicamente la relación `@ManyToOne` con `Cliente`, sino que guarda copias exactas del nombre, NIF, dirección e email en el instante de su emisión. De la misma manera ocurre con las `LineaFactura`, asegurando un snapshot en el momento `ENTREGADO`.

## Capturas
*(Añadir aquí las capturas requeridas de las peticiones de Postman / Swagger / Mailpit)*
