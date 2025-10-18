// wallet-service/src/main/java/SubastasMax/wallet_service/security/FirebaseUserAuthentication.java
package SubastasMax.wallet_service.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseUserAuthentication extends AbstractAuthenticationToken {
    private final String uid;

    public FirebaseUserAuthentication(String uid, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uid = uid;
        setAuthenticated(true);
    }

    @Override public Object getCredentials() { return "N/A"; }
    @Override public Object getPrincipal() { return uid; }
    public String getUid() { return uid; }
}
