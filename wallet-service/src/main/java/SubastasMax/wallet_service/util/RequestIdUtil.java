package SubastasMax.wallet_service.util;

import java.util.UUID;

public class RequestIdUtil {
    public static String generateRequestId(String userId) {
    long timestamp = System.currentTimeMillis();
    String randomPart = UUID.randomUUID().toString().substring(0, 6);
    return "req-" + userId + "-" + timestamp + "-" + randomPart;
}
}
