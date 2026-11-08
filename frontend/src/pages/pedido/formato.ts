export const euros = (n: number): string =>
  n.toLocaleString('es-ES', { style: 'currency', currency: 'EUR' })
