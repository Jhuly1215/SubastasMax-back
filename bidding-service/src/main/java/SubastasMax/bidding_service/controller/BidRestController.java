package SubastasMax.bidding_service.controller;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/bids")
public class BidRestController {

    private final Firestore db;

    public BidRestController(Firestore db) {
        this.db = db;
    }

    @GetMapping("/{auctionId}/highest")
    public ResponseEntity<Map<String, Object>> getHighestBid(@PathVariable String auctionId) throws ExecutionException, InterruptedException {
        DocumentReference auctionDoc = db.collection("bids").document(auctionId);
        DocumentSnapshot snapshot = auctionDoc.get().get();

        if (!snapshot.exists()) {
            return ResponseEntity.notFound().build();
        }

        Double highestBid = snapshot.contains("highestBid") ? snapshot.getDouble("highestBid") : 0.0;
        String winnerUserId = snapshot.contains("winnerUserId") ? snapshot.getString("winnerUserId") : null;

        Map<String, Object> response = Map.of(
                "highestBid", highestBid,
                "winnerUserId", winnerUserId
        );

        return ResponseEntity.ok(response);
    }
}
