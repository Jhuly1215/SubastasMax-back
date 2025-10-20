package SubastasMax.auction_service.controllers;

import SubastasMax.auction_service.dto.AuctionResponse;
import SubastasMax.auction_service.dto.CreateAuctionRequest;
import SubastasMax.auction_service.dto.UpdateAuctionRequest;
import SubastasMax.auction_service.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {
    
    private final AuctionService auctionService;
    
    /**
     * Crear una nueva subasta
     * POST /api/auctions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAuction(
            @Valid @RequestBody CreateAuctionRequest request,
            Authentication authentication) {
        
        try {
            /*String userId = authentication.getName();*/
            String userId = authentication.getName();
            AuctionResponse response = auctionService.createAuction(request, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Auction created successfully");
            result.put("data", response);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (Exception e) {
            log.error("Error creating auction", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Actualizar una subasta existente
     * PUT /api/auctions/{auctionId}
     */
    @PutMapping("/{auctionId}")
    public ResponseEntity<Map<String, Object>> updateAuction(
            @PathVariable String auctionId,
            @Valid @RequestBody UpdateAuctionRequest request,
            Authentication authentication) {
        
        try {
            /*String userId = authentication.getName();
            String userRole = getUserRole(authentication);*/

            String userId = authentication.getName();
            String userRole = getUserRole(authentication);
            
            AuctionResponse response = auctionService.updateAuction(auctionId, request, userId, userRole);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Auction updated successfully");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error updating auction", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            
            HttpStatus status = e.getMessage().contains("not found") ? 
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            
            return ResponseEntity.status(status).body(error);
        }
    }
    
    /**
     * Obtener una subasta por ID
     * GET /api/auctions/{auctionId}
     */
    @GetMapping("/{auctionId}")
    public ResponseEntity<Map<String, Object>> getAuction(@PathVariable String auctionId) {
        try {
            AuctionResponse response = auctionService.getAuctionById(auctionId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error getting auction", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            
            HttpStatus status = e.getMessage().contains("not found") ? 
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            
            return ResponseEntity.status(status).body(error);
        }
    }
    
    /**
     * Obtener todas las subastas
     * GET /api/auctions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAuctions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        
        try {
            List<AuctionResponse> auctions;
            
            if (status != null && !status.isEmpty()) {
                auctions = auctionService.getAuctionsByStatus(status);
            } else if (category != null && !category.isEmpty()) {
                auctions = auctionService.getAuctionsByCategory(category);
            } else {
                auctions = auctionService.getAllAuctions();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", auctions.size());
            result.put("data", auctions);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error getting auctions", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Obtener todas las subastas de un usuario específico
     * GET /api/auctions/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getAuctionsByUser(@PathVariable String userId) {
        try {
            List<AuctionResponse> auctions = auctionService.getAuctionsByUser(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", auctions.size());
            result.put("data", auctions);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error getting user auctions", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Obtener mis subastas (del usuario autenticado)
     * GET /api/auctions/my-auctions
     */
    @GetMapping("/my-auctions")
    public ResponseEntity<Map<String, Object>> getMyAuctions(Authentication authentication) {
        try {
            /*String userId = authentication.getName();*/
            String userId = authentication.getName();
            List<AuctionResponse> auctions = auctionService.getAuctionsByUser(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", auctions.size());
            result.put("data", auctions);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error getting my auctions", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Eliminar una subasta
     * DELETE /api/auctions/{auctionId}
     */
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Map<String, Object>> deleteAuction(
            @PathVariable String auctionId,
            Authentication authentication) {
        
        try {
            
            String userId = authentication.getName();
            String userRole = getUserRole(authentication);
            
            auctionService.deleteAuction(auctionId, userId, userRole);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Auction deleted successfully");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error deleting auction", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            
            HttpStatus status = e.getMessage().contains("not found") ? 
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            
            return ResponseEntity.status(status).body(error);
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/auctions/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Auction service is running");
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtener el rol del usuario desde el token
     */
    private String getUserRole(Authentication authentication) {
        // Esto dependerá de cómo Firebase Auth almacene los custom claims
        // Por defecto retornamos "user" si no se encuentra el rol
        return /*authentication.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .map(auth -> auth.getAuthority().replace("ROLE_", "").toLowerCase())
                .findFirst()
                .orElse("participante");*/ 
                
                "user"; // Temporal hasta integrar seguridad
    }
}