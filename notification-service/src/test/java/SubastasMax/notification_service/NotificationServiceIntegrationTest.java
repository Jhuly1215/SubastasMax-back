package SubastasMax.notification_service;

// EventPayloadDTO not available as an external class in this test; an inner DTO is defined below.
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración básica: POST /api/events
 */
@SpringBootTest
@AutoConfigureMockMvc
public class NotificationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postAuctionWinnerEvent_shouldReturnAccepted() throws Exception {
        EventPayloadDTO payload = new EventPayloadDTO();
        payload.setType("auction.winner.decided");
        payload.setAuctionId("auc-" + UUID.randomUUID());
        payload.setWinnerUserId("user-winner-1");
        payload.setLosersUserIds(List.of("user-loser-1", "user-loser-2"));
        payload.setAmount(1500L);
        payload.setEventId("evt-" + UUID.randomUUID());

        mockMvc.perform(post("/api/events")
                .header("X-Service-Name", "auction-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isAccepted());
    }

    // Simple DTO used only for this integration test to avoid external dependency
    static class EventPayloadDTO {
        private String type;
        private String auctionId;
        private String winnerUserId;
        private List<String> losersUserIds;
        private Long amount;
        private String eventId;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAuctionId() {
            return auctionId;
        }

        public void setAuctionId(String auctionId) {
            this.auctionId = auctionId;
        }

        public String getWinnerUserId() {
            return winnerUserId;
        }

        public void setWinnerUserId(String winnerUserId) {
            this.winnerUserId = winnerUserId;
        }

        public List<String> getLosersUserIds() {
            return losersUserIds;
        }

        public void setLosersUserIds(List<String> losersUserIds) {
            this.losersUserIds = losersUserIds;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }
    }
}
