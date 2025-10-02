package SubastasMax.admin_service.repository;

import SubastasMax.admin_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}