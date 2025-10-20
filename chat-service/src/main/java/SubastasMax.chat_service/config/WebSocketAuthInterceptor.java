// SubastasMax.chat_service.config.WebSocketAuthInterceptor
package SubastasMax.chat_service.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   org.springframework.http.server.ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servlet) {
            String token = null;

            // 1) Authorization: Bearer ...
            String authHeader = servlet.getServletRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            // 2) Query param ?token=...
            if (token == null) {
                token = servlet.getServletRequest().getParameter("token");
            }
            if (token == null || token.isBlank()) {
                System.out.println("❌ WS handshake: sin token");
                return false;
            }

            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                attributes.put("uid", decoded.getUid());
                attributes.put("email", decoded.getEmail());
                System.out.println("✅ WS handshake OK uid=" + decoded.getUid());
                return true;
            } catch (Exception e) {
                System.out.println("❌ WS handshake token inválido: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
