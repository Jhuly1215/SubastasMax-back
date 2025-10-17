package SubastasMax.chat_service.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   org.springframework.http.server.ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            var servlet = (ServletServerHttpRequest) request;
            String authHeader = servlet.getServletRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Aquí validar el token (mock): acepta cualquier mock-token-*
                String token = authHeader.substring(7);
                if (token.startsWith("mock-token-")) {
                    attributes.put("uid", token.substring("mock-token-".length()));
                    return true;
                }
                // En producción usar FirebaseAuthService.verifyIdToken(token)
            }
        }
        return false; // denegar handshake si no válido
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
