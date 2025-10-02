package SubastasMax.admin_service.controller;

import SubastasMax.admin_service.model.Bid;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String auctionId = getAuctionId(session);
        sessions.put(auctionId + "_" + session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String auctionId = getAuctionId(session);
        sessions.remove(auctionId + "_" + session.getId());
    }

    public void broadcastBidUpdate(String auctionId, Bid bid) throws IOException {
        String message = String.format(
                "{\"type\":\"bid_update\",\"auctionId\":\"%s\",\"bid\":{\"id\":\"%s\",\"amount\":%f,\"userName\":\"%s\",\"timestamp\":\"%s\"}}",
                auctionId, bid.getId(), bid.getAmount(), bid.getUserName(), bid.getTimestamp());
        broadcast(auctionId, message);
    }

    public void broadcastParticipantUpdate(String auctionId, int count) throws IOException {
        String message = String.format(
                "{\"type\":\"participants_update\",\"auctionId\":\"%s\",\"count\":%d}",
                auctionId, count);
        broadcast(auctionId, message);
    }

    private void broadcast(String auctionId, String message) throws IOException {
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen() && getAuctionId(session).equals(auctionId)) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    private String getAuctionId(WebSocketSession session) {
        return session.getUri().getPath().split("/")[3];
    }
}
