package SubastasMax.chat_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import SubastasMax.chat_service.service.ChatService;
import SubastasMax.chat_service.model.ChatMessage;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendFromServer(@RequestBody ChatMessage msg) {
        if (msg.getTimestamp() == null) {
            msg.setTimestamp(System.currentTimeMillis());
        }
        chatService.sendToRoom(msg);
        return ResponseEntity.ok().build();
    }
}
