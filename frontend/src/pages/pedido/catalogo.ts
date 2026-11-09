// Llamadas al catálogo para la selección del pedido.
// ADAPTA: si ya tienes funciones equivalentes en menus.ts / platos.ts, borra
// estas y cambia los imports en ConfigurarPedido.tsx. Lo único que necesita
// la pantalla son las formas Plato y Menu de abajo.

import { ErrorApi, leerDetalle } from '../../api/menus.ts'

// El backend calcula los menús a 15 € fijos (CalcularImporteRequest)
export const PRECIO_MENU = 15

export interface Plato {
  id: number
  nombre: string
  precio: number
}

export interface OpcionMenu {
  id: number
  nombre: string
}

// Un grupo = una de las 4 elecciones del menú (p. ej. "Entrante", "Principal"...)
export interface GrupoMenu {
  titulo: string
  opciones: OpcionMenu[]
}

export interface Menu {
  id: number
  nombre: string
  precio: number
  grupos: GrupoMenu[] // en orden: eleccionMenu1, 2, 3 y 4
}

async function obtener<T>(ruta: string, mensaje: string): Promise<T> {
  const respuesta = await fetch(ruta)
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, mensaje, await leerDetalle(respuesta))
  }
  return respuesta.json() as Promise<T>
}

// TODO: crea estos endpoints en Spring si todavía no existen
export const ListarPlatos = () =>
  obtener<Plato[]>('/api/platos', 'No se pudo cargar la carta')

export const ListarMenus = () =>
  obtener<Menu[]>('/api/menus', 'No se pudieron cargar los menús')

export function mensajeError(e: unknown): string {
  return e instanceof Error ? e.message : 'Error inesperado'
}
