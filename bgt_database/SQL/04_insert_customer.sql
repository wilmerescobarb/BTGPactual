-- ============================================================
-- BGT Database - Datos iniciales tabla: customer
-- ============================================================

INSERT INTO customer (id, names, lastnames, birthday, document_type, document_number, cellphone, email, username, pass_user, amount, created_at)
OVERRIDING SYSTEM VALUE
VALUES
    (
        1,
        'Wilmer',
        'Escobar',
        '1993-01-30',
        'CC',
        '108530',
        '3122423574',
        'es.wilmer93@gmail.com',
        'wilmerescobar',
        '$2a$10$XGvoJBIRR/qREEvrynuvzeRY3/30q1HQqXWu3EIpj4cED.bHwt7nK',
        500000.00,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (id) DO NOTHING;

-- Ajustar la secuencia del SERIAL al máximo ID insertado
SELECT setval(pg_get_serial_sequence('customer', 'id'), MAX(id)) FROM customer;
