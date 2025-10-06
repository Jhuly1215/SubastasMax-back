package SubastasMax.wallet_service.repository;

import SubastasMax.wallet_service.model.Wallet;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class WalletRepository {

    private static final String COLLECTION_NAME = "wallets";

    @Autowired
    private Firestore firestore;

    public Wallet save(Wallet wallet) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(wallet.getUserId());
        ApiFuture<WriteResult> result = docRef.set(wallet);
        result.get();
        return wallet;
    }

    public Wallet findByUserId(String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(Wallet.class);
        }
        return null;
    }

    public Wallet update(String userId, Wallet wallet) throws ExecutionException, InterruptedException {
        wallet.setUpdatedAt(System.currentTimeMillis());
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);
        ApiFuture<WriteResult> result = docRef.set(wallet);
        result.get();
        return wallet;
    }
}