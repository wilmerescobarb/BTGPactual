-- ============================================================
-- BGT Database - Datos iniciales tabla: investment
-- ============================================================

INSERT INTO investment (id, name, min_amount, category)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 'FPV_BTG_PACTUAL_RECAUDADORA', 75000.00,  'FPV'),
    (2, 'FPV_BTG_PACTUAL_ECOPETROL',   125000.00, 'FPV'),
    (3, 'DEUDAPRIVADA',                50000.00,  'FIC'),
    (4, 'FDO-ACCIONES',                250000.00, 'FIC'),
    (5, 'FPV_BTG_PACTUAL_DINAMICA',    100000.00, 'FPV')
ON CONFLICT (id) DO NOTHING;

-- Ajustar la secuencia del SERIAL al máximo ID insertado
SELECT setval(pg_get_serial_sequence('investment', 'id'), MAX(id)) FROM investment;
