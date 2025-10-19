package SubastasMax.notification_service.repository;

import Subastasmax.notificationservice.model.AuctionMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionMetricsRepository extends JpaRepository<AuctionMetrics, Long> {
    Optional<AuctionMetrics> findByAuctionId(String auctionId);
}