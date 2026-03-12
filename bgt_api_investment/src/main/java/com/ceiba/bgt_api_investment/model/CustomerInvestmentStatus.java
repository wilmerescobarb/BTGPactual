package com.ceiba.bgt_api_investment.model;

/**
 * Constantes que representan los posibles estados de una suscripción a un fondo de inversión.
 */
public final class CustomerInvestmentStatus {

    private CustomerInvestmentStatus() {
        // Clase de constantes, no instanciable
    }

    /** Suscripción activa */
    public static final String ACTIVE = "A";

    /** Suscripción cancelada */
    public static final String CANCELLED = "C";
}
