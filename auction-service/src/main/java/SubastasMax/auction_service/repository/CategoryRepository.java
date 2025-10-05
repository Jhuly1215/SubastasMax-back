package SubastasMax.auction_service.repository;

import SubastasMax.auction_service.model.Category;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class CategoryRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "categories";

    public String save(Category category) throws ExecutionException, InterruptedException {
        if (category.getTimestamp() == null) {
            category.setTimestamp(Instant.now());
        }

        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("amount", category.getAmount());
        categoryMap.put("auctionId", category.getAuctionId());
        categoryMap.put("status", category.getStatus());
        categoryMap.put("timestamp", com.google.cloud.Timestamp.ofTimeSecondsAndNanos(
                category.getTimestamp().getEpochSecond(),
                category.getTimestamp().getNano()));
        categoryMap.put("userId", category.getUserId());
        categoryMap.put("userName", category.getUserName());

        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(categoryMap);
        DocumentReference docRef = future.get();
        return docRef.getId();
    }

    public void update(String categoryId, Category category) throws ExecutionException, InterruptedException {
        Map<String, Object> updates = new HashMap<>();
        
        if (category.getAmount() != null) {
            updates.put("amount", category.getAmount());
        }
        if (category.getAuctionId() != null) {
            updates.put("auctionId", category.getAuctionId());
        }
        if (category.getStatus() != null) {
            updates.put("status", category.getStatus());
        }
        if (category.getUserId() != null) {
            updates.put("userId", category.getUserId());
        }
        if (category.getUserName() != null) {
            updates.put("userName", category.getUserName());
        }

        updates.put("timestamp", com.google.cloud.Timestamp.ofTimeSecondsAndNanos(
                Instant.now().getEpochSecond(),
                Instant.now().getNano()));

        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(categoryId)
                .update(updates);
        future.get();
    }

    public Category findById(String categoryId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(categoryId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return documentToCategory(document);
        }
        return null;
    }

    public List<Category> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Category> categories = new ArrayList<>();
        for (DocumentSnapshot doc : documents) {
            categories.add(documentToCategory(doc));
        }
        return categories;
    }

    public List<Category> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Category> categories = new ArrayList<>();
        for (DocumentSnapshot doc : documents) {
            categories.add(documentToCategory(doc));
        }
        return categories;
    }

    public List<Category> findByAuctionId(String auctionId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("auctionId", auctionId);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Category> categories = new ArrayList<>();
        for (DocumentSnapshot doc : documents) {
            categories.add(documentToCategory(doc));
        }
        return categories;
    }

    public void delete(String categoryId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(categoryId).delete();
        future.get();
    }

    private Category documentToCategory(DocumentSnapshot document) {
        com.google.cloud.Timestamp firestoreTimestamp = document.getTimestamp("timestamp");
        Instant timestamp = firestoreTimestamp != null 
                ? Instant.ofEpochSecond(firestoreTimestamp.getSeconds(), firestoreTimestamp.getNanos())
                : null;

        return Category.builder()
                .id(document.getId())
                .amount(document.getString("amount"))
                .auctionId(document.getString("auctionId"))
                .status(document.getString("status"))
                .timestamp(timestamp)
                .userId(document.getString("userId"))
                .userName(document.getString("userName"))
                .build();
    }
}