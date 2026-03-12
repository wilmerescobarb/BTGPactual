// ============================================================
// BGT Database - Datos iniciales colección: investment
// Base de datos: MongoDB
// ============================================================

use("bgt_db");

const investments = [
  { name: "FPV_BTG_PACTUAL_RECAUDADORA", min_amount: NumberDecimal("75000.00"),  category: "FPV" },
  { name: "FPV_BTG_PACTUAL_ECOPETROL",   min_amount: NumberDecimal("125000.00"), category: "FPV" },
  { name: "DEUDAPRIVADA",                min_amount: NumberDecimal("50000.00"),  category: "FIC" },
  { name: "FDO-ACCIONES",                min_amount: NumberDecimal("250000.00"), category: "FIC" },
  { name: "FPV_BTG_PACTUAL_DINAMICA",    min_amount: NumberDecimal("100000.00"), category: "FPV" }
];

investments.forEach((inv) => {
  db.investment.updateOne(
    { name: inv.name },
    { $setOnInsert: inv },
    { upsert: true }
  );
});

print(`Inversiones insertadas/verificadas: ${db.investment.countDocuments()}`);
