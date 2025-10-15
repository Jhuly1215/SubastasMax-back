package SubastasMax.chat_service.controller;

import SubastasMax.chat_service.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // Este método escucha en /app/chat.send (definido en el HTML)
    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        System.out.println("📩 Mensaje recibido de: " + message.getSenderId() + 
                           " en sala: " + message.getRoomId() + 
                           " → " + message.getContent());
        return message;
    }
}
