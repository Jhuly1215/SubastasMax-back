package SubastasMax.admin_service;

import SubastasMax.admin_service.controller.AdminController;
import SubastasMax.admin_service.controller.AuthController;
import SubastasMax.admin_service.controller.EventController;
import SubastasMax.admin_service.controller.UserController;
import SubastasMax.admin_service.service.EventService;
import SubastasMax.admin_service.service.ReportService;
import SubastasMax.admin_service.service.SystemConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AdminServiceApplicationTests {

    @Autowired
    private AdminController adminController;

    @Autowired
    private AuthController authController;

    @Autowired
    private EventController eventController;

    @Autowired
    private UserController userController;

    @Autowired
    private EventService eventService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Test
    public void contextLoads() {
        assertNotNull(adminController, "AdminController should be initialized");
        assertNotNull(authController, "AuthController should be initialized");
        assertNotNull(eventController, "EventController should be initialized");
        assertNotNull(userController, "UserController should be initialized");
        assertNotNull(eventService, "EventService should be initialized");
        assertNotNull(reportService, "ReportService should be initialized");
        assertNotNull(systemConfigService, "SystemConfigService should be initialized");
    }
}

