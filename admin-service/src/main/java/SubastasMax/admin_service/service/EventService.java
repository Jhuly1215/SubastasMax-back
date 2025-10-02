package SubastasMax.admin_service.service;

import SubastasMax.admin_service.controller.AuctionWebSocketHandler;
import SubastasMax.admin_service.model.Bid;
import SubastasMax.admin_service.model.Event;
import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.repository.BidRepository;
import SubastasMax.admin_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final CrudRepository<Event, Long> eventRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final AuctionWebSocketHandler webSocketHandler;

    public EventService(CrudRepository<Event, Long> eventRepository, BidRepository bidRepository, 
                       UserRepository userRepository, AuctionWebSocketHandler webSocketHandler) {
        this.eventRepository = eventRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.webSocketHandler = webSocketHandler;
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event event) {
        event.setId(id);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> listEvents(String category, String status, String search) {
        Iterable<Event> iterable = eventRepository.findAll();
        List<Event> events = new ArrayList<>();
        iterable.forEach(events::add);
        if (category != null) {
            events = events.stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        if (status != null) {
            events = events.stream()
                    .filter(e -> e.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        if (search != null) {
            String searchLower = search.toLowerCase();
            events = events.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(searchLower) ||
                            e.getDescription().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        return events;
    }

    public Bid placeBid(Long eventId, double amount, String userId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        Event event = opt.orElseThrow(() -> new IllegalArgumentException("Subasta no encontrada"));
        if (!event.getStatus().equals("live")) {
            throw new IllegalArgumentException("La subasta no está activa");
        }
        if (amount <= event.getCurrentBid()) {
            throw new IllegalArgumentException("La puja debe ser mayor al precio actual");
        }
        event.setCurrentBid(amount);
        event.setBidCount(event.getBidCount() + 1);
        event.setParticipants(event.getParticipants() + 1); // Increment participants
        eventRepository.save(event);

        Long userIdLong;
        try {
            userIdLong = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Usuario id inválido: " + userId);
        }
        User user = userRepository.findById(userIdLong)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Bid bid = new Bid();
        bid.setEventId(eventId);
        bid.setUserId(userId);
        bid.setUserName(user.getName());
        bid.setAmount(amount);
        bid.setTimestamp(LocalDateTime.now());
        bid.setStatus("accepted");
        Bid savedBid = bidRepository.save(bid);

        try {
            webSocketHandler.broadcastBidUpdate(eventId.toString(), savedBid);
            webSocketHandler.broadcastParticipantUpdate(eventId.toString(), event.getParticipants());
        } catch (IOException e) {
            // Log error but don't fail the bid
            System.err.println("Error broadcasting WebSocket update: " + e.getMessage());
        }

        return savedBid;
    }
}
