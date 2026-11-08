// Llamadas al backend para los pedidos en seleccion (US3, US5)

import type { Usuario } from "./usuarios"
import { ErrorApi, leerDetalle } from './menus'

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

export interface EleccionMenuCustom {
    id : number
    opcion : string
}

export interface EleccionesMenu {
      eleccionMenu1 : EleccionMenuCustom
      eleccionMenu2 : EleccionMenuCustom
      eleccionMenu3 : EleccionMenuCustom
      eleccionMenu4 : EleccionMenuCustom
}

export interface MenuPedido {
    idMenu : number
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