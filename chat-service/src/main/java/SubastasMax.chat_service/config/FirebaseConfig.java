package SubastasMax.chat_service.config;

import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        // lee el archivo de credenciales desde resources/
        InputStream serviceAccount = getClass()
            .getClassLoader()
            .getResourceAsStream("chat-service-key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            //.setDatabaseUrl("https://TU-PROYECTO.firebaseio.com") // opcional si no usas RTDB
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado en Chat Service");
        }
    }
}
