-- Eliminar la restricción antigua si existe
ALTER TABLE movimientos_dinero DROP CONSTRAINT IF EXISTS movimientos_dinero_metodo_check;

-- Recrearla con el nuevo valor (Agregando OTRO)
ALTER TABLE movimientos_dinero ADD CONSTRAINT movimientos_dinero_metodo_check CHECK (metodo IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'OTRO'));
