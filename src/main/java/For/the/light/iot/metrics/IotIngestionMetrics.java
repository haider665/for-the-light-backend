package For.the.light.iot.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class IotIngestionMetrics {

    private final Counter acceptedCounter;
    private final Counter duplicateCounter;
    private final Counter authFailureCounter;
    private final Counter validationFailureCounter;
    private final Counter rateLimitedCounter;
    private final Counter unhandledFailureCounter;
    private final Timer latencyTimer;

    public IotIngestionMetrics(MeterRegistry meterRegistry) {
        this.acceptedCounter = Counter.builder("iot.webhook.accepted.total").register(meterRegistry);
        this.duplicateCounter = Counter.builder("iot.webhook.duplicate.total").register(meterRegistry);
        this.authFailureCounter = Counter.builder("iot.webhook.auth_failure.total").register(meterRegistry);
        this.validationFailureCounter = Counter.builder("iot.webhook.validation_failure.total").register(meterRegistry);
        this.rateLimitedCounter = Counter.builder("iot.webhook.rate_limited.total").register(meterRegistry);
        this.unhandledFailureCounter = Counter.builder("iot.webhook.unhandled_failure.total").register(meterRegistry);
        this.latencyTimer = Timer.builder("iot.webhook.latency").register(meterRegistry);
    }

    public void recordAccepted(boolean duplicate) {
        acceptedCounter.increment();
        if (duplicate) {
            duplicateCounter.increment();
        }
    }

    public void recordAuthFailure() {
        authFailureCounter.increment();
    }

    public void recordValidationFailure() {
        validationFailureCounter.increment();
    }

    public void recordRateLimited() {
        rateLimitedCounter.increment();
    }

    public void recordUnhandledFailure() {
        unhandledFailureCounter.increment();
    }

    public Timer.Sample start() {
        return Timer.start();
    }

    public void stop(Timer.Sample sample) {
        sample.stop(latencyTimer);
    }
}
