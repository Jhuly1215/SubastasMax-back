package SubastasMax.admin_service;

import SubastasMax.admin_service.controller.AdminController;
import SubastasMax.admin_service.controller.AuthController;
import SubastasMax.admin_service.controller.EventController;
import SubastasMax.admin_service.controller.UserController;
import SubastasMax.admin_service.service.EventService;
import SubastasMax.admin_service.service.ReportService;
import SubastasMax.admin_service.service.SystemConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
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
        assertNotNull("AdminController should be initialized", adminController);
        assertNotNull("AuthController should be initialized", authController);
        assertNotNull("EventController should be initialized", eventController);
        assertNotNull("UserController should be initialized", userController);
        assertNotNull("EventService should be initialized", eventService);
        assertNotNull("ReportService should be initialized", reportService);
        assertNotNull("SystemConfigService should be initialized", systemConfigService);
    }
}
