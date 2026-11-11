import { useEffect, useMemo, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { crearPlato, obtenerPlatosCarta } from "./api/platos";
import { ErrorApi } from "./api/menus";
import type { Plato } from "./api/menus";
import ConfigurarMenu from "./pages/ConfigurarMenu";
import ListadoPlatosMenu from "./pages/ListadoPlatosMenu";
import ConfigurarPedido from "./pages/pedido/ConfigurarPedido";
import ConfirmarPedido from "./pages/pedido/ConfirmarPedido";
import { usePedido } from "./pages/pedido/pedidoContext";
import "./App.css";
import "api/cuenta.ts";

const [cuenta, setCuenta] = useState<Cuenta | null>(null);

useEffect(() => {
  consultar<Cuenta>("/api/auth/yo")
    .then(setCuenta)
    .catch(() => setCuenta(null));
}, []);

type Pantalla =
  | "carta"
  | "gestion"
  | "nuevo"
  | "configurar"
  | "listado"
  | "pedido"
  | "confirmar";
type Categoria =
  | "Entrantes"
  | "Ensaladas"
  | "Principales"
  | "Especialidades"
  | "Postres"
  | "Bebidas";

const categorias: Categoria[] = [
  "Entrantes",
  "Ensaladas",
  "Principales",
  "Especialidades",
  "Postres",
  "Bebidas",
];
function money(value: number) {
  return `${value.toFixed(2).replace(".", ",")} €`;
}

function App() {
  const [pantalla, setPantalla] = useState<Pantalla>("carta");
  const [category, setCategory] = useState<"Todas" | Categoria>("Todas");
  const [dishes, setDishes] = useState<Plato[]>([]);
  const [cargandoCarta, setCargandoCarta] = useState(true);
  const [errorCarta, setErrorCarta] = useState("");
  const [notice, setNotice] = useState("");
  const [avisoPedido, setAvisoPedido] = useState("");
  const [guardandoPlato, setGuardandoPlato] = useState(false);
  const [errorPlato, setErrorPlato] = useState("");
  const visibleDishes = useMemo(
    () =>
      dishes.filter(
        (dish) =>
          dish.enCarta && (category === "Todas" || dish.categoria === category),
      ),
    [category, dishes],
  );

  useEffect(() => {
    obtenerPlatosCarta()
      .then(setDishes)
      .catch((error) =>
        setErrorCarta(
          error instanceof ErrorApi && error.detalle
            ? error.detalle
            : "No se pudo cargar la carta.",
        ),
      )
      .finally(() => setCargandoCarta(false));
  }, []);

  async function addDish(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErrorPlato("");
    setGuardandoPlato(true);
    const form = new FormData(event.currentTarget);
    try {
      const dish = await crearPlato({
        nombre: String(form.get("nombre")),
        descripcion: String(form.get("descripcion")),
        precio: Number(form.get("precio")),
        categoria: String(form.get("categoria")),
      });
      setDishes((current) => [...current, dish]);
      setNotice(`«${dish.nombre}» se ha añadido correctamente a la carta.`);
      setPantalla("gestion");
    } catch (error) {
      setErrorPlato(
        error instanceof ErrorApi && error.detalle
          ? error.detalle
          : "No se pudo añadir el plato a la carta.",
      );
    } finally {
      setGuardandoPlato(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="site-header">
        <button className="brand" onClick={() => setPantalla("carta")}>
          <span className="brand-mark">⌁</span>
          <span>
            <strong>Sabor del Himalaya</strong>
            <small>COCINA CON RAÍCES</small>
          </span>
        </button>
        <nav className="main-nav" aria-label="Navegación principal">
          <button
            className={pantalla === "carta" ? "active" : ""}
            onClick={() => setPantalla("carta")}
          >
            Carta
          </button>
          <button
            className={
              pantalla === "gestion" || pantalla === "nuevo" ? "active" : ""
            }
            onClick={() => setPantalla("gestion")}
          >
            Administración
          </button>
          <button
            className={pantalla === "configurar" ? "active" : ""}
            onClick={() => setPantalla("configurar")}
          >
            Menú diario
          </button>
          <button
            className={pantalla === "listado" ? "active" : ""}
            onClick={() => setPantalla("listado")}
          >
            Platos para el menú
          </button>
        </nav>
      </header>
      {pantalla === "carta" && (
        <Carta
          dishes={visibleDishes}
          category={category}
          onCategoryChange={setCategory}
          cargando={cargandoCarta}
          error={errorCarta}
          aviso={avisoPedido}
          onVerSeleccion={() => {
            setAvisoPedido("");
            setPantalla("pedido");
          }}
        />
      )}
      {pantalla === "gestion" && (
        <Gestion
          dishes={dishes}
          notice={notice}
          onAdd={() => {
            setNotice("");
            setErrorPlato("");
            setPantalla("nuevo");
          }}
        />
      )}
      {pantalla === "nuevo" && (
        <NuevoPlato
          onCancel={() => setPantalla("gestion")}
          onSubmit={addDish}
          guardando={guardandoPlato}
          error={errorPlato}
        />
      )}
      {pantalla === "configurar" && <ConfigurarMenu />}
      {pantalla === "listado" && <ListadoPlatosMenu />}
      {pantalla === "pedido" && (
        <ConfigurarPedido
          onIrACarta={() => setPantalla("carta")}
          onRevisar={() => setPantalla("confirmar")}
        />
      )}
      {pantalla === "confirmar" && (
        <ConfirmarPedido
          onVolver={() => setPantalla("pedido")}
          onConfirmado={() => {
            setAvisoPedido(
              "Tu pedido se ha registrado correctamente y está recibido.",
            );
            setPantalla("carta");
          }}
        />
      )}
      <footer className="site-footer">
        <span>© 2026 Sabor del Himalaya</span>
        <span>Tradición de lejos. Sabor de casa.</span>
        <span>• COCINA DEL HIMALAYA</span>
      </footer>
    </div>
  );
}

function PageHeading({
  label,
  title,
  subtitle,
  action,
}: {
  label: string;
  title: string;
  subtitle: string;
  action?: ReactNode;
}) {
  return (
    <div className="page-heading">
      <div>
        {label && <span className="eyebrow">{label}</span>}
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
      {action}
    </div>
  );
}

function Carta({
  dishes,
  category,
  onCategoryChange,
  cargando,
  error,
  aviso,
  onVerSeleccion,
}: {
  dishes: Plato[];
  category: "Todas" | Categoria;
  onCategoryChange: (category: "Todas" | Categoria) => void;
  cargando: boolean;
  error: string;
  aviso: string;
  onVerSeleccion: () => void;
}) {
  const pedido = usePedido();
  if (cargando)
    return (
      <main className="page-content">
        <PageHeading
          label=""
          title="Nuestra carta"
          subtitle="Cargando los platos disponibles."
        />
        <p className="empty-state">Cargando carta...</p>
      </main>
    );
  if (error)
    return (
      <main className="page-content">
        <PageHeading
          label=""
          title="Nuestra carta"
          subtitle="No se ha podido cargar la carta."
        />
        <p className="notice">{error}</p>
      </main>
    );
  return (
    <main className="page-content">
      <PageHeading
        label=""
        title="Nuestra carta"
        subtitle="Recetas con raíces. Sabores para compartir."
        action={
          <button
            className="secondary-button"
            onClick={() => {
              if (cuenta !== null) {
                onVerSeleccion();
              }
            }}
          >
            Ver selección <span className="count">{pedido.numElementos}</span> ♧
          </button>
        }
      />
      {aviso && <div className="notice success">{aviso}</div>}
      <div className="catalogue-layout">
        <div className="catalogue-main">
          <div className="catalogue-toolbar">
            <span className="eyebrow">DE NUESTRA COCINA A TU MESA</span>
            <label>
              Categoría{" "}
              <select
                value={category}
                onChange={(event) =>
                  onCategoryChange(event.target.value as "Todas" | Categoria)
                }
              >
                <option value="Todas">Todas las categorías</option>
                {categorias.map((item) => (
                  <option key={item}>{item}</option>
                ))}
              </select>
            </label>
          </div>
          {(category === "Todas" ? categorias : [category]).map((group) => {
            const groupDishes = dishes.filter(
              (dish) => dish.categoria === group,
            );
            return (
              <section className="product-group" key={group}>
                <div className="section-title">
                  <h2>{group}</h2>
                  <span>{groupDishes.length} opciones</span>
                </div>
                <div className="product-grid">
                  {groupDishes.map((dish) => (
                    <article className="product-card" key={dish.idPlato}>
                      <div className="food-placeholder">⌁</div>
                      <div className="product-body">
                        <div className="product-top">
                          <h3>{dish.nombre}</h3>
                          <strong>{money(dish.precio)}</strong>
                        </div>
                        <p>{dish.descripcion}</p>
                        <button
                          className="add-button"
                          onClick={() => pedido.anadirPlato(dish)}
                        >
                          Añadir al pedido
                        </button>
                      </div>
                    </article>
                  ))}
                </div>
                {groupDishes.length === 0 && (
                  <p className="empty-state">
                    No hay productos publicados en esta categoría.
                  </p>
                )}
              </section>
            );
          })}
        </div>
        <SelectionPreview onVer={onVerSeleccion} />
      </div>
    </main>
  );
}

function SelectionPreview({ onVer }: { onVer: () => void }) {
  const pedido = usePedido();
  return (
    <aside className="selection-preview">
      <div className="selection-icon">♧</div>
      <h2>Tu selección</h2>
      <p>
        {pedido.numElementos} elementos seleccionados · {money(pedido.total)}
      </p>
      <button
        className="primary-button"
        disabled={pedido.numElementos === 0}
        onClick={onVer}
      >
        Ver selección <span>→</span>
      </button>
    </aside>
  );
}

function Gestion({
  dishes,
  notice,
  onAdd,
}: {
  dishes: Plato[];
  notice: string;
  onAdd: () => void;
}) {
  return (
    <main className="page-content">
      <PageHeading
        label=""
        title="Gestión de la carta"
        subtitle="Cuida cada plato, cada bebida y lo que ofrecemos a nuestros clientes."
        action={
          <button className="primary-button" onClick={onAdd}>
            Añadir plato o bebida
          </button>
        }
      />
      {notice && <div className="notice success">{notice}</div>}
      <section className="panel">
        <div className="panel-heading">
          <div>
            <h2>Platos y bebidas</h2>
            <p>
              {dishes.length} elementos ·{" "}
              {dishes.filter((dish) => dish.enCarta).length} publicados en carta
            </p>
          </div>
          <span className="status-badge">En carta</span>
        </div>
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>NOMBRE</th>
                <th>CATEGORÍA</th>
                <th>PRECIO</th>
                <th>DISPONIBILIDAD</th>
              </tr>
            </thead>
            <tbody>
              {dishes.map((dish) => (
                <tr key={dish.idPlato}>
                  <td>
                    <strong>{dish.nombre}</strong>
                    <small>{dish.descripcion}</small>
                  </td>
                  <td>{dish.categoria}</td>
                  <td>{money(dish.precio)}</td>
                  <td>
                    <span className="status-badge">
                      {dish.enCarta ? "En carta" : "Fuera de carta"}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
      <p className="helper-text">
        Los productos de esta lista se muestran en la carta cuando están
        disponibles.
      </p>
    </main>
  );
}

function NuevoPlato({
  onCancel,
  onSubmit,
  guardando,
  error,
}: {
  onCancel: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  guardando: boolean;
  error: string;
}) {
  return (
    <main className="page-content">
      <PageHeading
        label=""
        title="Añadir plato o bebida"
        subtitle="Un nuevo sabor para nuestra carta."
      />
      <div className="form-columns">
        <form className="panel dish-form" onSubmit={onSubmit}>
          <div className="panel-heading">
            <div>
              <h2>Datos del producto</h2>
              <p>Completa los datos para añadirlo a la carta.</p>
            </div>
          </div>
          <div className="form-content">
            <label>
              Nombre *<input name="nombre" required />
            </label>
            <label>
              Descripción *<textarea name="descripcion" required rows={4} />
            </label>
            <div className="form-row">
              <label>
                Precio *
                <input
                  name="precio"
                  type="number"
                  min="0"
                  step="0.01"
                  required
                />
              </label>
              <label>
                Categoría *
                <select name="categoria" defaultValue="Entrantes" required>
                  {categorias.map((item) => (
                    <option key={item}>{item}</option>
                  ))}
                </select>
              </label>
            </div>
            {error && <p className="notice">{error}</p>}
          </div>
          <div className="form-actions">
            <button
              type="button"
              className="secondary-button"
              onClick={onCancel}
              disabled={guardando}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="primary-button"
              disabled={guardando}
            >
              {guardando ? "Guardando..." : "Guardar en la carta"}
            </button>
          </div>
        </form>
        <aside className="info-card">
          <span className="info-icon">⌁</span>
          <span className="eyebrow">NUESTRA CARTA, AL DÍA</span>
          <h2>Cada detalle cuenta.</h2>
          <p>
            Nombre, descripción, precio y categoría: todo lo que el cliente
            necesita para elegir.
          </p>
        </aside>
      </div>
    </main>
  );
}

export default App;
