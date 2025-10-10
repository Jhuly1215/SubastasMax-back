package SubastasMax.admin_service.repository;

import SubastasMax.admin_service.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByEventId(Long eventId);
}