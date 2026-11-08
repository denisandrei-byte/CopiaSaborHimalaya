# Sabor del Himalaya

Proyecto de la asignatura **Ingeniería del Software II**.

Aplicación web para la gestión de pedidos de comida a domicilio de un restaurante nepalí.

## Tecnologías

- Frontend: React + TypeScript + Vite
- Backend: Spring Boot + Java 21
- Base de datos: PostgreSQL
- Contenedores: Docker + Docker Compose
- Integración continua: GitHub Actions

## Estructura del proyecto

```text
saborHimalaya/
├── backend/
├── frontend/
├── .github/
│   └── workflows/
│       └── ci.yml
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Cómo preparar el entorno

### 1. Clonar el repositorio

```bash
git clone https://github.com/LucassProgramming/saborHimalaya.git
cd saborHimalaya
```

### 2. Crear el archivo de variables de entorno

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

El archivo `.env` contiene la configuración local del proyecto y no debe subirse a Git.

## Cómo levantar el proyecto

Desde la raíz del repositorio:

```bash
docker compose up --build
```

Esto levanta automáticamente:

- PostgreSQL
- Backend Spring Boot
- Frontend React

Cuando todo esté iniciado:

**Frontend**

```text
http://localhost:5173
```

**Backend**

```text
http://localhost:8080
```

PostgreSQL funciona dentro de la red de Docker, por lo que no es necesario instalarlo directamente en el ordenador.

## Parar el proyecto

```bash
docker compose down
```

Los datos de PostgreSQL se mantienen porque se utiliza un volumen de Docker.

## Comandos útiles de Docker

Ver el estado de los servicios:

```bash
docker compose ps
```

Ver todos los logs:

```bash
docker compose logs
```

Ver los logs del backend:

```bash
docker compose logs backend
```

Ver los logs del frontend:

```bash
docker compose logs frontend
```

Ver los logs de PostgreSQL:

```bash
docker compose logs postgres
```

Levantar el proyecto reconstruyendo las imágenes:

```bash
docker compose up --build
```

Ejecutar los servicios en segundo plano:

```bash
docker compose up -d
```

Parar los servicios:

```bash
docker compose down
```

## Forma de trabajar con Git

Antes de empezar una tarea:

```bash
git checkout main
git pull
```

No se debe trabajar directamente sobre `main`.

Crear una rama nueva para cada tarea o funcionalidad:

```bash
git checkout -b tipo/nombre-tarea
```

Ejemplos:

```bash
git checkout -b feature/registro-clientes
git checkout -b fix/error-login
git checkout -b docs/actualizar-readme
```

Cuando se termine el trabajo:

```bash
git add .
git commit -m "tipo: descripción corta"
git push -u origin nombre-de-la-rama
```

Después se crea una **Pull Request** en GitHub para integrar los cambios en `main`.

## Formato de los commits

Seguiremos un formato sencillo basado en Conventional Commits:

```text
tipo: descripción corta
```

Tipos principales:

- `feat`: nueva funcionalidad
- `fix`: corrección de un error
- `docs`: cambios en documentación
- `test`: añadir o modificar tests
- `refactor`: cambios internos sin añadir funcionalidad
- `chore`: configuración, dependencias o mantenimiento
- `style`: cambios de formato que no afectan al funcionamiento
- `ci`: cambios relacionados con integración continua

Ejemplos:

```text
feat: añadir registro de clientes
fix: corregir conexión con PostgreSQL
docs: actualizar instrucciones del entorno
test: añadir pruebas del servicio de pedidos
refactor: reorganizar servicio de usuarios
chore: actualizar dependencias de Maven
ci: añadir validación del frontend
```

Los mensajes de commit deben ser cortos, claros y describir el cambio realizado.

Evitar mensajes como:

```text
cambios
arreglos
cosas
prueba
commit final
```

## Flujo de trabajo recomendado

Cada vez que se vaya a empezar una tarea:

```bash
git checkout main
git pull
git checkout -b tipo/nombre-tarea
```

Realizar los cambios y comprobar que el proyecto sigue funcionando:

```bash
docker compose up --build
```

Después:

```bash
git add .
git commit -m "tipo: descripción del cambio"
git push -u origin nombre-de-la-rama
```

Finalmente se crea una Pull Request hacia `main`.

## Integración continua

GitHub Actions ejecutará automáticamente comprobaciones cuando haya:

- push a `main`
- pull request hacia `main`

La CI comprobará el backend con Java 21, Maven y tests, y el frontend con instalación de dependencias, lint y build.

Antes de integrar una Pull Request en `main`, la CI debe haber terminado correctamente.

## Estado actual

Actualmente solo está preparada la infraestructura base del proyecto.