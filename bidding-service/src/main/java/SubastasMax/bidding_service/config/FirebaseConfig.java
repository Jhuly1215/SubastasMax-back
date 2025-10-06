/*package SubastasMax.bidding_service.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {
  @Bean
  public FirebaseApp firebaseApp() {
    if (!FirebaseApp.getApps().isEmpty()) return FirebaseApp.getInstance();
    return FirebaseApp.initializeApp(); // usa GOOGLE_APPLICATION_CREDENTIALS
  }
  @Bean public Firestore firestore() { return FirestoreClient.getFirestore(); }
}

*/