import { useEffect, useState } from "react";
import { obtenerPlatosParaMenu } from "../api/menus.ts";
import type { OrigenPlato, Plato, TipoOpcion } from "../api/menus.ts";
import "./ConfigurarMenu.css";

// P14: listado de platos para el menú (US13)
// Permite consultar los platos de la carta y los del restaurante que no están en ella,
// filtrando por el tipo de opción del menú (entrante, principal o postre) y por su origen.

const TIPOS: { valor: TipoOpcion | ""; texto: string }[] = [
  { valor: "", texto: "Todos" },
  { valor: "Entrante", texto: "Entrantes (entrantes y ensaladas)" },
  { valor: "Principal", texto: "Principales" },
  { valor: "Postre", texto: "Postres" },
];

const ORIGENES: { valor: OrigenPlato | ""; texto: string }[] = [
  { valor: "", texto: "Todos los platos del restaurante" },
  { valor: "carta", texto: "En la carta" },
  { valor: "fuera", texto: "Fuera de la carta" },
];

const formatoPrecio = new Intl.NumberFormat("es-ES", {
  style: "currency",
  currency: "EUR",
});

function ListadoPlatosMenu() {
  const [tipo, setTipo] = useState<TipoOpcion | "">("");
  const [origen, setOrigen] = useState<OrigenPlato | "">("");
  const [platos, setPlatos] = useState<Plato[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Al cambiar un filtro se marca la carga aquí y no dentro del efecto
  function cambiarFiltro(aplicar: () => void) {
    aplicar();
    setCargando(true);
    setError(null);
  }

  useEffect(() => {
    let vigente = true;
    obtenerPlatosParaMenu(tipo || undefined, origen || undefined)
      .then((resultado) => {
        if (vigente) setPlatos(resultado);
      })
      .catch(() => {
        if (vigente) setError("No se pudieron cargar los platos.");
      })
      .finally(() => {
        if (vigente) setCargando(false);
      });
    // Evita que una respuesta antigua sobrescriba la de un filtro más reciente
    return () => {
      vigente = false;
    };
  }, [tipo, origen]);

  return (
    <section className="configurar-menu">
      <h1>Listado de platos para el menú</h1>

      <div className="filtros-platos">
        <label>
          <span>Tipo de opción</span>
          <select
            value={tipo}
            onChange={(e) =>
              cambiarFiltro(() => setTipo(e.target.value as TipoOpcion | ""))
            }
          >
            {TIPOS.map((t) => (
              <option key={t.valor} value={t.valor}>
                {t.texto}
              </option>
            ))}
          </select>
        </label>
        <label>
          <span>Origen</span>
          <select
            value={origen}
            onChange={(e) =>
              cambiarFiltro(() => setOrigen(e.target.value as OrigenPlato | ""))
            }
          >
            {ORIGENES.map((o) => (
              <option key={o.valor} value={o.valor}>
                {o.texto}
              </option>
            ))}
          </select>
        </label>
      </div>

      {error && <p className="mensaje mensaje-error">{error}</p>}
      {!error && cargando && <p>Cargando platos…</p>}
      {!error && !cargando && platos.length === 0 && (
        <p>No hay platos con estos filtros.</p>
      )}

      {!error && !cargando && platos.length > 0 && (
        <table className="tabla-platos">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Categoría</th>
              <th>Precio</th>
              <th>Origen</th>
            </tr>
          </thead>
          <tbody>
            {platos.map((plato) => (
              <tr key={plato.idPlato}>
                <td>{plato.nombre}</td>
                <td>{plato.descripcion}</td>
                <td>{plato.categoria}</td>
                <td className="precio">{formatoPrecio.format(plato.precio)}</td>
                <td>{plato.enCarta ? "Carta" : "Fuera de carta"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

export default ListadoPlatosMenu;
