package SubastasMax.chat_service.controller;

import SubastasMax.chat_service.model.ChatMessage;
import SubastasMax.chat_service.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendFromServer(@RequestBody ChatMessage msg) {
        chatService.sendToRoom(msg);
        return ResponseEntity.ok().build();
    }
}
