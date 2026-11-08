import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { ErrorApi, configurarMenu, obtenerPlatosParaMenu } from '../api/menus'
import type { Plato } from '../api/menus'
import './ConfigurarMenu.css'

// P13: configurar menú diario (US12, US13)

const PRECIO_MENU = 15

// Cada selector guarda el id del plato elegido, o '' si aún no se ha elegido
type Seleccion = (number | '')[]

interface Mensaje {
  tipo: 'exito' | 'error'
  texto: string
}

function hoy(): string {
  const ahora = new Date()
  const mes = String(ahora.getMonth() + 1).padStart(2, '0')
  const dia = String(ahora.getDate()).padStart(2, '0')
  return `${ahora.getFullYear()}-${mes}-${dia}`
}

function estaCompleta(seleccion: Seleccion): seleccion is number[] {
  return seleccion.every((id) => id !== '')
}

function tieneRepetidos(seleccion: Seleccion): boolean {
  return new Set(seleccion).size !== seleccion.length
}

// Grupos que no tienen platos suficientes para completar el menú sin repetir
function gruposIncompletos(grupos: { titulo: string; disponibles: number; necesarios: number }[]): string[] {
  return grupos
    .filter((g) => g.disponibles < g.necesarios)
    .map((g) => `${g.titulo.toLowerCase()} (hay ${g.disponibles} de ${g.necesarios})`)
}

function mensajeDeError(error: unknown): string {
  if (error instanceof ErrorApi) {
    if (error.status === 400) return error.detalle ?? 'Los datos del menú no son válidos.'
    if (error.status === 404) return 'Alguno de los platos elegidos ya no existe.'
  }
  return 'No se pudo guardar el menú. Inténtalo de nuevo.'
}

interface GrupoPlatosProps {
  titulo: string
  singular: string
  platos: Plato[]
  seleccion: Seleccion
  onChange: (seleccion: Seleccion) => void
}

function GrupoPlatos({ titulo, singular, platos, seleccion, onChange }: GrupoPlatosProps) {
  function cambiar(posicion: number, valor: string) {
    const nueva = [...seleccion]
    nueva[posicion] = valor === '' ? '' : Number(valor)
    onChange(nueva)
  }

  return (
    <fieldset className="grupo-platos">
      <legend>
        {titulo} ({seleccion.length})
      </legend>
      {seleccion.map((idPlato, posicion) => (
        <label key={posicion}>
          <span>
            {singular} {posicion + 1}
          </span>
          <select value={idPlato} onChange={(e) => cambiar(posicion, e.target.value)} required>
            <option value="">Elige un plato</option>
            {platos.map((plato) => (
              <option key={plato.idPlato} value={plato.idPlato}>
                {`${plato.nombre} · ${plato.categoria}${plato.enCarta ? '' : ' (fuera de carta)'}`}
              </option>
            ))}
          </select>
        </label>
      ))}
    </fieldset>
  )
}

function ConfigurarMenu() {
  // CA13.1-CA13.3: cada grupo solo ofrece platos de las categorías que le corresponden
  const [platosEntrante, setPlatosEntrante] = useState<Plato[]>([])
  const [platosPrincipal, setPlatosPrincipal] = useState<Plato[]>([])
  const [platosPostre, setPlatosPostre] = useState<Plato[]>([])
  const [platosCargados, setPlatosCargados] = useState(false)
  const [errorCarga, setErrorCarga] = useState<string | null>(null)
  const [fecha, setFecha] = useState(hoy())
  const [entrantes, setEntrantes] = useState<Seleccion>(['', '', ''])
  const [principales, setPrincipales] = useState<Seleccion>(['', '', ''])
  const [postres, setPostres] = useState<Seleccion>(['', ''])
  const [guardando, setGuardando] = useState(false)
  const [mensaje, setMensaje] = useState<Mensaje | null>(null)

  useEffect(() => {
    // CA13.4: se cargan platos de la carta y de fuera de ella
    Promise.all([
      obtenerPlatosParaMenu('Entrante'),
      obtenerPlatosParaMenu('Principal'),
      obtenerPlatosParaMenu('Postre'),
    ])
      .then(([entrante, principal, postre]) => {
        setPlatosEntrante(entrante)
        setPlatosPrincipal(principal)
        setPlatosPostre(postre)
        setPlatosCargados(true)
      })
      .catch(() => setErrorCarga('No se pudieron cargar los platos.'))
  }, [])

  const faltan = gruposIncompletos([
    { titulo: 'Entrantes', disponibles: platosEntrante.length, necesarios: entrantes.length },
    { titulo: 'Principales', disponibles: platosPrincipal.length, necesarios: principales.length },
    { titulo: 'Postres', disponibles: platosPostre.length, necesarios: postres.length },
  ])

  async function guardar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    setMensaje(null)

    // CA12.1-CA12.3: hacen falta 3 entrantes, 3 principales y 2 postres
    if (!estaCompleta(entrantes) || !estaCompleta(principales) || !estaCompleta(postres)) {
      setMensaje({ tipo: 'error', texto: 'Elige todos los platos del menú.' })
      return
    }
    if (tieneRepetidos(entrantes) || tieneRepetidos(principales) || tieneRepetidos(postres)) {
      setMensaje({ tipo: 'error', texto: 'No repitas el mismo plato dentro de un grupo.' })
      return
    }

    setGuardando(true)
    try {
      await configurarMenu({ fecha, entrantes, principales, postres })
      setMensaje({ tipo: 'exito', texto: `Menú del ${fecha} configurado.` })
    } catch (error) {
      setMensaje({ tipo: 'error', texto: mensajeDeError(error) })
    } finally {
      setGuardando(false)
    }
  }

  return (
    <section className="configurar-menu">
      <h1>Configurar menú diario</h1>
      <p className="precio-menu">Precio del menú: {PRECIO_MENU} €</p>

      {errorCarga && <p className="mensaje mensaje-error">{errorCarga}</p>}
      {platosCargados && faltan.length > 0 && (
        <p className="mensaje mensaje-error">
          No hay platos suficientes para configurar el menú: {faltan.join(', ')}. Añade platos de esas categorías
          primero.
        </p>
      )}

      <form onSubmit={guardar}>
        <label className="campo-fecha">
          <span>Día</span>
          <input type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} required />
        </label>

        <GrupoPlatos titulo="Entrantes" singular="Entrante" platos={platosEntrante} seleccion={entrantes} onChange={setEntrantes} />
        <GrupoPlatos titulo="Principales" singular="Principal" platos={platosPrincipal} seleccion={principales} onChange={setPrincipales} />
        <GrupoPlatos titulo="Postres" singular="Postre" platos={platosPostre} seleccion={postres} onChange={setPostres} />

        {mensaje && <p className={`mensaje mensaje-${mensaje.tipo}`}>{mensaje.texto}</p>}

        <button type="submit" disabled={guardando || !platosCargados || faltan.length > 0}>
          {guardando ? 'Guardando…' : 'Guardar menú'}
        </button>
      </form>
    </section>
  )
}

export default ConfigurarMenu
