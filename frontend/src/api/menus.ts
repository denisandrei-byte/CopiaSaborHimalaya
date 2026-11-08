// Llamadas al backend para el menú diario (US12, US13)

export interface Plato {
  idPlato: number
  nombre: string
  descripcion: string
  precio: number
  categoria: string
  enCarta: boolean
}

export type TipoOpcion = 'Entrante' | 'Principal' | 'Postre'

// carta: solo platos de la carta; fuera: platos del restaurante que no están en la carta
export type OrigenPlato = 'carta' | 'fuera'

export interface ConfigurarMenuRequest {
  fecha: string
  entrantes: number[]
  principales: number[]
  postres: number[]
}

export class ErrorApi extends Error {
  readonly status: number
  // Explicación devuelta por el backend (campo "detail" del ProblemDetail), si la hay
  readonly detalle?: string

  constructor(status: number, mensaje: string, detalle?: string) {
    super(mensaje)
    this.status = status
    this.detalle = detalle
  }
}

export async function leerDetalle(respuesta: Response): Promise<string | undefined> {
  try {
    const cuerpo = await respuesta.json()
    return typeof cuerpo?.detail === 'string' ? cuerpo.detail : undefined
  } catch {
    return undefined
  }
}

// US13: platos que se pueden elegir para el menú. Sin filtros devuelve todos los platos del restaurante.
// Contrato: docs/contratos/menus.md
export async function obtenerPlatosParaMenu(tipo?: TipoOpcion, origen?: OrigenPlato): Promise<Plato[]> {
  const parametros = new URLSearchParams()
  if (tipo) parametros.set('tipo', tipo)
  if (origen) parametros.set('origen', origen)
  const consulta = parametros.toString()

  const respuesta = await fetch(`/api/menus/platos${consulta ? `?${consulta}` : ''}`)
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, 'No se pudieron cargar los platos')
  }
  return respuesta.json()
}

export async function configurarMenu(datos: ConfigurarMenuRequest): Promise<void> {
  const respuesta = await fetch('/api/menus', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  })
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, 'No se pudo guardar el menú', await leerDetalle(respuesta))
  }
}
