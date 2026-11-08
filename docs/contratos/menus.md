# Contrato del menú diario

Contrato de las funcionalidades US12 y US13.

## Listar platos para el menú (US13)

### Petición

```http
GET /api/menus/platos?tipo=Principal&origen=fuera
```

Parámetros opcionales:

| Parámetro | Valores | Si se omite |
|-----------|---------|-------------|
| `tipo` | `Entrante`, `Principal`, `Postre` | No se filtra por tipo de opción |
| `origen` | `carta` (en la carta), `fuera` (plato del restaurante que no está en la carta) | Se devuelven los dos |

Los valores no distinguen mayúsculas de minúsculas.

Qué categorías admite cada tipo de opción (las categorías válidas están en `docs/contratos/platos.md`):

| Tipo | Categorías |
|------|------------|
| `Entrante` | `Entrantes`, `Ensaladas` |
| `Principal` | Todas menos `Entrantes`, `Ensaladas`, `Postres` y `Bebidas` |
| `Postre` | `Postres` |

### Respuesta correcta

Código HTTP: `200 OK`

```json
[
  {
    "idPlato": 11,
    "nombre": "Momos de cordero",
    "descripcion": "Momos rellenos de cordero",
    "precio": 10.50,
    "categoria": "Especialidades",
    "enCarta": false
  }
]
```

Los platos se devuelven ordenados por categoría y nombre.

### Datos no válidos

Si `tipo` u `origen` tienen un valor distinto de los indicados, la respuesta es `400 Bad Request`.

## Configurar el menú del día (US12)

### Petición

```http
POST /api/menus
Content-Type: application/json
```

```json
{
  "fecha": "2026-11-05",
  "entrantes": [1, 2, 3],
  "principales": [4, 5, 6],
  "postres": [7, 8]
}
```

Las listas contienen los `idPlato` elegidos, en el orden en que se mostrarán. Se pueden usar platos que estén en la carta o fuera de ella. Si ya había un menú para esa fecha, se sustituyen sus opciones.

### Respuesta correcta

Código HTTP: `201 Created`, con la cabecera `Location: /api/menus/{idMenu}` y sin cuerpo.

El menú se guarda con un precio de 15 € y queda marcado como configurado.

### Errores

| Código | Cuándo |
|--------|--------|
| `400 Bad Request` | Falta la fecha, no hay exactamente 3 entrantes, 3 principales y 2 postres, se repite un plato dentro del mismo grupo, o un plato no pertenece a una categoría válida para su tipo de opción |
| `404 Not Found` | Algún `idPlato` no existe |

Los errores `400` detectados por el servicio (cantidades, repetidos, categoría) y los `404` devuelven un ProblemDetail cuyo campo `detail` explica el motivo.
