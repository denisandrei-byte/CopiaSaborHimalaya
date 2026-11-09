// Estado del pedido en curso, compartido entre la carta, ConfigurarPedido y ConfirmarPedido.
// Nada se envía al backend hasta que el usuario pulsa "Confirmar pedido".

import {
  createContext,
  useContext,
  useRef,
  useState,
  type ReactNode,
} from "react";
import type { Plato } from "../../api/menus.ts";
import type { EleccionesMenu, PedidoRequest } from "../../api/pedidos";

export interface LineaPlato {
  idPlato: number;
  nombre: string;
  precio: number;
  cantidad: number;
}

export interface LineaMenu {
  idLocal: number; // identifica la línea en pantalla (dos menús con elecciones distintas = dos líneas)
  idMenu: number;
  precio: number;
  elecciones: EleccionesMenu;
  detalle: string[]; // nombres de los 4 platos elegidos, en orden, solo para mostrarlos
  cantidad: number;
}

interface PedidoContexto {
  platos: LineaPlato[];
  menus: LineaMenu[];
  numElementos: number;
  total: number;
  anadirPlato: (plato: Pick<Plato, "idPlato" | "nombre" | "precio">) => void;
  anadirMenu: (
    menu: { idMenu: number; precio: number },
    elecciones: EleccionesMenu,
    detalle: string[],
  ) => void;
  cambiarCantidadPlato: (idPlato: number, delta: number) => void;
  cambiarCantidadMenu: (idLocal: number, delta: number) => void;
  vaciar: () => void;
  aRequest: () => PedidoRequest;
}

const Contexto = createContext<PedidoContexto | null>(null);

export function PedidoProvider({ children }: { children: ReactNode }) {
  const [platos, setPlatos] = useState<LineaPlato[]>([]);
  const [menus, setMenus] = useState<LineaMenu[]>([]);
  const siguienteId = useRef(1);

  const anadirPlato: PedidoContexto["anadirPlato"] = (plato) =>
    setPlatos((prev) =>
      prev.some((l) => l.idPlato === plato.idPlato)
        ? prev.map((l) =>
            l.idPlato === plato.idPlato
              ? { ...l, cantidad: l.cantidad + 1 }
              : l,
          )
        : [
            ...prev,
            {
              idPlato: plato.idPlato,
              nombre: plato.nombre,
              precio: plato.precio,
              cantidad: 1,
            },
          ],
    );

  const anadirMenu: PedidoContexto["anadirMenu"] = (
    menu,
    elecciones,
    detalle,
  ) =>
    setMenus((prev) => [
      ...prev,
      {
        idLocal: siguienteId.current++,
        idMenu: menu.idMenu,
        precio: menu.precio,
        elecciones,
        detalle,
        cantidad: 1,
      },
    ]);

  // Una cantidad que llega a 0 retira la línea
  const cambiarCantidadPlato = (idPlato: number, delta: number) =>
    setPlatos((prev) =>
      prev
        .map((l) =>
          l.idPlato === idPlato ? { ...l, cantidad: l.cantidad + delta } : l,
        )
        .filter((l) => l.cantidad > 0),
    );

  const cambiarCantidadMenu = (idLocal: number, delta: number) =>
    setMenus((prev) =>
      prev
        .map((l) =>
          l.idLocal === idLocal ? { ...l, cantidad: l.cantidad + delta } : l,
        )
        .filter((l) => l.cantidad > 0),
    );

  const vaciar = () => {
    setPlatos([]);
    setMenus([]);
  };

  const total =
    platos.reduce((s, l) => s + l.precio * l.cantidad, 0) +
    menus.reduce((s, l) => s + l.precio * l.cantidad, 0);

  // Convierte el estado en el cuerpo que espera CrearPedido (api/pedidos.ts)
  const aRequest = (): PedidoRequest => ({
    platos: platos.map((l) => ({ idPlato: l.idPlato, cantidad: l.cantidad })),
    menus: menus.map((l) => ({
      idMenu: l.idMenu,
      elecciones: l.elecciones,
      cantidad: l.cantidad,
    })),
  });

  return (
    <Contexto.Provider
      value={{
        platos,
        menus,
        numElementos: platos.length + menus.length,
        total,
        anadirPlato,
        anadirMenu,
        cambiarCantidadPlato,
        cambiarCantidadMenu,
        vaciar,
        aRequest,
      }}
    >
      {children}
    </Contexto.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function usePedido(): PedidoContexto {
  const ctx = useContext(Contexto);
  if (!ctx) throw new Error("usePedido debe usarse dentro de <PedidoProvider>");
  return ctx;
}
