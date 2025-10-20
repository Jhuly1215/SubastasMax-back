package SubastasMax.auction_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class BiddingServiceClient {

    private final WebClient webClient;

    public BiddingServiceClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://bidding-service:8090") // URL de tu bidding-service
                .build();
    }

    public Map<String, Object> getHighestBid(String auctionId) {
        return webClient.get()
                .uri("api/bids/{auctionId}/highest", auctionId)
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // bloquea hasta obtener la respuesta
    }
}
