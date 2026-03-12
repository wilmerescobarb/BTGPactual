// ============================================================
// BGT Database - Script de creación de colecciones
// Base de datos: MongoDB
// ============================================================

// Seleccionar / crear la base de datos
use("bgt_db");

// ============================================================
// Colección: investment
// ============================================================
db.createCollection("investment", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "min_amount", "category"],
      properties: {
        name: {
          bsonType: "string",
          maxLength: 50,
          description: "Nombre del producto de inversión - requerido"
        },
        min_amount: {
          bsonType: "decimal",
          description: "Monto mínimo de inversión - requerido"
        },
        category: {
          bsonType: "string",
          maxLength: 3,
          description: "Categoría del producto (p.ej. FPV, FIC) - requerido"
        }
      }
    }
  },
  validationAction: "error",
  validationLevel: "strict"
});

// ============================================================
// Colección: customer
// ============================================================
db.createCollection("customer", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "names", "lastnames", "birthday",
        "document_type", "document_number",
        "cellphone", "email",
        "username", "pass_user",
        "amount", "created_at"
      ],
      properties: {
        names: {
          bsonType: "string",
          maxLength: 50,
          description: "Nombres del cliente - requerido"
        },
        lastnames: {
          bsonType: "string",
          maxLength: 50,
          description: "Apellidos del cliente - requerido"
        },
        birthday: {
          bsonType: "date",
          description: "Fecha de nacimiento - requerido"
        },
        document_type: {
          bsonType: "string",
          maxLength: 2,
          description: "Tipo de documento (2 caracteres, p.ej. CC, CE) - requerido"
        },
        document_number: {
          bsonType: "string",
          maxLength: 20,
          description: "Número de documento - requerido"
        },
        cellphone: {
          bsonType: "string",
          maxLength: 20,
          description: "Número de celular - requerido"
        },
        email: {
          bsonType: "string",
          maxLength: 100,
          description: "Correo electrónico - requerido"
        },
        username: {
          bsonType: "string",
          maxLength: 20,
          description: "Nombre de usuario único - requerido"
        },
        pass_user: {
          bsonType: "string",
          description: "Contraseña del usuario (hash bcrypt) - requerido"
        },
        amount: {
          bsonType: "decimal",
          description: "Saldo disponible del cliente - requerido"
        },
        created_at: {
          bsonType: "date",
          description: "Fecha y hora de creación del registro - requerido"
        }
      }
    }
  },
  validationAction: "error",
  validationLevel: "strict"
});

// Índice único sobre username
db.customer.createIndex(
  { username: 1 },
  { unique: true, name: "idx_customer_username_unique" }
);

// ============================================================
// Colección: customer_investment
// ============================================================
db.createCollection("customer_investment", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "id_customer", "id_investment",
        "opened_at", "invested_amount", "status"
      ],
      properties: {
        id_customer: {
          bsonType: "objectId",
          description: "Referencia al cliente (customer._id) - requerido"
        },
        id_investment: {
          bsonType: "objectId",
          description: "Referencia al producto de inversión (investment._id) - requerido"
        },
        opened_at: {
          bsonType: "date",
          description: "Fecha y hora de apertura de la inversión - requerido"
        },
        closed_at: {
          bsonType: ["date", "null"],
          description: "Fecha y hora de cierre de la inversión - opcional"
        },
        invested_amount: {
          bsonType: "decimal",
          description: "Monto invertido - requerido"
        },
        status: {
          bsonType: "string",
          enum: ["A", "C"],
          description: "A: Apertura, C: Cierre - requerido"
        }
      }
    }
  },
  validationAction: "error",
  validationLevel: "strict"
});

// Índices de referencia para consultas frecuentes
db.customer_investment.createIndex(
  { id_customer: 1 },
  { name: "idx_ci_customer" }
);

db.customer_investment.createIndex(
  { id_investment: 1 },
  { name: "idx_ci_investment" }
);
