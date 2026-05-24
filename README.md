# Gestor de Finanzas

Aplicación Full Stack para la gestión de finanzas personales.

El objetivo del proyecto es permitir a los usuarios gestionar ingresos, gastos y categorías de forma segura mediante una API REST desarrollada con Spring Boot y un futuro frontend desarrollado con React.

---

## Tecnologías

### Backend

- Java 21
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA
- PostgreSQL
- OpenAPI / Swagger

### DevOps

- Docker
- Docker Compose

### Frontend (Próximamente)

- React
- React Router
- Axios

---

## Arquitectura

El backend sigue una arquitectura por capas:

- Controller
- Service
- Repository
- DTO
- Mapper
- Security
- Exception Handling

La aplicación utiliza autenticación JWT y control de acceso basado en usuario para proteger los recursos.

---

## Backend - Finanzas API

Funcionalidades implementadas:

### Usuarios

- Registro
- Login
- Autenticación JWT

### Categorías

- Crear categoría
- Obtener categorías
- Actualizar categoría
- Eliminar categoría

### Transacciones

- Crear transacción
- Obtener transacciones
- Actualizar transacción
- Eliminar transacción

### Resúmenes financieros

- Resumen general
- Resumen semanal
- Resumen mensual

---

## Seguridad

Características implementadas:

- JWT Authentication
- BCrypt Password Encoding
- Stateless Sessions
- Ownership de recursos por usuario
- Protección de endpoints mediante Spring Security

---

## Docker

La aplicación puede ejecutarse mediante Docker Compose.

Servicios incluidos:

- PostgreSQL
- Finanzas API

---

## Documentación API

Swagger UI disponible en:

```text
/swagger-ui/index.html
```

---

## Estado Actual

### Backend

✅ Completado

### Docker

✅ Completado

### OpenAPI

✅ Completado

### Frontend React

🚧 En desarrollo

---

## Roadmap

- [x] Backend Spring Boot
- [x] Seguridad JWT
- [x] PostgreSQL
- [x] Docker
- [x] Swagger/OpenAPI
- [ ] Frontend React
- [ ] Integración Full Stack
- [ ] Despliegue
- [ ] Preparación entrevistas técnicas