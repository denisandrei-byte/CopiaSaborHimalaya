// Pantalla de revisión: aquí es donde el pedido se registra de verdad (POST /api/pedidos)
import { useState } from 'react'
import './pedido.css'
import { CrearPedido } from './pedidos'
import { usePedido } from './pedidoContext'
import { mensajeError } from './catalogo'
import { euros } from './formato'

const CLAVES = ['eleccionMenu1', 'eleccionMenu2', 'eleccionMenu3', 'eleccionMenu4'] as const

interface Props {
  onVolver: () => void // vuelve a ConfigurarPedido sin perder lo elegido
  onConfirmado: () => void // pedido creado correctamente
}

export default function ConfirmarPedido({ onVolver, onConfirmado }: Props) {
  const pedido = usePedido()
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState('')

  if (pedido.numElementos === 0) {
    return (
      <main className="pagina pagina-simple">
        <section className="panel resumen">
          <h2>Tu pedido está vacío</h2>
          <p className="mensaje">Añade platos o un menú antes de confirmar.</p>
          <button className="btn-principal" onClick={onVolver}>
            Volver a la selección
          </button>
        </section>
      </main>
    )
  }

  async function confirmar() {
    if (enviando) return // evita doble envío
    setEnviando(true)
    setError('')
    try {
      await CrearPedido(pedido.aRequest())
      pedido.vaciar()
      onConfirmado()
    } catch (e) {
      setError(mensajeError(e))
      setEnviando(false)
    }
  }

  return (
    <main className="pagina pagina-simple">
      <section className="panel resumen">
        <h2>Revisa tu pedido</h2>

        <ul className="revision">
          {pedido.menus.map((l) => (
            <li key={`m-${l.idLocal}`}>
              <div className="fila">
                <span>
                  {l.cantidad} × {l.nombre}
                </span>
                <b>{euros(l.precio * l.cantidad)}</b>
              </div>
              <ul className="opciones">
                {CLAVES.map((c) => (
                  <li key={c}>{l.elecciones[c].opcion}</li>
                ))}
              </ul>
            </li>
          ))}
          {pedido.platos.map((l) => (
            <li key={`p-${l.idPlato}`}>
              <div className="fila">
                <span>
                  {l.cantidad} × {l.nombre}
                </span>
                <b>{euros(l.precio * l.cantidad)}</b>
              </div>
            </li>
          ))}
        </ul>

        <div className="total">
          <span>Total</span>
          <strong>{euros(pedido.total)}</strong>
        </div>

        {error && (
          <p className="mensaje error" role="alert">
            {error}
          </p>
        )}

        <div className="acciones">
          <button className="btn-sec" onClick={onVolver} disabled={enviando}>
            Modificar pedido
          </button>
          <button className="btn-principal" onClick={confirmar} disabled={enviando}>
            {enviando ? 'Enviando…' : 'Confirmar pedido'}
          </button>
        </div>
        <p className="nota">El importe definitivo lo calcula el restaurante al registrar el pedido.</p>
      </section>
    </main>
  )
}
