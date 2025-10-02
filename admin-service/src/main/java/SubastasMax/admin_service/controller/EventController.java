package SubastasMax.admin_service.controller;

import SubastasMax.admin_service.model.Bid;
import SubastasMax.admin_service.model.Event;
import SubastasMax.admin_service.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auctions")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAuctions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(eventService.listEvents(category, status, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getAuction(@PathVariable Long id) {
        Optional<Event> event = eventService.listEvents(null, null, null).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/bids")
    @PreAuthorize("hasAnyRole('PARTICIPANTE', 'SUBASTADOR')")
    public ResponseEntity<?> placeBid(@PathVariable Long id, @RequestBody BidRequest bidRequest) {
        try {
            Bid bid = eventService.placeBid(id, bidRequest.getAmount(), bidRequest.getUserId());
            return ResponseEntity.ok(new BidResponse(true, bid, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BidResponse(false, null, e.getMessage()));
        }
    }

    static class BidRequest {
        private double amount;
        private String userId;

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    static class BidResponse {
        private boolean success;
        private Bid bid;
        private String error;

        public BidResponse(boolean success, Bid bid, String error) {
            this.success = success;
            this.bid = bid;
            this.error = error;
        }

        public boolean isSuccess() { return success; }
        public Bid getBid() { return bid; }
        public String getError() { return error; }
    }
}
