package SubastasMax.notification_service.service;

import Subastasmax.notificationservice.model.AuctionMetrics;
import Subastasmax.notificationservice.repository.AuctionMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para manejar métricas de subastas usando Firebase.
 */
@Service
public class MetricsServiceImpl implements MetricsService {

    private static final Logger log = LoggerFactory.getLogger(MetricsServiceImpl.class);
    private final AuctionMetricsRepository repository;

    public MetricsServiceImpl(AuctionMetricsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void recordBid(String auctionId, long amount) {
        Optional<AuctionMetrics> metricsOpt = repository.findByAuctionId(auctionId);
        AuctionMetrics metrics = metricsOpt.orElseGet(() -> new AuctionMetrics(auctionId));
        metrics.incrementBid(amount);
        repository.save(metrics);
        log.info("Bid recorded for auction {}: amount {}", auctionId, amount);
    }

    @Override
    public void recordSettlement(String auctionId, String winnerUserId, long amount) {
        Optional<AuctionMetrics> metricsOpt = repository.findByAuctionId(auctionId);
        AuctionMetrics metrics = metricsOpt.orElseGet(() -> new AuctionMetrics(auctionId));
        metrics.recordSettlement(winnerUserId, amount);
        repository.save(metrics);
        log.info("Settlement recorded for auction {}: winner {}, amount {}", auctionId, winnerUserId, amount);
    }

    @Override
    public void recordCapture(String auctionId, long amount) {
        Optional<AuctionMetrics> metricsOpt = repository.findByAuctionId(auctionId);
        AuctionMetrics metrics = metricsOpt.orElseGet(() -> new AuctionMetrics(auctionId));
        metrics.recordCapture(amount);
        repository.save(metrics);
        log.info("Capture recorded for auction {}: amount {}", auctionId, amount);
    }

    @Override
    public Optional<AuctionMetrics> getMetrics(String auctionId) {
        return repository.findByAuctionId(auctionId);
    }
}