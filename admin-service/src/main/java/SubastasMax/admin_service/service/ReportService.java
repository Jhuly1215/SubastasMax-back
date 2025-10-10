package SubastasMax.admin_service.service;

import SubastasMax.admin_service.model.Bid;
import SubastasMax.admin_service.model.Event;
import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.repository.BidRepository;
import SubastasMax.admin_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ReportService {

    private final BidRepository bidRepository;
    private final EventRepository eventRepository;
    private final FirestoreUserService firestoreUserService;

    public ReportService(BidRepository bidRepository,
                         EventRepository eventRepository,
                         FirestoreUserService firestoreUserService) {
        this.bidRepository = bidRepository;
        this.eventRepository = eventRepository;
        this.firestoreUserService = firestoreUserService;
    }

    /**
     * ✅ Obtiene todas las pujas de un evento específico.
     */
    public List<Map<String, Object>> getBidsByEvent(Long eventId)
            throws ExecutionException, InterruptedException {
        List<Bid> bids = bidRepository.findByEventId(eventId);
        List<Map<String, Object>> bidList = new ArrayList<>();

        for (Bid bid : bids) {
            Map<String, Object> bidData = new HashMap<>();
            bidData.put("userId", bid.getUserId());
            bidData.put("amount", bid.getAmount());
            bidData.put("timestamp", bid.getTimestamp());
            bidData.put("status", bid.getStatus());
            bidList.add(bidData);
        }

        return bidList;
    }

    /**
     * ✅ Genera un reporte detallado de una subasta.
     */
    public Map<String, Object> generateAuctionReport(Long eventId)
            throws ExecutionException, InterruptedException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        List<Bid> bids = bidRepository.findByEventId(eventId);
        double totalRevenue = bids.stream().mapToDouble(Bid::getAmount).sum();
        double highestBid = bids.stream().mapToDouble(Bid::getAmount).max().orElse(0.0);
        int totalBids = bids.size();

        return Map.of(
                "eventId", event.getId(),
                "eventTitle", event.getTitle(),
                "totalBids", totalBids,
                "highestBid", highestBid,
                "totalRevenue", totalRevenue,
                "status", event.getStatus(),
                "participants", event.getParticipants()
        );
    }

    /**
     * ✅ Genera un reporte agregado de todos los usuarios desde Firestore.
     */
    public Map<String, Object> generateUserReport()
            throws ExecutionException, InterruptedException {

        List<User> users = firestoreUserService.getAllUsers();

        long activeUsers = users.stream()
                .filter(u -> "active".equalsIgnoreCase(u.getStatus()))
                .count();

        double avgReputation = users.stream()
                .mapToDouble(User::getReputation)
                .average()
                .orElse(0.0);

        return Map.of(
                "totalUsers", users.size(),
                "activeUsers", activeUsers,
                "averageReputation", avgReputation
        );
    }
}
