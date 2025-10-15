package SubastasMax.admin_service.controller;

import SubastasMax.admin_service.model.SystemConfig;
import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.service.FirestoreUserService;
import SubastasMax.admin_service.service.ReportService;
import SubastasMax.admin_service.service.SystemConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final SystemConfigService systemConfigService;
    private final ReportService reportService;
    private final FirestoreUserService firestoreUserService;

    public AdminController(SystemConfigService systemConfigService,
                           ReportService reportService,
                           FirestoreUserService firestoreUserService) {
        this.systemConfigService = systemConfigService;
        this.reportService = reportService;
        this.firestoreUserService = firestoreUserService;
    }

    // ✅ Obtener configuración del sistema
    @GetMapping("/config")
    public ResponseEntity<Object> getSystemConfig() {
        try {
            Object config = systemConfigService.getSystemConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudo obtener la configuración", "detalle", e.getMessage()));
        }
    }

    // ✅ Actualizar configuración del sistema
    @PutMapping("/config")
    public ResponseEntity<Object> updateSystemConfig(@RequestBody SystemConfig config) {
        try {
            Object updated = systemConfigService.updateSystemConfig(config);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar la configuración", "detalle", e.getMessage()));
        }
    }

    // ✅ Reporte de subasta específica
    @GetMapping("/reports/auction/{eventId}")
    public ResponseEntity<Map<String, Object>> generateAuctionReport(@PathVariable Long eventId) {
        try {
            Map<String, Object> report = reportService.generateAuctionReport(eventId);
            return ResponseEntity.ok(report);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al generar reporte de subasta", "detalle", e.getMessage()));
        }
    }

    // ✅ Reporte de todos los usuarios
    @GetMapping("/reports/users")
    public ResponseEntity<Map<String, Object>> generateUserReport() {
        try {
            Map<String, Object> report = reportService.generateUserReport();
            return ResponseEntity.ok(report);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al generar reporte de usuarios", "detalle", e.getMessage()));
        }
    }

    // ✅ Listar pujas de un evento
    @GetMapping("/reports/bids/{eventId}")
    public ResponseEntity<List<Map<String, Object>>> getBidsByEvent(@PathVariable Long eventId) {
        try {
            List<Map<String, Object>> bids = reportService.getBidsByEvent(eventId);
            return ResponseEntity.ok(bids);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonList(Map.of("error", "Error al obtener pujas", "detalle", e.getMessage())));
        }
    }

    // ✅ NUEVO: Obtener métricas (KPIs) del dashboard admin
    @GetMapping("/reports/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs() {
        try {
            Map<String, Object> kpis = new HashMap<>();
            List<User> users = firestoreUserService.getAllUsers();

            long totalUsers = users.size();
            long activeUsers = users.stream().filter(u -> "active".equalsIgnoreCase(u.getStatus())).count();
            long suspendedUsers = users.stream().filter(u -> "suspended".equalsIgnoreCase(u.getStatus())).count();

            // Simulamos datos de subastas y pujas
            long activeAuctions = 12;
            long totalBids = 342;
            double totalRevenue = 15200.50;

            kpis.put("totalUsers", totalUsers);
            kpis.put("activeUsers", activeUsers);
            kpis.put("suspendedUsers", suspendedUsers);
            kpis.put("activeAuctions", activeAuctions);
            kpis.put("totalBids", totalBids);
            kpis.put("totalRevenue", totalRevenue);

            return ResponseEntity.ok(kpis);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudieron calcular los KPIs", "detalle", e.getMessage()));
        }
    }

    // ✅ NUEVO: Listar todos los usuarios
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<User> users = firestoreUserService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener usuarios", "detalle", e.getMessage()));
        }
    }

    // ✅ NUEVO: Actualizar rol de usuario
    @PutMapping("/users/{id}/role")
    public ResponseEntity<Object> updateUserRole(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String newRole = body.get("role");
            User updated = firestoreUserService.updateUserRole(id, newRole);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar rol", "detalle", e.getMessage()));
        }
    }

    // ✅ NUEVO: Actualizar estado de usuario
    @PutMapping("/users/{id}/status")
    public ResponseEntity<Object> updateUserStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String newStatus = body.get("status");
            User updated = firestoreUserService.updateUserStatus(id, newStatus);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar estado", "detalle", e.getMessage()));
        }
    }
}
