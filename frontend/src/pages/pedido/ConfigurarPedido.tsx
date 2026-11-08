// Pantalla de selección: "Lo que has elegido" + "Tu selección"
import { useEffect, useRef, useState, type ReactNode } from 'react'
import './pedido.css'
import type { EleccionesMenu } from './pedidos'
import { ListarMenus, ListarPlatos, mensajeError, type Menu, type Plato } from './catalogo'
import { usePedido } from './pedidoContext'
import { euros } from './formato'

const CLAVES = ['eleccionMenu1', 'eleccionMenu2', 'eleccionMenu3', 'eleccionMenu4'] as const

interface Props {
  onRevisar: () => void // navega a ConfirmarPedido
}

export default function ConfigurarPedido({ onRevisar }: Props) {
  const pedido = usePedido()
  const [platos, setPlatos] = useState<Plato[]>([])
  const [menus, setMenus] = useState<Menu[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [dialogo, setDialogo] = useState<'carta' | 'menu' | null>(null)

  useEffect(() => {
    let activo = true
    Promise.all([ListarPlatos(), ListarMenus()])
      .then(([p, m]) => {
        if (!activo) return
        setPlatos(p)
        setMenus(m)
      })
      .catch((e) => activo && setError(mensajeError(e)))
      .finally(() => activo && setCargando(false))
    return () => {
      activo = false
    }
  }, [])

  const vacio = pedido.numElementos === 0

  return (
    <main className="pagina">
      <section>
        <div className="panel">
          <div className="cabecera">
            <h2>Lo que has elegido</h2>
            <span>{pedido.numElementos} elementos</span>
          </div>

          <div className="tabla-scroll">
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Precio</th>
                  <th>Cantidad</th>
                  <th>Importe</th>
                </tr>
              </thead>
              <tbody>
                {vacio && (
                  <tr>
                    <td colSpan={4} className="vacio">
                      Aún no has elegido nada. Añade platos o un menú.
                    </td>
                  </tr>
                )}

                {pedido.menus.map((l) => (
                  <tr key={`m-${l.idLocal}`}>
                    <td>
                      <div className="tipo">Menú completo</div>
                      <div className="nombre">{l.nombre}</div>
                      <ul className="opciones">
                        {CLAVES.map((c) => (
                          <li key={c}>{l.elecciones[c].opcion}</li>
                        ))}
                      </ul>
                    </td>
                    <td>{euros(l.precio)}</td>
                    <td>
                      <ControlCantidad
                        cantidad={l.cantidad}
                        onCambiar={(d) => pedido.cambiarCantidadMenu(l.idLocal, d)}
                      />
                    </td>
                    <td className="importe">{euros(l.precio * l.cantidad)}</td>
                  </tr>
                ))}

                {pedido.platos.map((l) => (
                  <tr key={`p-${l.idPlato}`}>
                    <td>
                      <div className="tipo">De nuestra carta</div>
                      <div className="nombre">{l.nombre}</div>
                    </td>
                    <td>{euros(l.precio)}</td>
                    <td>
                      <ControlCantidad
                        cantidad={l.cantidad}
                        onCambiar={(d) => pedido.cambiarCantidadPlato(l.idPlato, d)}
                      />
                    </td>
                    <td className="importe">{euros(l.precio * l.cantidad)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <p className="ayuda">Puedes ajustar las cantidades. Una cantidad de 0 retira el producto.</p>
        </div>

        <div className="acciones">
          <button className="btn-sec" disabled={cargando || !!error} onClick={() => setDialogo('carta')}>
            Añadir de la carta
          </button>
          <button className="btn-sec" disabled={cargando || !!error} onClick={() => setDialogo('menu')}>
            Añadir menú
          </button>
        </div>
        {cargando && <p className="mensaje">Cargando carta…</p>}
        {error && <p className="mensaje error">{error}</p>}
      </section>

      <aside className="panel resumen">
        <h2>Tu selección</h2>
        <ul>
          {pedido.menus.map((l) => (
            <li key={`rm-${l.idLocal}`}>
              <span>
                {l.cantidad} × {l.nombre}
              </span>
              <b>{euros(l.precio * l.cantidad)}</b>
            </li>
          ))}
          {pedido.platos.map((l) => (
            <li key={`rp-${l.idPlato}`}>
              <span>
                {l.cantidad} × {l.nombre}
              </span>
              <b>{euros(l.precio * l.cantidad)}</b>
            </li>
          ))}
        </ul>
        <div className="total">
          <span>Total</span>
          <strong>{euros(pedido.total)}</strong>
        </div>
        <button className="btn-principal" disabled={vacio} onClick={onRevisar}>
          Revisar pedido
        </button>
        <p className="nota">Solo se registra al confirmar.</p>
      </aside>

      <Dialogo abierto={dialogo === 'carta'} titulo="Carta" onCerrar={() => setDialogo(null)}>
        <ul className="lista-platos">
          {platos.map((p) => (
            <li key={p.id}>
              <span>
                {p.nombre} · {euros(p.precio)}
              </span>
              <button className="btn-sec" onClick={() => pedido.anadirPlato(p)}>
                Añadir
              </button>
            </li>
          ))}
        </ul>
      </Dialogo>

      <Dialogo abierto={dialogo === 'menu'} titulo="Añadir menú" onCerrar={() => setDialogo(null)}>
        <FormularioMenu
          menus={menus}
          onAnadir={(menu, elecciones) => {
            pedido.anadirMenu(menu, elecciones)
            setDialogo(null)
          }}
          onCancelar={() => setDialogo(null)}
        />
      </Dialogo>
    </main>
  )
}

/* ---------- Cantidad (− 1 +) ---------- */
function ControlCantidad({ cantidad, onCambiar }: { cantidad: number; onCambiar: (delta: number) => void }) {
  return (
    <div className="cantidad">
      <button aria-label="Quitar uno" onClick={() => onCambiar(-1)}>
        −
      </button>
      <span>{cantidad}</span>
      <button aria-label="Añadir uno" onClick={() => onCambiar(1)}>
        +
      </button>
    </div>
  )
}

/* ---------- Diálogo genérico (usa <dialog> nativo) ---------- */
function Dialogo({
  abierto,
  titulo,
  onCerrar,
  children,
}: {
  abierto: boolean
  titulo: string
  onCerrar: () => void
  children: ReactNode
}) {
  const ref = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    const d = ref.current
    if (!d) return
    if (abierto && !d.open) d.showModal()
    if (!abierto && d.open) d.close()
  }, [abierto])

  return (
    <dialog ref={ref} onClose={onCerrar} aria-label={titulo}>
      <h3>{titulo}</h3>
      {abierto && children}
      <div className="dialogo-pie">
        <button className="btn-sec" onClick={onCerrar}>
          Cerrar
        </button>
      </div>
    </dialog>
  )
}

/* ---------- Formulario del menú: 4 elecciones ---------- */
function FormularioMenu({
  menus,
  onAnadir,
  onCancelar,
}: {
  menus: Menu[]
  onAnadir: (menu: Menu, elecciones: EleccionesMenu) => void
  onCancelar: () => void
}) {
  const [idMenu, setIdMenu] = useState<number>(menus[0]?.id ?? 0)
  const [seleccion, setSeleccion] = useState<Record<number, number>>({}) // índice de grupo -> id de opción
  const [error, setError] = useState('')

  const menu = menus.find((m) => m.id === idMenu)

  function cambiarMenu(id: number) {
    setIdMenu(id)
    setSeleccion({})
    setError('')
  }

  function anadir() {
    if (!menu) return
    if (menu.grupos.length !== CLAVES.length) {
      setError('Este menú no tiene las 4 elecciones configuradas.')
      return
    }
    const elecciones = {} as EleccionesMenu
    for (let i = 0; i < CLAVES.length; i++) {
      const opcion = menu.grupos[i].opciones.find((o) => o.id === seleccion[i])
      if (!opcion) {
        setError(`Elige una opción en: ${menu.grupos[i].titulo}`)
        return
      }
      elecciones[CLAVES[i]] = { id: opcion.id, opcion: opcion.nombre }
    }
    onAnadir(menu, elecciones)
  }

  if (menus.length === 0) return <p className="mensaje">No hay menús disponibles.</p>

  return (
    <div>
      <label htmlFor="selMenu">Menú</label>
      <select id="selMenu" value={idMenu} onChange={(e) => cambiarMenu(Number(e.target.value))}>
        {menus.map((m) => (
          <option key={m.id} value={m.id}>
            {m.nombre}
          </option>
        ))}
      </select>

      {menu?.grupos.map((g, i) => (
        <div key={i}>
          <label htmlFor={`grupo-${i}`}>{g.titulo}</label>
          <select
            id={`grupo-${i}`}
            value={seleccion[i] ?? ''}
            onChange={(e) => setSeleccion((s) => ({ ...s, [i]: Number(e.target.value) }))}
          >
            <option value="" disabled>
              Elige una opción
            </option>
            {g.opciones.map((o) => (
              <option key={o.id} value={o.id}>
                {o.nombre}
              </option>
            ))}
          </select>
        </div>
      ))}

      {error && <p className="mensaje error">{error}</p>}
      <div className="dialogo-pie">
        <button className="btn-sec" onClick={onCancelar}>
          Cancelar
        </button>
        <button className="btn-principal btn-auto" onClick={anadir}>
          Añadir menú
        </button>
      </div>
    </div>
  )
}
