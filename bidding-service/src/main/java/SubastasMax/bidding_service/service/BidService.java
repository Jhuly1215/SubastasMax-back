package SubastasMax.bidding_service.service;

import SubastasMax.bidding_service.model.Bid;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class BidService {

    private final Firestore db;

    // 🔹 Inyección de Firestore desde FirebaseConfig
    public BidService(Firestore firestore) {
        this.db = firestore;
    }

    public Bid saveBid(Bid bid) throws ExecutionException, InterruptedException {
        DocumentReference auctionDoc = db.collection("bids").document(bid.getAuctionId());

        // 🔹 Si el documento de la subasta no existe, crearlo con valores iniciales
        if (!auctionDoc.get().get().exists()) {
            Map<String, Object> initialData = new HashMap<>();
            initialData.put("highestBid", 0);
            initialData.put("winnerUserId", null);

            auctionDoc.set(initialData).get();
        }

        // Guardar la puja dentro de la subcolección "items"
        CollectionReference bidsRef = auctionDoc.collection("items");
        bidsRef.add(bid).get();

        // Actualizar el bid más alto
        updateHighestBid(bid);

        return bid;
    }

    private void updateHighestBid(Bid bid) throws ExecutionException, InterruptedException {
        DocumentReference auctionDoc = db.collection("bids").document(bid.getAuctionId());

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(auctionDoc).get();

            Double currentHighest = snapshot.contains("highestBid")
                    ? snapshot.getDouble("highestBid")
                    : 0.0;

            if (bid.getAmount() > currentHighest) {
                transaction.update(auctionDoc,
                        "highestBid", bid.getAmount(),
                        "winnerUserId", bid.getUserId());
                System.out.println("🏆 Nuevo highest bid: " + bid.getAmount() +
                        " por usuario " + bid.getUserId());
            }

            return null;
        }).get();
    }
}
