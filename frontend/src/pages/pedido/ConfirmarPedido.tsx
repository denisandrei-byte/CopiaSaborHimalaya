// Pantalla de revisión: aquí es donde el pedido se registra de verdad (POST /api/pedidos)
import { useState } from 'react'
import { CrearPedido } from '../../api/pedidos'
import { usePedido } from './pedidoContext'
import { euros, textoError } from './formato'
import './pedido.css'

interface Props {
  onVolver: () => void // vuelve a ConfigurarPedido sin perder lo elegido
  onConfirmado: () => void // pedido creado correctamente
}

export default function ConfirmarPedido({ onVolver, onConfirmado }: Props) {
  const pedido = usePedido()
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState('')

  async function confirmar() {
    if (enviando) return // evita doble envío
    setEnviando(true)
    setError('')
    try {
      await CrearPedido(pedido.aRequest())
      pedido.vaciar()
      onConfirmado()
    } catch (e) {
      setError(textoError(e, 'No se pudo registrar el pedido.'))
      setEnviando(false)
    }
  }

  if (pedido.numElementos === 0) {
    return (
      <main className="page-content pedido-simple">
        <div className="page-heading">
          <div>
            <h1>Tu pedido está vacío</h1>
            <p>Añade platos de la carta o un menú antes de confirmar.</p>
          </div>
        </div>
        <button className="primary-button" onClick={onVolver}>
          Volver a la selección
        </button>
      </main>
    )
  }

  return (
    <main className="page-content pedido-simple">
      <div className="page-heading">
        <div>
          <h1>Revisa tu pedido</h1>
          <p>Comprueba lo que has elegido antes de confirmarlo.</p>
        </div>
      </div>

      <section className="panel">
        <div className="panel-heading">
          <div>
            <h2>Resumen</h2>
            <p>{pedido.numElementos} elementos</p>
          </div>
        </div>

        <ul className="pedido-revision">
          {pedido.menus.map((linea) => (
            <li key={`m-${linea.idLocal}`}>
              <div className="pedido-fila">
                <span>{linea.cantidad} × Menú del día</span>
                <b>{euros(linea.precio * linea.cantidad)}</b>
              </div>
              <ul className="pedido-opciones">
                {linea.detalle.map((nombre, i) => (
                  <li key={i}>{nombre}</li>
                ))}
              </ul>
            </li>
          ))}
          {pedido.platos.map((linea) => (
            <li key={`p-${linea.idPlato}`}>
              <div className="pedido-fila">
                <span>
                  {linea.cantidad} × {linea.nombre}
                </span>
                <b>{euros(linea.precio * linea.cantidad)}</b>
              </div>
            </li>
          ))}
        </ul>

        <div className="pedido-pie">
          <div className="pedido-total">
            <span className="eyebrow">Total</span>
            <strong>{euros(pedido.total)}</strong>
          </div>

          {error && (
            <p className="pedido-mensaje error" role="alert">
              {error}
            </p>
          )}

          <div className="pedido-acciones">
            <button className="secondary-button" onClick={onVolver} disabled={enviando}>
              Modificar pedido
            </button>
            <button className="primary-button" onClick={confirmar} disabled={enviando}>
              {enviando ? 'Enviando...' : 'Confirmar pedido'}
            </button>
          </div>
          <p className="pedido-nota">El importe definitivo lo calcula el restaurante al registrar el pedido.</p>
        </div>
      </section>
    </main>
  )
}
