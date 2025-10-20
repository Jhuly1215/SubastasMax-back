package SubastasMax.wallet_service.repository;

import SubastasMax.wallet_service.model.Transaction;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Repository
public class TransactionRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_TRANSACTIONS = "transactions";

    /* -----------------------------
     * CREATE / UPDATE / GET BY ID
     * ----------------------------- */
    public String createTransaction(Transaction transaction) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_TRANSACTIONS).document();

        transaction.setTransactionId(docRef.getId());
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(new Date());
        }

        Map<String, Object> data = transactionToMap(transaction);
        ApiFuture<WriteResult> result = docRef.set(data);
        result.get();

        return docRef.getId();
    }

    public Transaction getTransaction(String transactionId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_TRANSACTIONS).document(transactionId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) return null;
        return mapToTransaction(document);
    }

    public void updateTransaction(String transactionId, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_TRANSACTIONS).document(transactionId);
        ApiFuture<WriteResult> result = docRef.update(updates);
        result.get();
    }

    /* ---------------
     * LIST QUERIES
     * --------------- */

    /** Recibidas por el usuario (toUserId = userId) */
    public List<Transaction> getTransactionsByToUser(String userId) throws ExecutionException, InterruptedException {
        CollectionReference col = firestore.collection(COLLECTION_TRANSACTIONS);
        Query q = col.whereEqualTo("toUserId", userId); // sin orderBy: evitamos índice compuesto
        List<Transaction> list = runQuery(q);
        sortByCreatedAtDesc(list);
        return list;
    }

    /** Enviadas por el usuario (fromUserId = userId) */
    public List<Transaction> getTransactionsByFromUser(String userId) throws ExecutionException, InterruptedException {
        CollectionReference col = firestore.collection(COLLECTION_TRANSACTIONS);
        Query q = col.whereEqualTo("fromUserId", userId); // sin orderBy
        List<Transaction> list = runQuery(q);
        sortByCreatedAtDesc(list);
        return list;
    }

    /** Todas (enviadas y recibidas) para un usuario */
    public List<Transaction> getAllTransactionsByUser(String userId) throws ExecutionException, InterruptedException {
        List<Transaction> sent     = getTransactionsByFromUser(userId);
        List<Transaction> received = getTransactionsByToUser(userId);

        // Unir y deduplicar por transactionId
        Map<String, Transaction> map = new LinkedHashMap<>();
        for (Transaction t : sent)     { if (t.getTransactionId() != null) map.put(t.getTransactionId(), t); }
        for (Transaction t : received) { if (t.getTransactionId() != null) map.put(t.getTransactionId(), t); }

        List<Transaction> all = new ArrayList<>(map.values());
        sortByCreatedAtDesc(all);
        return all;
    }

    /** Todas las transacciones entre dos usuarios (A→B y B→A) */
    public List<Transaction> getTransactionsBetweenUsers(String userId1, String userId2) throws ExecutionException, InterruptedException {
        CollectionReference col = firestore.collection(COLLECTION_TRANSACTIONS);

        // A -> B
        Query q1 = col.whereEqualTo("fromUserId", userId1)
                      .whereEqualTo("toUserId", userId2);
        // B -> A
        Query q2 = col.whereEqualTo("fromUserId", userId2)
                      .whereEqualTo("toUserId", userId1);

        List<Transaction> list1 = runQuery(q1);
        List<Transaction> list2 = runQuery(q2);

        Map<String, Transaction> map = new LinkedHashMap<>();
        for (Transaction t : list1) { if (t.getTransactionId() != null) map.put(t.getTransactionId(), t); }
        for (Transaction t : list2) { if (t.getTransactionId() != null) map.put(t.getTransactionId(), t); }

        List<Transaction> all = new ArrayList<>(map.values());
        sortByCreatedAtDesc(all);
        return all;
    }

    /* -------------------
     * INTERNAL UTILITIES
     * ------------------- */

    private List<Transaction> runQuery(Query q) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = q.get();
        QuerySnapshot qs = future.get();

        List<Transaction> out = new ArrayList<>();
        for (DocumentSnapshot doc : qs.getDocuments()) {
            Transaction t = mapToTransaction(doc);
            if (t != null) out.add(t);
        }
        return out;
    }

    private void sortByCreatedAtDesc(List<Transaction> list) {
        list.sort((a, b) -> {
            Date da = a.getCreatedAt() != null ? a.getCreatedAt() : new Date(0);
            Date db = b.getCreatedAt() != null ? b.getCreatedAt() : new Date(0);
            return db.compareTo(da);
        });
    }

    private Map<String, Object> transactionToMap(Transaction transaction) {
        Map<String, Object> data = new HashMap<>();
        data.put("transactionId", transaction.getTransactionId());
        data.put("fromUserId", transaction.getFromUserId());
        data.put("toUserId", transaction.getToUserId());
        data.put("amount", transaction.getAmount());          // String en tu modelo
        data.put("currency", transaction.getCurrency());
        data.put("type", transaction.getType());
        data.put("status", transaction.getStatus());
        data.put("requestId", transaction.getRequestId());
        if (transaction.getDescription() != null) data.put("description", transaction.getDescription());
        if (transaction.getBalanceBefore() != null) data.put("balanceBefore", transaction.getBalanceBefore().toMap());
        if (transaction.getBalanceAfter()  != null) data.put("balanceAfter",  transaction.getBalanceAfter().toMap());
        if (transaction.getCreatedAt()     != null) data.put("createdAt",     transaction.getCreatedAt());
        if (transaction.getCompletedAt()   != null) data.put("completedAt",   transaction.getCompletedAt());
        if (transaction.getFailedAt()      != null) data.put("failedAt",      transaction.getFailedAt());
        if (transaction.getErrorMessage()  != null) data.put("errorMessage",  transaction.getErrorMessage());
        if (transaction.getMetadata()      != null) data.put("metadata",      transaction.getMetadata().toMap());
        return data;
    }

    @SuppressWarnings("unchecked")
    private Transaction mapToTransaction(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        if (data == null) return null;

        return Transaction.builder()
            .transactionId(document.getId())
            .fromUserId((String) data.get("fromUserId"))
            .toUserId((String) data.get("toUserId"))
            .amount((String) data.get("amount"))
            .currency((String) data.get("currency"))
            .type((String) data.get("type"))
            .status((String) data.get("status"))
            .description((String) data.get("description"))
            .requestId((String) data.get("requestId"))
            .balanceBefore(Transaction.Balance.fromMap((Map<String, Object>) data.get("balanceBefore")))
            .balanceAfter(Transaction.Balance.fromMap((Map<String, Object>) data.get("balanceAfter")))
            .createdAt(readDate(data.get("createdAt")))
            .completedAt(readDate(data.get("completedAt")))
            .failedAt(readDate(data.get("failedAt")))
            .errorMessage((String) data.get("errorMessage"))
            .metadata(Transaction.TransactionMetadata.fromMap((Map<String, Object>) data.get("metadata")))
            .build();
    }

    private Date readDate(Object v) {
        if (v instanceof Date) return (Date) v;
        if (v instanceof com.google.cloud.Timestamp ts) return ts.toDate();
        return null;
    }
}
