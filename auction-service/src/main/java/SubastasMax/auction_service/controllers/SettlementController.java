package SubastasMax.auction_service.controllers;

import SubastasMax.auction_service.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/auctions")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    // Endpoint interno (protegido por gateway/auth) para disparar settlement
    @PostMapping("/{auctionId}/settle")
    public void settle(@PathVariable Long auctionId) {
        settlementService.settleAuction(auctionId);
    }
}