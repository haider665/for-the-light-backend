package For.the.light.iot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IotWebhookRateLimiter {

    private static class WindowCounter {
        long windowStartEpochSecond;
        int count;
    }

    private final int maxRequestsPerWindow;
    private final int windowSeconds;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public IotWebhookRateLimiter(
            @Value("${app.iot.rate-limit.max-requests:120}") int maxRequestsPerWindow,
            @Value("${app.iot.rate-limit.window-seconds:60}") int windowSeconds) {
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSeconds = windowSeconds;
    }

    public void check(String deviceId, String clientIp) {
        long now = Instant.now().getEpochSecond();
        String key = deviceId + "|" + clientIp;

        WindowCounter counter = counters.computeIfAbsent(key, ignored -> {
            WindowCounter c = new WindowCounter();
            c.windowStartEpochSecond = now;
            c.count = 0;
            return c;
        });

        synchronized (counter) {
            if ((now - counter.windowStartEpochSecond) >= windowSeconds) {
                counter.windowStartEpochSecond = now;
                counter.count = 0;
            }

            counter.count += 1;
            if (counter.count > maxRequestsPerWindow) {
                throw new RateLimitExceededException("Rate limit exceeded");
            }
        }
    }
}
