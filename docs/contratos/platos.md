# Contrato de la carta de platos

Contrato previsto para las funcionalidades US1 y US15.

## Consultar la carta

### Petición

```http
GET /api/platos/carta
```

No necesita cuerpo.

### Respuesta correcta

Código HTTP: `200 OK`

```json
[
  {
    "idPlato": 1,
    "nombre": "Momos vegetales",
    "descripcion": "Momos rellenos de verduras",
    "precio": 7.50,
    "categoria": "Entrantes",
    "enCarta": true
  }
]
```

La respuesta contiene únicamente platos disponibles en la carta (`enCarta = true`). Los platos se devuelven ordenados por categoría y nombre.

## Añadir un plato a la carta

### Petición

```http
POST /api/platos
Content-Type: application/json
```

```json
{
  "nombre": "Momos vegetales",
  "descripcion": "Momos rellenos de verduras",
  "precio": 7.50,
  "categoria": "Entrantes"
}
```

El cliente no envía `idPlato` ni `enCarta`. El servidor genera el identificador y guarda el plato como disponible en la carta.

### Respuesta correcta

Código HTTP: `201 Created`

```json
{
  "idPlato": 1,
  "nombre": "Momos vegetales",
  "descripcion": "Momos rellenos de verduras",
  "precio": 7.50,
  "categoria": "Entrantes",
  "enCarta": true
}
```

### Datos no válidos

Si falta el nombre, la descripción, el precio o la categoría, la petición debe responder con `400 Bad Request`.

También responde con `400 Bad Request` si la categoría no es una de las de la tabla siguiente.

## Categorías

La categoría es un valor fijo, definido en el enum `Categoria` del backend. En el JSON se usa la columna *Nombre*; al recibirla no se distinguen mayúsculas de minúsculas.

| Nombre | Constante en Java |
|--------|-------------------|
| `Entrantes` | `ENTRANTES` |
| `Ensaladas` | `ENSALADAS` |
| `Principales` | `PRINCIPALES` |
| `Especialidades` | `ESPECIALIDADES` |
| `Postres` | `POSTRES` |
| `Bebidas` | `BEBIDAS` |

Para añadir una categoría nueva hay que añadirla al enum y a esta tabla.
