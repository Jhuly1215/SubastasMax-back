package SubastasMax.admin_service.model;

import java.time.LocalDateTime;

/**
 * Modelo de usuario para Firestore y compatibilidad con el sistema de subastas.
 * 
 * - Incluye idString para manejar el ID propio de Firestore.
 * - Mantiene id (Long) para compatibilidad con otras partes del sistema.
 */
public class User {

    // ID numérico opcional (compatibilidad)
    private Long id;

    // ID de documento en Firestore
    private String idString;

    private String name;
    private String email;
    private String role;
    private double reputation;
    private int strikes;
    private String status;
    private LocalDateTime lastActivity;

    // ✅ Constructor vacío requerido por Firestore
    public User() {}

    // ✅ Constructor auxiliar (opcional)
    public User(Long id, String idString, String name, String email, String role,
                double reputation, int strikes, String status, LocalDateTime lastActivity) {
        this.id = id;
        this.idString = idString;
        this.name = name;
        this.email = email;
        this.role = role;
        this.reputation = reputation;
        this.strikes = strikes;
        this.status = status;
        this.lastActivity = lastActivity;
    }

    // ✅ Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdString() { return idString; }
    public void setIdString(String idString) { this.idString = idString; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getReputation() { return reputation; }
    public void setReputation(double reputation) { this.reputation = reputation; }

    public int getStrikes() { return strikes; }
    public void setStrikes(int strikes) { this.strikes = strikes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    // ✅ Método útil para debugging
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", idString='" + idString + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", reputation=" + reputation +
                ", strikes=" + strikes +
                ", status='" + status + '\'' +
                ", lastActivity=" + lastActivity +
                '}';
    }
}
