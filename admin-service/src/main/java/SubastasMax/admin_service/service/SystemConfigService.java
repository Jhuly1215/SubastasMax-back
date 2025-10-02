package SubastasMax.admin_service.service;

import SubastasMax.admin_service.model.SystemConfig; // Ensure this import is present
import SubastasMax.admin_service.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    public SystemConfig getSystemConfig() {
        return systemConfigRepository.findById(1L)
                .orElse(new SystemConfig());
    }

    public SystemConfig updateSystemConfig(SystemConfig config) {
        config.setId(1L); // Assuming single config record
        return systemConfigRepository.save(config);
    }
}
