// SubastasMax.chat_service.controller.ChatController
package SubastasMax.chat_service.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import SubastasMax.chat_service.model.ChatMessage;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;

    public ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    // Cliente envía a: /app/chat/{roomId}
    @MessageMapping("/chat/{roomId}")
    public void sendToRoom(@DestinationVariable String roomId, @Payload ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(System.currentTimeMillis());
        }
        message.setRoomId(roomId); // por si no viene
        System.out.println("📩 " + message.getSenderId() + " (" + message.getUserName() + ") → sala " + roomId + ": " + message.getContent());
        template.convertAndSend("/topic/room." + roomId, message);
    }
}
