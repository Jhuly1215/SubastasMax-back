package SubastasMax.auction_service.service;

import SubastasMax.auction_service.clients.WalletClient;
import SubastasMax.auction_service.model.*;
import SubastasMax.auction_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SettlementService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private WalletClient walletClient;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void settleAuction(Long auctionId) {
        // 1. load auction
        Auction_model auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found " + auctionId));

        if ("CLOSED".equalsIgnoreCase(auction.getStatus())) {
            // already closed
            return;
        }

        // 2. determine highest valid bid
        List<Bid> bids = bidRepository.findByAuctionIdOrderByAmountDesc(auctionId);
        if (bids == null || bids.isEmpty()) {
            auction.setStatus("CLOSED");
            auctionRepository.save(auction);
            audit("auction-service", "auction.closed.no-bids", "{\"auctionId\":" + auctionId + "}");
            return;
        }

        Bid winnerBid = bids.get(0);
        Long winnerId = winnerBid.getBidderId();
        Double winningAmount = winnerBid.getAmount();

        // persist winner
        auction.setWinnerId(winnerId);
        auction.setWinningAmount(winningAmount);
        auction.setStatus("CLOSED");
        auctionRepository.save(auction);

        audit("auction-service", "auction.winner.decided",
                "{\"auctionId\":" + auctionId + ",\"winnerId\":" + winnerId + ",\"winningBidId\":" + winnerBid.getId() + "}");

        // 3. capture winner funds
        try {
            ResponseEntity<String> resp = walletClient.capture(winnerId, auctionId, winningAmount, "auction:" + auctionId);
            audit("wallet-service", "wallet.capture.request", "{\"auctionId\":" + auctionId + ",\"userId\":" + winnerId + "}");
            if (resp.getStatusCode().is2xxSuccessful()) {
                audit("wallet-service", "wallet.captured", resp.getBody() == null ? "{}" : resp.getBody());
                // create payment_accredited notification
                Notification n = new Notification();
                n.setUserId(winnerId);
                n.setType("payment_accredited");
                n.setTitle("Pago acreditado");
                n.setBody("Se acreditó el pago por la subasta " + auctionId + " por " + winningAmount);
                n.setMetadata("{\"auctionId\":" + auctionId + "}");
                notificationService.createNotification(n);
            } else {
                // capture failed -- create alert + notification for admin and mark auction as payment_failed
                auction.setStatus("PAYMENT_FAILED");
                auctionRepository.save(auction);

                Notification n = new Notification();
                n.setUserId(winnerId);
                n.setType("payment_failed");
                n.setTitle("Pago no acreditado");
                n.setBody("No fue posible capturar fondos para la subasta " + auctionId + ". Estado: " + resp.getStatusCode());
                n.setMetadata("{\"auctionId\":" + auctionId + "}");
                notificationService.createNotification(n);
            }
        } catch (Exception ex) {
            audit("wallet-service", "wallet.capture.error", "{\"auctionId\":" + auctionId + ",\"error\":\"" + ex.getMessage() + "\"}");
            auction.setStatus("PAYMENT_ERROR");
            auctionRepository.save(auction);
            Notification n = new Notification();
            n.setUserId(winnerId);
            n.setType("payment_error");
            n.setTitle("Error en pago");
            n.setBody("Ocurrió un error al procesar el pago para la subasta " + auctionId);
            n.setMetadata("{\"auctionId\":" + auctionId + "}");
            notificationService.createNotification(n);
        }

        // 4. release frozen funds for losers (if any)
        for (int i = 1; i < bids.size(); i++) {
            Bid loserBid = bids.get(i);
            Long loserId = loserBid.getBidderId();
            Double amount = loserBid.getAmount();
            try {
                ResponseEntity<String> resp = walletClient.release(loserId, auctionId, amount, "auction_release:" + auctionId);
                audit("wallet-service", "wallet.released", "{\"auctionId\":" + auctionId + ",\"userId\":" + loserId + "}");
                Notification n = new Notification();
                n.setUserId(loserId);
                n.setType("funds_released");
                n.setTitle("Fondos liberados");
                n.setBody("Se liberaron tus fondos de " + amount + " para la subasta " + auctionId);
                n.setMetadata("{\"auctionId\":" + auctionId + "}");
                notificationService.createNotification(n);
            } catch (Exception ex) {
                audit("wallet-service", "wallet.release.error", "{\"auctionId\":" + auctionId + ",\"userId\":" + loserId + ",\"error\":\"" + ex.getMessage() + "\"}");
                // continue; admin/auditoría puede rastrear
            }
            // also inform outbid (could be earlier when new bid arrived; we still create a "outbid" notification)
            Notification outbid = new Notification();
            outbid.setUserId(loserId);
            outbid.setType("outbid");
            outbid.setTitle("Fuiste superado");
            outbid.setBody("Tu puja en la subasta " + auctionId + " fue superada.");
            outbid.setMetadata("{\"auctionId\":" + auctionId + "}");
            notificationService.createNotification(outbid);
        }

        // create winner notification (inform that ganó)
        Notification win = new Notification();
        win.setUserId(winnerId);
        win.setType("won");
        win.setTitle("Ganaste la subasta");
        win.setBody("Felicitaciones, ganaste la subasta " + auctionId + " con " + winningAmount);
        win.setMetadata("{\"auctionId\":" + auctionId + ",\"bidId\":" + winnerBid.getId() + "}");
        notificationService.createNotification(win);
    }

    private void audit(String source, String eventType, String payload) {
        AuditLog a = new AuditLog();
        a.setSource(source);
        a.setEventType(eventType);
        a.setPayload(payload);
        auditLogRepository.save(a);
    }
}