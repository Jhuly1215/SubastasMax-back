package SubastasMax.chat_service.service;

import SubastasMax.chat_service.model.ChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToRoom(ChatMessage msg) {
        String dest = "/topic/room." + msg.getRoomId();
        messagingTemplate.convertAndSend(dest, msg);
    }
}
