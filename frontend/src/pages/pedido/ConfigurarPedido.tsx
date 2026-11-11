// Pantalla de selección del pedido: "Lo que has elegido" + "Tu selección" (US3)
import { useEffect, useRef, useState, type ReactNode } from "react";
import { obtenerPlatosParaMenu } from "../../api/menus.ts";
import type { Plato } from "../../api/menus.ts";
import { obtenerPlatosCarta } from "../../api/platos";
import type { EleccionesMenu, TipoEleccion } from "../../api/pedidos";
import { PRECIO_MENU, usePedido } from "./pedidoContext";
import { euros, textoError } from "./formato";
import "./pedido.css";

// Las 4 elecciones del menú, en el orden que espera el backend (eleccionMenu1..4)
const GRUPOS = [
  { clave: "eleccionMenu1", tipo: "Entrante" },
  { clave: "eleccionMenu2", tipo: "Principal" },
  { clave: "eleccionMenu3", tipo: "Postre" },
  { clave: "eleccionMenu4", tipo: "Bebida" },
] as const;

interface Props {
  onIrACarta: () => void; // "Añadir de la carta": vuelve a la carta, que ya tiene "Añadir al pedido"
  onRevisar: () => void; // navega a ConfirmarPedido
}

export default function ConfigurarPedido({ onIrACarta, onRevisar }: Props) {
  const pedido = usePedido();
  // Platos entre los que se puede elegir en cada hueco del menú
  const [opciones, setOpciones] = useState<Record<TipoEleccion, Plato[]> | null>(
    null,
  );
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");
  const [dialogoAbierto, setDialogoAbierto] = useState(false);

  useEffect(() => {
    let activo = true;
    // No existe un endpoint con el menú configurado para hoy, así que se ofrecen los platos
    // admitidos para cada tipo (GET /api/menus/platos) y las bebidas de la carta. El backend
    // comprueba al crear el pedido que cada plato pertenece al menú de hoy.
    Promise.all([
      obtenerPlatosParaMenu("Entrante"),
      obtenerPlatosParaMenu("Principal"),
      obtenerPlatosParaMenu("Postre"),
      obtenerPlatosCarta(),
    ])
      .then(([entrantes, principales, postres, carta]) => {
        if (!activo) return;
        setOpciones({
          Entrante: entrantes,
          Principal: principales,
          Postre: postres,
          Bebida: carta.filter((plato) => plato.categoria === "Bebidas"),
        });
      })
      .catch(
        (e) =>
          activo &&
          setError(textoError(e, "No se pudieron cargar los platos del menú.")),
      )
      .finally(() => activo && setCargando(false));
    return () => {
      activo = false;
    };
  }, []);

  const vacio = pedido.numElementos === 0;

  return (
    <main className="page-content">
      <div className="page-heading">
        <div>
          <h1>Tu pedido</h1>
          <p>Revisa lo que has elegido y ajusta las cantidades.</p>
        </div>
      </div>

      <div className="pedido-layout">
        <section>
          <div className="panel">
            <div className="panel-heading">
              <div>
                <h2>Lo que has elegido</h2>
                <p>{pedido.numElementos} elementos</p>
              </div>
            </div>
            <div className="table-scroll">
              <table>
                <thead>
                  <tr>
                    <th>PRODUCTO</th>
                    <th>PRECIO</th>
                    <th>CANTIDAD</th>
                    <th>IMPORTE</th>
                  </tr>
                </thead>
                <tbody>
                  {vacio && (
                    <tr>
                      <td colSpan={4} className="pedido-vacio">
                        Aún no has elegido nada. Añade platos de la carta o un
                        menú.
                      </td>
                    </tr>
                  )}

                  {pedido.menus.map((linea) => (
                    <tr key={`m-${linea.idLocal}`}>
                      <td>
                        <small>Menú completo</small>
                        <strong>Menú del día</strong>
                        <ul className="pedido-opciones">
                          {linea.detalle.map((nombre, i) => (
                            <li key={i}>{nombre}</li>
                          ))}
                        </ul>
                      </td>
                      <td>{euros(linea.precio)}</td>
                      <td>
                        <ControlCantidad
                          cantidad={linea.cantidad}
                          onCambiar={(d) =>
                            pedido.cambiarCantidadMenu(linea.idLocal, d)
                          }
                        />
                      </td>
                      <td className="pedido-importe">
                        {euros(linea.precio * linea.cantidad)}
                      </td>
                    </tr>
                  ))}

                  {pedido.platos.map((linea) => (
                    <tr key={`p-${linea.idPlato}`}>
                      <td>
                        <small>De nuestra carta</small>
                        <strong>{linea.nombre}</strong>
                      </td>
                      <td>{euros(linea.precio)}</td>
                      <td>
                        <ControlCantidad
                          cantidad={linea.cantidad}
                          onCambiar={(d) =>
                            pedido.cambiarCantidadPlato(linea.idPlato, d)
                          }
                        />
                      </td>
                      <td className="pedido-importe">
                        {euros(linea.precio * linea.cantidad)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
          <p className="pedido-mensaje">
            Puedes ajustar las cantidades. Una cantidad de 0 retira el producto.
          </p>

          <div className="pedido-acciones">
            <button className="secondary-button" onClick={onIrACarta}>
              Añadir de la carta
            </button>
            <button
              className="secondary-button"
              disabled={cargando || !!error || opciones === null}
              onClick={() => setDialogoAbierto(true)}
            >
              Añadir menú
            </button>
          </div>
          {cargando && (
            <p className="pedido-mensaje">Cargando los platos del menú...</p>
          )}
          {error && <p className="pedido-mensaje error">{error}</p>}
        </section>

        <aside className="panel pedido-resumen">
          <h2>Tu selección</h2>
          <ul>
            {pedido.menus.map((linea) => (
              <li key={`rm-${linea.idLocal}`}>
                <span>{linea.cantidad} × Menú del día</span>
                <b>{euros(linea.precio * linea.cantidad)}</b>
              </li>
            ))}
            {pedido.platos.map((linea) => (
              <li key={`rp-${linea.idPlato}`}>
                <span>
                  {linea.cantidad} × {linea.nombre}
                </span>
                <b>{euros(linea.precio * linea.cantidad)}</b>
              </li>
            ))}
          </ul>
          <div className="pedido-total">
            <span className="eyebrow">Total</span>
            <strong>{euros(pedido.total)}</strong>
          </div>
          <button
            className="primary-button"
            disabled={vacio}
            onClick={onRevisar}
          >
            Revisar pedido
          </button>
          <p className="pedido-nota">Solo se registra al confirmar.</p>
        </aside>
      </div>

      <Dialogo
        abierto={dialogoAbierto}
        onCerrar={() => setDialogoAbierto(false)}
      >
        {opciones && (
          <FormularioMenu
            opciones={opciones}
            onAnadir={(elecciones, detalle) => {
              pedido.anadirMenu(elecciones, detalle);
              setDialogoAbierto(false);
            }}
            onCancelar={() => setDialogoAbierto(false)}
          />
        )}
      </Dialogo>
    </main>
  );
}

/* ---------- Cantidad (− 1 +) ---------- */
function ControlCantidad({
  cantidad,
  onCambiar,
}: {
  cantidad: number;
  onCambiar: (delta: number) => void;
}) {
  return (
    <div className="pedido-cantidad">
      <button aria-label="Quitar uno" onClick={() => onCambiar(-1)}>
        −
      </button>
      <span>{cantidad}</span>
      <button aria-label="Añadir uno" onClick={() => onCambiar(1)}>
        +
      </button>
    </div>
  );
}

/* ---------- Diálogo modal con <dialog> nativo ---------- */
function Dialogo({
  abierto,
  onCerrar,
  children,
}: {
  abierto: boolean;
  onCerrar: () => void;
  children: ReactNode;
}) {
  const ref = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialogo = ref.current;
    if (!dialogo) return;
    if (abierto && !dialogo.open) dialogo.showModal();
    if (!abierto && dialogo.open) dialogo.close();
  }, [abierto]);

  return (
    <dialog
      ref={ref}
      className="pedido-dialogo"
      onClose={onCerrar}
      aria-label="Añadir menú del día"
    >
      {abierto && children}
    </dialog>
  );
}

/* ---------- Formulario: elegir las 4 opciones del menú del día ---------- */
function FormularioMenu({
  opciones,
  onAnadir,
  onCancelar,
}: {
  opciones: Record<TipoEleccion, Plato[]>;
  onAnadir: (elecciones: EleccionesMenu, detalle: string[]) => void;
  onCancelar: () => void;
}) {
  const [seleccion, setSeleccion] = useState<
    Partial<Record<TipoEleccion, number>>
  >({});
  const [error, setError] = useState("");

  function anadir() {
    const elecciones = {} as EleccionesMenu;
    const detalle: string[] = [];
    for (const grupo of GRUPOS) {
      const plato = opciones[grupo.tipo].find(
        (p) => p.idPlato === seleccion[grupo.tipo],
      );
      if (!plato) {
        setError(`Elige una opción de ${grupo.tipo.toLowerCase()}.`);
        return;
      }
      elecciones[grupo.clave] = { idPlato: plato.idPlato, opcion: grupo.tipo };
      detalle.push(plato.nombre);
    }
    onAnadir(elecciones, detalle);
  }

  return (
    <div>
      <h2>Menú del día</h2>
      <p>
        {euros(PRECIO_MENU)} · entrante, principal, postre y bebida. Se pide para
        hoy y solo admite los platos configurados en el menú de hoy.
      </p>

      {GRUPOS.map(({ tipo }) => (
        <label key={tipo}>
          {tipo}
          <select
            value={seleccion[tipo] ?? ""}
            onChange={(e) => {
              setSeleccion((s) => ({ ...s, [tipo]: Number(e.target.value) }));
              setError("");
            }}
          >
            <option value="" disabled>
              Elige una opción
            </option>
            {opciones[tipo].map((plato) => (
              <option key={plato.idPlato} value={plato.idPlato}>
                {plato.nombre}
              </option>
            ))}
          </select>
        </label>
      ))}

      {error && <p className="pedido-mensaje error">{error}</p>}
      <div className="pedido-dialogo-pie">
        <button className="secondary-button" onClick={onCancelar}>
          Cancelar
        </button>
        <button className="primary-button" onClick={anadir}>
          Añadir menú
        </button>
      </div>
    </div>
  );
}
