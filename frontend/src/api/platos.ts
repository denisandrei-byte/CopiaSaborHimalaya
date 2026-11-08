import { ErrorApi, leerDetalle } from './menus'
import type { Plato } from './menus'

export interface CrearPlatoRequest {
  nombre: string
  descripcion: string
  precio: number
  categoria: string
}

export async function obtenerPlatosCarta(): Promise<Plato[]> {
  const respuesta = await fetch('/api/platos/carta')
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, 'No se pudieron cargar los platos de la carta', await leerDetalle(respuesta))
  }
  return respuesta.json()
}

export async function crearPlato(datos: CrearPlatoRequest): Promise<Plato> {
  const respuesta = await fetch('/api/platos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  })
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, 'No se pudo crear el plato', await leerDetalle(respuesta))
  }
  return respuesta.json()
}
