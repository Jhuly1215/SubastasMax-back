package SubastasMax.wallet_service.repository;

import SubastasMax.wallet_service.exception.ExchangeRateNotFoundException;
import SubastasMax.wallet_service.model.ExchangeRate;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class ExchangeRateRepository {

    private static final String COLLECTION_NAME = "exchange_rates";
    private final Firestore firestore;

    public ExchangeRateRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try {
            CollectionReference collection = firestore.collection(COLLECTION_NAME);
            
            if (exchangeRate.getId() == null || exchangeRate.getId().isEmpty()) {
                DocumentReference docRef = collection.document();
                exchangeRate.setId(docRef.getId());
            }

            exchangeRate.setDate(Timestamp.now());
            exchangeRate.setUpdatedAt(Timestamp.now());

            Map<String, Object> data = new HashMap<>();
            data.put("date", exchangeRate.getDate());
            data.put("rates", exchangeRate.getRates());
            data.put("updatedAt", exchangeRate.getUpdatedAt());

            ApiFuture<WriteResult> result = collection.document(exchangeRate.getId()).set(data);
            result.get();

            return exchangeRate;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving exchange rate: " + e.getMessage(), e);
        }
    }

    public ExchangeRate update(String id, ExchangeRate exchangeRate) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            DocumentSnapshot document = docRef.get().get();

            if (!document.exists()) {
                throw new ExchangeRateNotFoundException("Exchange rate not found with id: " + id);
            }

            exchangeRate.setId(id);
            exchangeRate.setUpdatedAt(Timestamp.now());

            Map<String, Object> updates = new HashMap<>();
            updates.put("rates", exchangeRate.getRates());
            updates.put("updatedAt", exchangeRate.getUpdatedAt());

            ApiFuture<WriteResult> result = docRef.update(updates);
            result.get();

            Timestamp originalDate = document.getTimestamp("date");
            exchangeRate.setDate(originalDate);

            return exchangeRate;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error updating exchange rate: " + e.getMessage(), e);
        }
    }

    public ExchangeRate findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                throw new ExchangeRateNotFoundException("Exchange rate not found with id: " + id);
            }

            return documentToExchangeRate(document);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving exchange rate: " + e.getMessage(), e);
        }
    }

    public ExchangeRate findLatest() {
        try {
            CollectionReference collection = firestore.collection(COLLECTION_NAME);
            Query query = collection.orderBy("date", Query.Direction.DESCENDING).limit(1);
            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot querySnapshot = future.get();

            if (querySnapshot.isEmpty()) {
                throw new ExchangeRateNotFoundException("No exchange rates found");
            }

            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            return documentToExchangeRate(document);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving latest exchange rate: " + e.getMessage(), e);
        }
    }

    private ExchangeRate documentToExchangeRate(DocumentSnapshot document) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(document.getId());
        exchangeRate.setDate(document.getTimestamp("date"));
        exchangeRate.setRates((Map<String, Double>) document.get("rates"));
        exchangeRate.setUpdatedAt(document.getTimestamp("updatedAt"));
        return exchangeRate;
    }
}