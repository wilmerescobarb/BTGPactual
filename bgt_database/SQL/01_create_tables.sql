-- ============================================================
-- BGT Database - Script de creación de tablas
-- Base de datos: PostgreSQL
-- ============================================================

-- Tabla: investment
CREATE TABLE IF NOT EXISTS investment (
    id          SERIAL          PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL,
    min_amount  DECIMAL(18, 2)  NOT NULL,
    category    VARCHAR(3)     NOT NULL
);

-- Tabla: customer
CREATE TABLE IF NOT EXISTS customer (
    id              SERIAL          PRIMARY KEY,
    names           VARCHAR(50)     NOT NULL,
    lastnames       VARCHAR(50)     NOT NULL,
    birthday        DATE            NOT NULL,
    document_type   CHAR(2)         NOT NULL,
    document_number VARCHAR(20)     NOT NULL,
    cellphone       VARCHAR(20)     NOT NULL,
    email           VARCHAR(100)     NOT NULL,
    username        VARCHAR(20)     NOT NULL UNIQUE,
    pass_user       TEXT            NOT NULL,
    amount          DECIMAL(18, 2)  NOT NULL DEFAULT 0.00,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: customer_investment
CREATE TABLE IF NOT EXISTS customer_investment (
    id_customer_investment          SERIAL          PRIMARY KEY,
    id_customer     INT             NOT NULL,
    id_investment   INT             NOT NULL,
    opened_at       TIMESTAMP       NOT NULL,
    closed_at       TIMESTAMP,
    invested_amount DECIMAL(18, 2)  NOT NULL,
    status          CHAR(1)         NOT NULL CHECK (status IN ('A', 'C')),

    CONSTRAINT fk_ci_customer   FOREIGN KEY (id_customer)   REFERENCES customer(id),
    CONSTRAINT fk_ci_investment FOREIGN KEY (id_investment) REFERENCES investment(id)
);

-- Comentarios en columnas de status
COMMENT ON COLUMN customer_investment.status IS 'A: Apertura, C: Cierre';
