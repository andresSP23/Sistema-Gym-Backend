-- Add ultimo_numero_factura column to sucursal table
ALTER TABLE sucursal ADD COLUMN IF NOT EXISTS ultimo_numero_factura BIGINT NOT NULL DEFAULT 0;

-- Backfill logic: Calculate the max invoice number number for each branch from existing sales to avoid collisions
-- Assumes format S{sucursalId}-{number}, e.g., S1-00000100 -> extracts 100
UPDATE sucursal s
SET ultimo_numero_factura = (
    SELECT COALESCE(MAX(CAST(SPLIT_PART(numero_factura, '-', 2) AS BIGINT)), 0)
    FROM ventas v
    WHERE v.sucursal_id = s.id
);
