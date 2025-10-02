package SubastasMax.admin_service.model;

import java.time.LocalDateTime;

// JPA annotations removed because javax.persistence is not available in the classpath.
// If you add the JPA dependency to your build (for example javax.persistence:javax.persistence-api:2.2 or the appropriate Jakarta/JPA artifact),
// restore the @Entity, @Table and field annotations.
public class User {

    private Long id;

    private String name;

    private String email;

    private String role;

    private double reputation;

    private int strikes;

    private String status;

    private LocalDateTime lastActivity;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}