package com.ceiba.bgt_api_customer.constant;

import java.math.BigDecimal;

/**
 * Centraliza las constantes de negocio del dominio de clientes.
 */
public final class CustomerConstants {

    private CustomerConstants() {
        // Clase de constantes, no instanciable
    }

    /** Saldo inicial asignado a todo cliente al momento del registro. */
    public static final BigDecimal INITIAL_AMOUNT = new BigDecimal("500000");
}
