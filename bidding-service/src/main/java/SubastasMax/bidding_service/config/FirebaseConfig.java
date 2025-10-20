package SubastasMax.bidding_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() throws IOException {
        // Verifica si ya existe FirebaseApp inicializado
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase/bidding-service-firebase.json");

            if (serviceAccount == null) {
                throw new RuntimeException(
                        "No se pudo encontrar auction-service-key.json en el classpath");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://bidding-service-6fcd7-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado correctamente");
        } else {
            System.out.println("ℹ Firebase ya estaba inicializado");
        }

        return FirestoreClient.getFirestore();
    }
}
