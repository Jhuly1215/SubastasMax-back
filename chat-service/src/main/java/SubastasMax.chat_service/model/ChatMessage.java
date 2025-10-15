package SubastasMax.chat_service.model;

public class ChatMessage {
    private String senderId;
    private String content;
    private String roomId;

    public ChatMessage() {}

    public ChatMessage(String senderId, String content, String roomId) {
        this.senderId = senderId;
        this.content = content;
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
