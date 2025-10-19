package Subastasmax.notificationservice.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client stub para auction-service.
 * Usar para enriquecer notificaciones con datos de subasta si el evento no trae suficiente contexto.
 */
@FeignClient(name = "auction-service", url = "${services.auction.url:}", decode404 = true)
public interface AuctionClient {

    @GetMapping("/api/auctions/{auctionId}")
    Object getAuction(@PathVariable("auctionId") String auctionId);

    @GetMapping("/api/auctions/{auctionId}/metrics")
    Object getAuctionMetrics(@PathVariable("auctionId") String auctionId);
}
