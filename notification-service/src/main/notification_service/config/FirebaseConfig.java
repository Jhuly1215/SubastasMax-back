package Subastasmax.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${GOOGLE_APPLICATION_CREDENTIALS:${firebase.config.path:}}")
    private String credentialsPath;

    @Value("${firebase.database.url:}")
    private String firebaseDatabaseUrl;

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = null;

                if (credentialsPath != null && !credentialsPath.isBlank()) {
                    File f = new File(credentialsPath);
                    if (f.exists() && f.isFile()) {
                        serviceAccount = new FileInputStream(f);
                        log.info("Inicializando Firebase desde archivo: {}", f.getAbsolutePath());
                    } else {
                        serviceAccount = getClass().getClassLoader().getResourceAsStream(credentialsPath);
                        if (serviceAccount != null) {
                            log.info("Inicializando Firebase desde classpath: {}", credentialsPath);
                        }
                    }
                }

                if (serviceAccount == null) {
                    log.warn("No se encontró GOOGLE_APPLICATION_CREDENTIALS ni firebase.config.path; Firebase no se inicializará.");
                    return;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl(firebaseDatabaseUrl != null && !firebaseDatabaseUrl.isBlank() ? firebaseDatabaseUrl : null)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp inicializado correctamente.");
            } else {
                log.info("FirebaseApp ya inicializado, omitiendo.");
            }
        } catch (Exception ex) {
            log.error("Error inicializando Firebase", ex);
            throw new RuntimeException("Failed to initialize Firebase", ex);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
