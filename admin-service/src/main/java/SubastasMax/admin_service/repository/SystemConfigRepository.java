package SubastasMax.admin_service.repository;

import SubastasMax.admin_service.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
}