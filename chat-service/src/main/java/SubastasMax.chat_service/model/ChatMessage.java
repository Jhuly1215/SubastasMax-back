// SubastasMax.chat_service.model.ChatMessage
package SubastasMax.chat_service.model;

public class ChatMessage {
    private String senderId;
    private String userName;   // 👈 NUEVO
    private String content;
    private String roomId;
    private Long timestamp;    // 👈 opcional, útil para ordenar
    private String userRole;   // 👈 opcional

    public ChatMessage() {}

    public ChatMessage(String senderId, String userName, String content, String roomId, Long timestamp, String userRole) {
        this.senderId = senderId;
        this.userName = userName;
        this.content = content;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.userRole = userRole;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
}
