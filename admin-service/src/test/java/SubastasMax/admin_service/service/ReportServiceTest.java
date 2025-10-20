package SubastasMax.admin_service.service;

import SubastasMax.admin_service.model.Bid;
import SubastasMax.admin_service.model.Event;
import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.repository.BidRepository;
import SubastasMax.admin_service.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ReportServiceTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FirestoreUserService firestoreUserService;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateAuctionReport() throws ExecutionException, InterruptedException {
        // Arrange
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Auction");
        event.setStatus("active");
        event.setParticipants(2); // 🔹 Corregido: usa un entero, no una lista

        Bid bid = new Bid();
        bid.setEventId(eventId);
        bid.setAmount(100.0);

        List<Bid> bids = List.of(bid);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(bidRepository.findByEventId(eventId)).thenReturn(bids);

        // Act
        Map<String, Object> report = reportService.generateAuctionReport(eventId);

        // Assert
        assertEquals(event, report.get("event"));
        assertEquals(1, report.get("totalBids"));
        assertEquals(100.0, (double) report.get("totalRevenue"), 0.001);
        assertEquals(100.0, (double) report.get("highestBid"), 0.001);
    }

    @Test
    public void testGenerateUserReport() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User();
        user.setStatus("active");
        user.setReputation(4.5);

        List<User> users = List.of(user);
        when(firestoreUserService.getAllUsers()).thenReturn(users);

        // Act
        Map<String, Object> report = reportService.generateUserReport();

        // Assert
        assertEquals(1, report.get("totalUsers"));
        assertEquals(1L, report.get("activeUsers"));
        assertEquals(4.5, (double) report.get("averageReputation"), 0.001);
    }
}
