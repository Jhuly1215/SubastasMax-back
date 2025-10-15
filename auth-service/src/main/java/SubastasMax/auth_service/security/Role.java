//auth_service/security/Role.java:
package SubastasMax.auth_service.security;

public enum Role {
    ADMIN, SUBASTADOR, PARTICIPANTE;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
