package SubastasMax.controller;

import SubastasMax.model.Bid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class BiddingController {

    @MessageMapping("/bid")           // Cliente envía a /app/bid
    @SendTo("/topic/auction")         // Se envía a todos los suscritos
    public Bid placeBid(Bid bid) {
        System.out.println("Nuevo bid en subasta " + bid.getAuctionId() +
                           " por usuario " + bid.getUserId() +
                           " monto: " + bid.getAmount());
        // Aquí podrías guardar en BD o validar la lógica de subasta
        return bid;
    }
}
