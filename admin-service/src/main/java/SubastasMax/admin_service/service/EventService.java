package SubastasMax.admin_service.service;

import SubastasMax.admin_service.controller.AuctionWebSocketHandler;
import SubastasMax.admin_service.model.Bid;
import SubastasMax.admin_service.model.Event;
import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.repository.BidRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final CrudRepository<Event, Long> eventRepository;
    private final BidRepository bidRepository;
    private final FirestoreUserService firestoreUserService;
    private final AuctionWebSocketHandler webSocketHandler;

    public EventService(CrudRepository<Event, Long> eventRepository,
                        BidRepository bidRepository,
                        FirestoreUserService firestoreUserService,
                        AuctionWebSocketHandler webSocketHandler) {
        this.eventRepository = eventRepository;
        this.bidRepository = bidRepository;
        this.firestoreUserService = firestoreUserService;
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

        if (category != null)
            events = events.stream().filter(e -> e.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());

        if (status != null)
            events = events.stream().filter(e -> e.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());

        if (search != null) {
            String s = search.toLowerCase();
            events = events.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(s) || e.getDescription().toLowerCase().contains(s))
                    .collect(Collectors.toList());
        }

        return events;
    }

    public Bid placeBid(Long eventId, double amount, String userId)
            throws ExecutionException, InterruptedException {
        Optional<Event> opt = eventRepository.findById(eventId);
        Event event = opt.orElseThrow(() -> new IllegalArgumentException("Subasta no encontrada"));

        if (!"live".equals(event.getStatus()))
            throw new IllegalArgumentException("La subasta no está activa");

        if (amount <= event.getCurrentBid())
            throw new IllegalArgumentException("La puja debe ser mayor al precio actual");

        // Obtener el usuario desde Firestore
        User user = firestoreUserService.getUserById(userId);
        if (user == null)
            throw new IllegalArgumentException("Usuario no encontrado en Firebase");

        // Actualizar evento
        event.setCurrentBid(amount);
        event.setBidCount(event.getBidCount() + 1);
        event.setParticipants(event.getParticipants() + 1);
        eventRepository.save(event);

        // Crear registro de puja
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
            System.err.println("⚠️ Error enviando actualización WebSocket: " + e.getMessage());
        }

        return savedBid;
    }
}
