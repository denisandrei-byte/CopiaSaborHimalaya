// Estado del pedido en curso, compartido entre ConfigurarPedido y ConfirmarPedido.
// Nada se envía al backend hasta que el usuario pulsa "Confirmar pedido".

import {
  createContext,
  useContext,
  useRef,
  useState,
  type ReactNode,
} from "react";
import type { EleccionesMenu, PedidoRequest } from "../../api/pedidos";
import { PRECIO_MENU } from "./catalogo";

export interface LineaPlato {
  idPlato: number;
  nombre: string;
  precio: number;
  cantidad: number;
}

export interface LineaMenu {
  idLocal: number; // identifica la línea en pantalla (dos menús con elecciones distintas = dos líneas)
  idMenu: number;
  nombre: string;
  precio: number;
  elecciones: EleccionesMenu;
  cantidad: number;
}

interface PedidoContexto {
  platos: LineaPlato[];
  menus: LineaMenu[];
  numElementos: number;
  total: number;
  anadirPlato: (p: { id: number; nombre: string; precio: number }) => void;
  anadirMenu: (
    m: { id: number; nombre: string; precio?: number },
    elecciones: EleccionesMenu,
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

  const anadirPlato: PedidoContexto["anadirPlato"] = (p) =>
    setPlatos((prev) =>
      prev.some((l) => l.idPlato === p.id)
        ? prev.map((l) =>
            l.idPlato === p.id ? { ...l, cantidad: l.cantidad + 1 } : l,
          )
        : [
            ...prev,
            { idPlato: p.id, nombre: p.nombre, precio: p.precio, cantidad: 1 },
          ],
    );

  const anadirMenu: PedidoContexto["anadirMenu"] = (m, elecciones) =>
    setMenus((prev) => [
      ...prev,
      {
        idLocal: siguienteId.current++,
        idMenu: m.id,
        nombre: m.nombre,
        precio: m.precio ?? PRECIO_MENU,
        elecciones,
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

  // Convierte el estado en el cuerpo que espera CrearPedido (pedidos.ts)
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

export function usePedido(): PedidoContexto {
  const ctx = useContext(Contexto);
  if (!ctx) throw new Error("usePedido debe usarse dentro de <PedidoProvider>");
  return ctx;
}
