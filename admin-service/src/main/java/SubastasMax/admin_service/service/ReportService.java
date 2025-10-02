package SubastasMax.admin_service.service;

import SubastasMax.admin_service.model.Bid;
import SubastasMax.admin_service.model.Event;
import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.repository.BidRepository;
import SubastasMax.admin_service.repository.EventRepository;
import SubastasMax.admin_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final BidRepository bidRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReportService(BidRepository bidRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Bid> getBidsByEvent(Long eventId) {
        return bidRepository.findByEventId(eventId);
    }

    public Map<String, Object> generateAuctionReport(Long eventId) {
        List<Bid> bids = bidRepository.findByEventId(eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        double totalRevenue = bids.stream().mapToDouble(Bid::getAmount).sum();
        return Map.of(
                "event", event,
                "totalBids", bids.size(),
                "totalRevenue", totalRevenue,
                "highestBid", bids.stream().mapToDouble(Bid::getAmount).max().orElse(0.0)
        );
    }

    public Map<String, Object> generateUserReport() {
        List<User> users = userRepository.findAll();
        long activeUsers = users.stream().filter(u -> u.getStatus().equals("active")).count();
        return Map.of(
                "totalUsers", users.size(),
                "activeUsers", activeUsers,
                "averageReputation", users.stream().mapToDouble(User::getReputation).average().orElse(0.0)
        );
    }
}
