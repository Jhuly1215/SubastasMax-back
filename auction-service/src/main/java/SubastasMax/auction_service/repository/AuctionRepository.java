package SubastasMax.auction_service.repository;

import SubastasMax.auction_service.model.Auction_model;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuctionRepository {
    
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "auctions";
    
    /**
     * Crear una nueva subasta
     */
    public Auction_model create(Auction_model auction) throws ExecutionException, InterruptedException {
        auction.setCreatedAt(Timestamp.now());
        auction.setUpdatedAt(Timestamp.now());
        
        if (auction.getId() == null || auction.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            auction.setId(docRef.getId());
        }
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(auction.getId());
        ApiFuture<WriteResult> result = docRef.set(convertToMap( auction));
        result.get();
        
        log.info("Auction created with ID: {}", auction.getId());
        return auction;
    }
    
    /**
     * Actualizar una subasta existente
     */
    public Auction_model update(String auctionId, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(auctionId);
        
        updates.put("updatedAt", Timestamp.now());
        
        ApiFuture<WriteResult> result = docRef.update(updates);
        result.get();
        
        log.info("Auction updated with ID: {}", auctionId);
        return findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found after update"));
    }
    
    /**
     * Obtener subasta por ID
     */
    public Optional<Auction_model> findById(String auctionId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(auctionId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            Auction_model auction = documentToAuction(document);
            return Optional.of(auction);
        }
        
        return Optional.empty();
    }
    
    /**
     * Obtener todas las subastas de un usuario
     */
    public List<Auction_model> findByCreatedBy(String userId) throws ExecutionException, InterruptedException {
        CollectionReference auctions = firestore.collection(COLLECTION_NAME);
        Query query = auctions.whereEqualTo("createdBy", userId);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        return documents.stream()
                .map(this::documentToAuction)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener todas las subastas
     */
    public List<Auction_model> findAll() throws ExecutionException, InterruptedException {
        CollectionReference auctions = firestore.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> querySnapshot = auctions.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        return documents.stream()
                .map(this::documentToAuction)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener subastas por estado
     */
    public List<Auction_model> findByStatus(String status) throws ExecutionException, InterruptedException {
        CollectionReference auctions = firestore.collection(COLLECTION_NAME);
        Query query = auctions.whereEqualTo("status", status);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        return documents.stream()
                .map(this::documentToAuction)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener subastas por categoría
     */
    public List<Auction_model> findByCategory(String category) throws ExecutionException, InterruptedException {
        CollectionReference auctions = firestore.collection(COLLECTION_NAME);
        Query query = auctions.whereEqualTo("category", category);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        return documents.stream()
                .map(this::documentToAuction)
                .collect(Collectors.toList());
    }
    
    /**
     * Eliminar subasta
     */
    public void delete(String auctionId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(auctionId);
        ApiFuture<WriteResult> result = docRef.delete();
        result.get();
        log.info("Auction deleted with ID: {}", auctionId);
    }
    
    /**
     * Convertir Auction a Map para Firestore
     */
    private Map<String, Object> convertToMap(Auction_model auction) {
        Map<String, Object> map = new HashMap<>();
        
        if (auction.getActualEndAt() != null) map.put("actualEndAt", auction.getActualEndAt());
        if (auction.getActualStartAt() != null) map.put("actualStartAt", auction.getActualStartAt());
        if (auction.getApprovedAt() != null) map.put("approvedAt", auction.getApprovedAt());
        if (auction.getApprovedBy() != null) map.put("approvedBy", auction.getApprovedBy());
        map.put("autoExtension", auction.getAutoExtension());
        if (auction.getCategory() != null) map.put("category", auction.getCategory());
        if (auction.getCreatedAt() != null) map.put("createdAt", auction.getCreatedAt());
        if (auction.getCreatedBy() != null) map.put("createdBy", auction.getCreatedBy());
        if (auction.getCurrency() != null) map.put("currency", auction.getCurrency());
        map.put("currentPrice", auction.getCurrentPrice() != null ? auction.getCurrentPrice() : 0.0);
        if (auction.getDescription() != null) map.put("description", auction.getDescription());
        if (auction.getDuration() != null) map.put("duration", auction.getDuration());
        if (auction.getEndAt() != null) map.put("endAt", auction.getEndAt());
        if (auction.getExtensionThreshold() != null) map.put("extensionThreshold", auction.getExtensionThreshold());
        if (auction.getExtensionTime() != null) map.put("extensionTime", auction.getExtensionTime());
        map.put("featured", auction.getFeatured());
        map.put("images", auction.getImages() != null ? auction.getImages() : new ArrayList<>());
        if (auction.getInitialPrice() != null) map.put("initialPrice", auction.getInitialPrice());
        if (auction.getMinimumIncrement() != null) map.put("minimumIncrement", auction.getMinimumIncrement());
        map.put("participants", auction.getParticipants() != null ? auction.getParticipants() : new ArrayList<>());
        if (auction.getReservePrice() != null) map.put("reservePrice", auction.getReservePrice());
        if (auction.getScheduledDate() != null) map.put("scheduledDate", auction.getScheduledDate());
        if (auction.getScheduledTime() != null) map.put("scheduledTime", auction.getScheduledTime());
        if (auction.getSettledAt() != null) map.put("settledAt", auction.getSettledAt());
        if (auction.getStartAt() != null) map.put("startAt", auction.getStartAt());
        if (auction.getStatus() != null) map.put("status", auction.getStatus());
        map.put("tags", auction.getTags() != null ? auction.getTags() : new ArrayList<>());
        if (auction.getTitle() != null) map.put("title", auction.getTitle());
        map.put("totalBids", auction.getTotalBids() != null ? auction.getTotalBids() : 0);
        map.put("uniqueBidders", auction.getUniqueBidders() != null ? auction.getUniqueBidders() : 0);
        if (auction.getUpdatedAt() != null) map.put("updatedAt", auction.getUpdatedAt());
        map.put("viewCount", auction.getViewCount() != null ? auction.getViewCount() : 0);
        map.put("visibility", auction.getVisibility() != null ? auction.getVisibility() : 1);
        if (auction.getWinnerId() != null) map.put("winnerId", auction.getWinnerId());
        if (auction.getWinningBid() != null) map.put("winningBid", auction.getWinningBid());
        
        return map;
    }
    
    /**
     * Convertir DocumentSnapshot a Auction
     */
    private Auction_model documentToAuction(DocumentSnapshot document) {
        return Auction_model.builder()
                .id(document.getId())
                .actualEndAt(document.getTimestamp("actualEndAt"))
                .actualStartAt(document.getTimestamp("actualStartAt"))
                .approvedAt(document.getTimestamp("approvedAt"))
                .approvedBy(document.getString("approvedBy"))
                .autoExtension(document.getBoolean("autoExtension"))
                .category(document.getString("category"))
                .createdAt(document.getTimestamp("createdAt"))
                .createdBy(document.getString("createdBy"))
                .currency(document.getString("currency"))
                .currentPrice(document.getDouble("currentPrice"))
                .description(document.getString("description"))
                .duration(document.getLong("duration"))
                .endAt(document.getTimestamp("endAt"))
                .extensionThreshold(document.getLong("extensionThreshold"))
                .extensionTime(document.getLong("extensionTime"))
                .featured(document.getBoolean("featured"))
                .images((List<String>) document.get("images"))
                .initialPrice(document.getDouble("initialPrice"))
                .minimumIncrement(document.getDouble("minimumIncrement"))
                .participants((List<String>) document.get("participants"))
                .reservePrice(document.getDouble("reservePrice"))
                .scheduledDate(document.getTimestamp("scheduledDate"))
                .scheduledTime(document.getTimestamp("scheduledTime"))
                .settledAt(document.getTimestamp("settledAt"))
                .startAt(document.getTimestamp("startAt"))
                .status(document.getString("status"))
                .tags((List<String>) document.get("tags"))
                .title(document.getString("title"))
                .totalBids(document.getLong("totalBids") != null ? document.getLong("totalBids").intValue() : 0)
                .uniqueBidders(document.getLong("uniqueBidders") != null ? document.getLong("uniqueBidders").intValue() : 0)
                .updatedAt(document.getTimestamp("updatedAt"))
                .viewCount(document.getLong("viewCount") != null ? document.getLong("viewCount").intValue() : 0)
                .visibility(document.getLong("visibility") != null ? document.getLong("visibility").intValue() : 1)
                .winnerId(document.getString("winnerId"))
                .winningBid(document.getDouble("winningBid"))
                .build();
    }
}