package SubastasMax.wallet_service.repository;

import SubastasMax.wallet_service.model.Transaction;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class TransactionRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_WALLETS = "transactions";
    private static final String SUBCOLLECTION_TX = "tx";

    public String createTransaction(Transaction transaction) throws ExecutionException, InterruptedException {
        String userId = transaction.getUserId();
        DocumentReference docRef = firestore
            .collection(COLLECTION_WALLETS)
            .document(userId)
            .collection(SUBCOLLECTION_TX)
            .document();
        
        transaction.setTransactionId(docRef.getId());
        transaction.setCreatedAt(new Date());
        
        Map<String, Object> data = transactionToMap(transaction);
        ApiFuture<WriteResult> result = docRef.set(data);
        result.get();
        
        return docRef.getId();
    }

    public Transaction getTransaction(String userId, String transactionId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore
            .collection(COLLECTION_WALLETS)
            .document(userId)
            .collection(SUBCOLLECTION_TX)
            .document(transactionId);
        
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (!document.exists()) {
            return null;
        }
        
        return mapToTransaction(document);
    }

    public void updateTransaction(String userId, String transactionId, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore
            .collection(COLLECTION_WALLETS)
            .document(userId)
            .collection(SUBCOLLECTION_TX)
            .document(transactionId);
        
        ApiFuture<WriteResult> result = docRef.update(updates);
        result.get();
    }

    public java.util.List<Transaction> getAllTransactionsByUser(String userId) throws ExecutionException, InterruptedException {
        CollectionReference txCollection = firestore
            .collection(COLLECTION_WALLETS)
            .document(userId)
            .collection(SUBCOLLECTION_TX);
        
        ApiFuture<QuerySnapshot> future = txCollection.orderBy("createdAt", Query.Direction.DESCENDING).get();
        QuerySnapshot querySnapshot = future.get();
        
        java.util.List<Transaction> transactions = new java.util.ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Transaction transaction = mapToTransaction(document);
            if (transaction != null) {
                transaction.setUserId(userId);
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }

    private Map<String, Object> transactionToMap(Transaction transaction) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("amount", transaction.getAmount());
        data.put("currency", transaction.getCurrency());
        data.put("type", transaction.getType());
        data.put("status", transaction.getStatus());
        data.put("requestId", transaction.getRequestId());
        
        if (transaction.getDescription() != null) {
            data.put("description", transaction.getDescription());
        }
        
        if (transaction.getBalanceBefore() != null) {
            data.put("balanceBefore", transaction.getBalanceBefore().toMap());
        }
        
        if (transaction.getBalanceAfter() != null) {
            data.put("balanceAfter", transaction.getBalanceAfter().toMap());
        }
        
        if (transaction.getCreatedAt() != null) {
            data.put("createdAt", transaction.getCreatedAt());
        }
        
        if (transaction.getCompletedAt() != null) {
            data.put("completedAt", transaction.getCompletedAt());
        }
        
        if (transaction.getFailedAt() != null) {
            data.put("failedAt", transaction.getFailedAt());
        }
        
        if (transaction.getErrorMessage() != null) {
            data.put("errorMessage", transaction.getErrorMessage());
        }
        
        if (transaction.getMetadata() != null) {
            data.put("metadata", transaction.getMetadata().toMap());
        }
        
        return data;
    }

    @SuppressWarnings("unchecked")
    private Transaction mapToTransaction(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        if (data == null) return null;
        
        return Transaction.builder()
            .transactionId(document.getId())
            .amount((String) data.get("amount"))
            .currency((String) data.get("currency"))
            .type((String) data.get("type"))
            .status((String) data.get("status"))
            .description((String) data.get("description"))
            .requestId((String) data.get("requestId"))
            .balanceBefore(Transaction.Balance.fromMap((Map<String, Object>) data.get("balanceBefore")))
            .balanceAfter(Transaction.Balance.fromMap((Map<String, Object>) data.get("balanceAfter")))
            .createdAt(data.get("createdAt") instanceof Date ? (Date) data.get("createdAt") : 
                      data.get("createdAt") instanceof com.google.cloud.Timestamp ? 
                      ((com.google.cloud.Timestamp) data.get("createdAt")).toDate() : null)
            .completedAt(data.get("completedAt") instanceof Date ? (Date) data.get("completedAt") : 
                        data.get("completedAt") instanceof com.google.cloud.Timestamp ? 
                        ((com.google.cloud.Timestamp) data.get("completedAt")).toDate() : null)
            .failedAt(data.get("failedAt") instanceof Date ? (Date) data.get("failedAt") : 
                     data.get("failedAt") instanceof com.google.cloud.Timestamp ? 
                     ((com.google.cloud.Timestamp) data.get("failedAt")).toDate() : null)
            .errorMessage((String) data.get("errorMessage"))
            .metadata(Transaction.TransactionMetadata.fromMap((Map<String, Object>) data.get("metadata")))
            .build();
    }
}