// ============================================================
// BGT Database - Datos iniciales colección: customer
// Base de datos: MongoDB
// ============================================================

use("bgt_db");

const customers = [
  {
    names:           "Wilmer",
    lastnames:       "Escobar",
    birthday:        new Date("1993-01-30"),
    document_type:   "CC",
    document_number: "108530",
    cellphone:       "3122423574",
    email:           "es.wilmer93@gmail.com",
    username:        "wilmerescobar",
    pass_user:       "$2a$10$XGvoJBIRR/qREEvrynuvzeRY3/30q1HQqXWu3EIpj4cED.bHwt7nK",
    amount:          NumberDecimal("500000.00"),
    created_at:      new Date()
  }
];

customers.forEach((cust) => {
  db.customer.updateOne(
    { username: cust.username },
    { $setOnInsert: cust },
    { upsert: true }
  );
});

print(`Clientes insertados/verificados: ${db.customer.countDocuments()}`);
