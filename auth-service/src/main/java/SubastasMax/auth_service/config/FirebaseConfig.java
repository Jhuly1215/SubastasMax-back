//auth_service/config/FirebaseConfig.java:
package SubastasMax.auth_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.Base64;

@Configuration
@ConditionalOnProperty(value = "app.firebase.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    @Value("${app.firebase.project-id:}")
    private String projectId;

    // Opcionales: 3 formas de credenciales (usa la que tengas):
    @Value("${app.firebase.credentials-file:}")   // ruta a JSON (dentro del contenedor)
    private String credentialsFile;

    @Value("${app.firebase.credentials-b64:}")    // JSON en base64 (útil en CI)
    private String credentialsB64;

    // Si no pasas ninguna de las dos de arriba, hará fallback a ADC:
    // GOOGLE_APPLICATION_CREDENTIALS debe apuntar al JSON montado por volumen.

    @PostConstruct
    public void init() throws Exception {
        // Si ya fue inicializado, no repetir
        if (!FirebaseApp.getApps().isEmpty()) {
            System.out.println("[FB] Firebase ya inicializado: " + FirebaseApp.getApps().get(0).getName());
            return;
        
        }
        System.out.println("[FB] Inicializando Firebase para projectId=" + projectId);

        if (projectId == null || projectId.isBlank()) {
            // Evita NPE y deja claro el motivo si falta la variable
            throw new IllegalStateException("app.firebase.project-id no está definido");
        }

        GoogleCredentials creds;
        if (credentialsB64 != null && !credentialsB64.isBlank()) {
            byte[] json = Base64.getDecoder().decode(credentialsB64);
            creds = GoogleCredentials.fromStream(new ByteArrayInputStream(json));
        } else if (credentialsFile != null && !credentialsFile.isBlank()) {
            creds = GoogleCredentials.fromStream(new FileInputStream(credentialsFile));
        } else {
            // Application Default Credentials (GOOGLE_APPLICATION_CREDENTIALS)
            creds = GoogleCredentials.getApplicationDefault();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(creds)
                .setProjectId(projectId)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);
        System.out.println("[FB] OK: " + app.getName());
        
    }
}