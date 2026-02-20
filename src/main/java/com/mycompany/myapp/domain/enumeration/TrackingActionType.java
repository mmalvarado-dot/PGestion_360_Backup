package com.mycompany.myapp.domain.enumeration;

/**
 * The TrackingActionType enumeration.
 */
public enum TrackingActionType {
    CREACION, // <--- AGREGAR ESTO (Nuevo registro)
    EDICION, // Cambio de datos
    CAMBIO_ESTADO, // Cambio de flujo (Pendiente -> En Proceso)
}
