import { ErrorApi } from '../../api/menus.ts'

export const euros = (n: number): string =>
  n.toLocaleString('es-ES', { style: 'currency', currency: 'EUR' })

// Texto de error para mostrar al usuario: usa el "detail" del backend si lo hay
export function textoError(e: unknown, porDefecto: string): string {
  if (e instanceof ErrorApi) {
    if (e.status === 401 || e.status === 403) {
      return 'Debes iniciar sesión para realizar el pedido.'
    }
    if (e.detalle) {
      return e.detalle
    }
  }
  return porDefecto
}
