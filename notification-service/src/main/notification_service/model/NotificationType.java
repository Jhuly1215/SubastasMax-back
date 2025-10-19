package Subastasmax.notificationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de notificación usados por notification-service.
 * - Los nombres de los enums se usan como valor JSON (por compatibilidad entre microservicios).
 * - Incluye utilidades para obtener un título por defecto y para distinguir si la notificación
 *   es visible al usuario final o es de sistema/admin.
 */
public enum NotificationType {
    NEW_BID("Nueva puja", true),
    OUTBID("Has sido superado", true),
    AUCTION_WIN("Ganaste la subasta", true),
    PAYMENT_CONFIRMED("Pago acreditado", true),
    WALLET_CAPTURED("Fondos capturados", true),
    WALLET_RELEASED("Fondos liberados", true),
    PAYMENT_FAILED("Pago fallido", true),
    SYSTEM("Sistema", false),
    INFO("Información", true),
    WARNING("Advertencia", true),
    ADMIN_ACTION("Acción administrativa", false);

    private final String displayName;
    private final boolean userFacing;

    NotificationType(String displayName, boolean userFacing) {
        this.displayName = displayName;
        this.userFacing = userFacing;
    }

    /**
     * Texto amigable para mostrar en la UI (p. ej. títulos por defecto).
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Indica si esta notificación debe mostrarse en el feed del usuario final.
     * Algunas notificaciones (ADMIN_ACTION, SYSTEM) pueden ser internas.
     */
    public boolean isUserFacing() {
        return userFacing;
    }

    /**
     * Devuelve un título por defecto para la notificación (puedes complementarlo con detalles).
     * Ej: AUCTION_WIN -> "¡Ganaste la subasta!"
     */
    public String defaultTitle() {
        switch (this) {
            case NEW_BID: return "Nueva puja";
            case OUTBID: return "Has sido superado";
            case AUCTION_WIN: return "¡Ganaste la subasta!";
            case PAYMENT_CONFIRMED: return "Pago acreditado";
            case WALLET_CAPTURED: return "Pago capturado";
            case WALLET_RELEASED: return "Fondos liberados";
            case PAYMENT_FAILED: return "Pago fallido";
            case INFO: return "Información";
            case WARNING: return "Advertencia";
            case ADMIN_ACTION: return "Acción administrativa";
            default: return "Notificación del sistema";
        }
    }

    /**
     * Serializa el enum como su nombre (para JSON).
     */
    @JsonValue
    public String toValue() {
        return name();
    }

    /**
     * Deserializa desde un string (caso-insensible) a NotificationType.
     * Acepta tanto el nombre del enum (NEW_BID) como el displayName (ej. "Nueva puja").
     */
    @JsonCreator
    public static NotificationType fromValue(String value) {
        if (value == null) return SYSTEM;
        String v = value.trim();
        // Try match by enum name (case-insensitive)
        for (NotificationType t : values()) {
            if (t.name().equalsIgnoreCase(v)) return t;
        }
        // Try match by display name
        for (NotificationType t : values()) {
            if (t.displayName.equalsIgnoreCase(v)) return t;
        }
        // Fallback to SYSTEM para evitar excepciones al deserializar valores desconocidos
        return SYSTEM;
    }
}