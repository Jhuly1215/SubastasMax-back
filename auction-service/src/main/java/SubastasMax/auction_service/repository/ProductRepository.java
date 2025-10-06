package SubastasMax.auction_service.repository;

import SubastasMax.auction_service.model.Product;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ProductRepository {

    private static final String COLLECTION_NAME = "products";

    @Autowired
    private Firestore firestore;

    public String save(Product product) throws ExecutionException, InterruptedException {
        if (product.getId() == null || product.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            product.setId(docRef.getId());
        }
        
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(product.getId())
                .set(product);
        result.get();
        return product.getId();
    }

    public Product findById(String productId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(productId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            Product product = document.toObject(Product.class);
            if (product != null) {
                product.setId(document.getId());
            }
            return product;
        }
        return null;
    }

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Product> products = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Product product = document.toObject(Product.class);
            if (product != null) {
                product.setId(document.getId());
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> findByCreatedBy(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("createdBy", userId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Product> products = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Product product = document.toObject(Product.class);
            if (product != null) {
                product.setId(document.getId());
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> findByAuctionId(String auctionId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereArrayContains("usedInAuctions", auctionId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Product> products = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Product product = document.toObject(Product.class);
            if (product != null) {
                product.setId(document.getId());
                products.add(product);
            }
        }
        return products;
    }

    public void delete(String productId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(productId)
                .delete();
        result.get();
    }

    public boolean exists(String productId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(productId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return document.exists();
    }
}