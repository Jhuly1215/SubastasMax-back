package SubastasMax.wallet_service.repository;

import SubastasMax.wallet_service.model.ExchangeRate;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;

@Repository
public class ExchangeRateRepository {

    private static final String COLLECTION_NAME = "exchange_rates";

    @Autowired
    private Firestore firestore;

    public ExchangeRate save(ExchangeRate exchangeRate) throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        
        if (exchangeRate.getId() == null || exchangeRate.getId().isEmpty()) {
            DocumentReference docRef = collection.document();
            exchangeRate.setId(docRef.getId());
        }

        ApiFuture<WriteResult> future = collection.document(exchangeRate.getId()).set(exchangeRate);
        future.get();
        
        return exchangeRate;
    }

    public ExchangeRate findLatest() throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        
        ApiFuture<QuerySnapshot> future = collection
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get();
        
        QuerySnapshot querySnapshot = future.get();
        
        if (querySnapshot.isEmpty()) {
            return null;
        }
        
        QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
        return document.toObject(ExchangeRate.class);
    }

    public ExchangeRate findByDate(String date) throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        
        ApiFuture<QuerySnapshot> future = collection
                .whereEqualTo("date", parseDate(date))
                .limit(1)
                .get();
        
        QuerySnapshot querySnapshot = future.get();
        
        if (querySnapshot.isEmpty()) {
            return null;
        }
        
        QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
        return document.toObject(ExchangeRate.class);
    }

    private LocalDateTime parseDate(String dateStr) {
        return LocalDateTime.parse(dateStr);
    }
}