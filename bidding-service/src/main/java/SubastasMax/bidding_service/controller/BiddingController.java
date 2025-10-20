package SubastasMax.bidding_service.controller;

import SubastasMax.bidding_service.model.Bid;
import SubastasMax.bidding_service.service.BidService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class BiddingController {

    private final BidService bidService;

    public BiddingController(BidService bidService) {
        this.bidService = bidService;
    }

    @MessageMapping("/bid")
    @SendTo("/topic/auction")
    public Bid placeBid(Bid bid) {
        try {
            Bid savedBid = bidService.saveBid(bid);
            System.out.println("📤 Bid guardado en Firestore: " + savedBid.getAmount());
            return savedBid;
        } catch (Exception e) {
            System.err.println("❌ Error guardando bid: " + e.getMessage());
            e.printStackTrace();
            return bid;
        }
    }
    
}
