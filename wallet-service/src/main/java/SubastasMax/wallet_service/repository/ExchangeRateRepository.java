package SubastasMax.wallet_service.repository;

import SubastasMax.wallet_service.model.ExchangeRate;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class ExchangeRateRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "exchange_rates";

    public ExchangeRateRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try {
            Timestamp now = Timestamp.now();
            exchangeRate.setDate(now);
            exchangeRate.setUpdatedAt(now);

            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            exchangeRate.setId(docRef.getId());

            docRef.set(exchangeRate).get();
            return exchangeRate;
        } catch (Exception e) {
            throw new RuntimeException("Error saving exchange rate", e);
        }
    }

    public ExchangeRate update(String id, ExchangeRate exchangeRate) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            
            Timestamp now = Timestamp.now();
            exchangeRate.setUpdatedAt(now);
            exchangeRate.setId(id);

            docRef.set(exchangeRate).get();
            return findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error updating exchange rate", e);
        }
    }

    public ExchangeRate findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            DocumentSnapshot document = docRef.get().get();

            if (!document.exists()) {
                throw new RuntimeException("Exchange rate not found");
            }

            return documentToExchangeRate(document);
        } catch (Exception e) {
            throw new RuntimeException("Error finding exchange rate", e);
        }
    }

    public ExchangeRate findLatest() {
        try {
            CollectionReference collection = firestore.collection(COLLECTION_NAME);
            Query query = collection.orderBy("updatedAt", Query.Direction.DESCENDING).limit(1);
            
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            if (documents.isEmpty()) {
                throw new RuntimeException("No exchange rates found");
            }

            return documentToExchangeRate(documents.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Error finding latest exchange rate", e);
        }
    }

    // ESTE ES EL MÉTODO CLAVE QUE SOLUCIONA EL ERROR
    private ExchangeRate documentToExchangeRate(DocumentSnapshot document) {
        String id = document.getId();
        Timestamp date = document.getTimestamp("date");
        Timestamp updatedAt = document.getTimestamp("updatedAt");
        
        // Obtener el mapa de rates como Object y convertir manualmente
        Object ratesObj = document.get("rates");
        Map<String, Double> rates = new HashMap<>();
        
        if (ratesObj instanceof Map) {
            Map<String, Object> rawRates = (Map<String, Object>) ratesObj;
            
            for (Map.Entry<String, Object> entry : rawRates.entrySet()) {
                Object value = entry.getValue();
                Double doubleValue;
                
                // Convertir Long a Double si es necesario
                if (value instanceof Long) {
                    doubleValue = ((Long) value).doubleValue();
                } else if (value instanceof Double) {
                    doubleValue = (Double) value;
                } else if (value instanceof Number) {
                    doubleValue = ((Number) value).doubleValue();
                } else {
                    doubleValue = 0.0;
                }
                
                rates.put(entry.getKey(), doubleValue);
            }
        }
        
        return new ExchangeRate(id, date, rates, updatedAt);
    }
}