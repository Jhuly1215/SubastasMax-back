package SubastasMax.auction_service.repository;

import SubastasMax.auction_service.model.Bid;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class BidRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "bids";

    public String save(Bid bid) throws ExecutionException, InterruptedException {
        Map<String, Object> bidData = new HashMap<>();
        bidData.put("amount", bid.getAmount());
        bidData.put("auctionId", bid.getAuctionId());
        bidData.put("userId", bid.getUserId());
        bidData.put("userName", bid.getUserName());
        bidData.put("status", bid.getStatus());
        bidData.put("timestamp", bid.getTimestamp() != null ? bid.getTimestamp() : Timestamp.now());

        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(bidData);
        DocumentReference docRef = future.get();
        return docRef.getId();
    }

    public Bid findById(String bidId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(bidId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return documentToBid(document);
        }
        return null;
    }

    public List<Bid> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Bid> bids = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            bids.add(documentToBid(document));
        }
        return bids;
    }

    public List<Bid> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Bid> bids = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            bids.add(documentToBid(document));
        }
        return bids;
    }

    public List<Bid> findByAuctionId(String auctionId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("auctionId", auctionId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<Bid> bids = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            bids.add(documentToBid(document));
        }
        return bids;
    }

    public void update(String bidId, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(bidId);
        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }

    public void delete(String bidId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(bidId).delete();
        future.get();
    }

    private Bid documentToBid(DocumentSnapshot document) {
        Bid bid = new Bid();
        bid.setId(document.getId());
        bid.setAmount(document.getString("amount"));
        bid.setAuctionId(document.getString("auctionId"));
        bid.setUserId(document.getString("userId"));
        bid.setUserName(document.getString("userName"));
        bid.setStatus(document.getString("status"));
        bid.setTimestamp(document.getTimestamp("timestamp"));
        return bid;
    }
}