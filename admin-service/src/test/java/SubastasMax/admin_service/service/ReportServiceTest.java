package SubastasMax.admin_service.service;

     import SubastasMax.admin_service.model.Bid;
     import SubastasMax.admin_service.model.Event;
     import SubastasMax.admin_service.model.User;
     import SubastasMax.admin_service.repository.BidRepository;
     import SubastasMax.admin_service.repository.EventRepository;
     import SubastasMax.admin_service.repository.UserRepository;
     import org.junit.Test;
     import org.junit.Before;
     import org.mockito.InjectMocks;
     import org.mockito.Mock;
     import org.mockito.MockitoAnnotations;

     import java.util.List;
     import java.util.Map;
     import java.util.Optional;

     import static org.junit.Assert.assertEquals;
     import static org.mockito.Mockito.when;

     public class ReportServiceTest {

         @Before
         public void setUp() {
             MockitoAnnotations.openMocks(this);
         }

         @Mock
         private BidRepository bidRepository;

         @Mock
         private EventRepository eventRepository;

         @Mock
         private UserRepository userRepository;

         @InjectMocks
         private ReportService reportService;

         @Test
         public void testGenerateAuctionReport() {
             Long eventId = 1L;
             Event event = new Event();
             event.setId(eventId);
             event.setTitle("Test Auction");
             Bid bid = new Bid();
             bid.setEventId(eventId);
             bid.setAmount(100.0);
             List<Bid> bids = List.of(bid);

             when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
             when(bidRepository.findByEventId(eventId)).thenReturn(bids);

             Map<String, Object> report = reportService.generateAuctionReport(eventId);
             assertEquals(event, report.get("event"));
             assertEquals(1, report.get("totalBids"));
             assertEquals(100.0, report.get("totalRevenue"));
             assertEquals(100.0, report.get("highestBid"));
         }

         @Test
         public void testGenerateUserReport() {
             User user = new User();
             user.setStatus("active");
             user.setReputation(4.5);
             List<User> users = List.of(user);

             when(userRepository.findAll()).thenReturn(users);

             Map<String, Object> report = reportService.generateUserReport();
             assertEquals(1, report.get("totalUsers"));
             assertEquals(1, report.get("activeUsers"));
             assertEquals(4.5, report.get("averageReputation"));
         }
     }
     