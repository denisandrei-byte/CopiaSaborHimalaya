// Llamadas al backend para los pedidos en seleccion (US3, US5)

import type { Usuario } from "./usuarios"
import { ErrorApi, leerDetalle } from './menus.ts'

export interface Pedido {
  idPedido: number
  usuario: Usuario
  importeTotal : number
  estado : string
  fechaHoraCreacion : string
}

export interface PlatoPedido {
    idPlato : number
    cantidad : number
}

// Debe coincidir con el CHECK de eleccion_menu.tipo en la BBDD
export type TipoEleccion = 'Entrante' | 'Principal' | 'Postre' | 'Bebida'

// Igual que EleccionMenuCustom del backend: idPlato y el tipo de elección
export interface EleccionMenuCustom {
    idPlato : number
    opcion : TipoEleccion
}

export interface EleccionesMenu {
      eleccionMenu1 : EleccionMenuCustom
      eleccionMenu2 : EleccionMenuCustom
      eleccionMenu3 : EleccionMenuCustom
      eleccionMenu4 : EleccionMenuCustom
}

export interface MenuPedido {
    fechaMenu : string
    elecciones : EleccionesMenu
    cantidad : number
}

export interface PedidoRequest {
    menus : MenuPedido []
    platos : PlatoPedido []
}

export async function CrearPedido(datos: PedidoRequest): Promise<void> {
  const respuesta = await fetch('/api/pedidos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  })
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, 'No se pudo crear el pedido', await leerDetalle(respuesta))
  }
}