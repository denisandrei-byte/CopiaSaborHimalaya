# Contrato de Selección para Pedidos

Contrato de las funcionalidades de US3.

## Crear Pedido (US5)

```http
POST api/pedidos
Content-Type: application/json
```

```json
{
  "platos": [{ "idPlato": 1, "cantidad": 2 }],
  "menus": [
    {
      "cantidad": 1,
      "elecciones": {
        "eleccionMenu1": { "idPlato": 2, "opcion": "Entrante" },
        "eleccionMenu2": { "idPlato": 3, "opcion": "Principal" },
        "eleccionMenu3": { "idPlato": 4, "opcion": "Postre" },
        "eleccionMenu4": { "idPlato": 5, "opcion": "Bebida" }
      }
    }
  ]
}
```

### Respuesta correcta

Código HTTP: `201 Created`, con la cabecera `Location: /api/pedidos/{idPedido}` y sin cuerpo.

## Errores

Todos los errores devuelven un `ProblemDetail` (`application/problem+json`) con el motivo en el campo `detail`.

| Código                       | Cuándo                                                                                                          |
| ---------------------------- | --------------------------------------------------------------------------------------------------------------- |
| `400 Bad Request`            | El cuerpo no es un JSON válido o `fechaMenu` no tiene formato de fecha                                          |
| `400 Bad Request`            | Falta un campo obligatorio (`idPlato`, `cantidad`, `fechaMenu`, `elecciones` o alguna de las cuatro elecciones) |
| `400 Bad Request`            | `cantidad` no es un número positivo                                                                             |
| `400 Bad Request`            | `opcion` no es `Entrante`, `Principal`, `Postre` o `Bebida`                                                     |
| `400 Bad Request`            | Las cuatro elecciones de un menú no son una de cada tipo                                                        |
| `400 Bad Request`            | El pedido no contiene ningún plato ni ningún menú                                                               |
| `400 Bad Request`            | El usuario autenticado no existe en la base de datos                                                            |
| `401 Unauthorized`           | No se ha enviado autenticación o es incorrecta                                                                  |
| `404 Not Found`              | Algún `idPlato` no existe o no está disponible en la carta                                                      |
| `404 Not Found`              | No hay un menú configurado para la `fechaMenu` indicada                                                         |
| `415 Unsupported Media Type` | La cabecera `Content-Type` no es `application/json`                                                             |

Ejemplo de respuesta de error:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "menus[0].cantidad: Cantidad debe ser positiva",
  "instance": "/api/pedidos"
}
```
