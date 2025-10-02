package SubastasMax.admin_service.repository;

import SubastasMax.admin_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}