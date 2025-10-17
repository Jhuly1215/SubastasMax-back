package com.subastasmax.auction_service.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

@Component
public class WalletClient {
    private final RestTemplate rest = new RestTemplate();

    @Value("${wallet.service.url:http://wallet-service:8080}")
    private String walletBaseUrl;

    public ResponseEntity<String> capture(Long userId, Long auctionId, Double amount, String reference) {
        String url = walletBaseUrl + "/wallets/" + userId + "/capture";
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("reference", reference);
        body.put("auctionId", auctionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, headers);
        return rest.postForEntity(url, req, String.class);
    }

    public ResponseEntity<String> release(Long userId, Long auctionId, Double amount, String reference) {
        String url = walletBaseUrl + "/wallets/" + userId + "/release";
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("reference", reference);
        body.put("auctionId", auctionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, headers);
        return rest.postForEntity(url, req, String.class);
    }
}