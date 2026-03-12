package com.ceiba.bgt_api_investment.constant;

/**
 * Centraliza los mensajes de respuesta exitosa del API de inversiones.
 */
public final class ResponseMessages {

    private ResponseMessages() {
        // Clase de constantes, no instanciable
    }

    public static final String GET_INVESTMENTS_OK = "Inversiones del cliente";
    public static final String GET_CATALOG_OK = "Catálogo de fondos de inversión";
    public static final String SUBSCRIBE_OK = "Suscripción creada exitosamente";
    public static final String UNSUBSCRIBE_OK = "Suscripción cancelada exitosamente";
}
