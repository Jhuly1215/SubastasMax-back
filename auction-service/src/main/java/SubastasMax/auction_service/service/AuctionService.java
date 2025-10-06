package SubastasMax.auction_service.service;

import SubastasMax.auction_service.dto.AuctionResponse;
import SubastasMax.auction_service.dto.CreateAuctionRequest;
import SubastasMax.auction_service.dto.UpdateAuctionRequest;
import SubastasMax.auction_service.exception.AuctionNotFoundException;
import SubastasMax.auction_service.exception.UnauthorizedException;
import SubastasMax.auction_service.model.Auction_model;
import SubastasMax.auction_service.repository.AuctionRepository;
import com.google.cloud.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {
    
    private final AuctionRepository auctionRepository;
    
    /**
     * Crear una nueva subasta
     */
    public AuctionResponse createAuction(CreateAuctionRequest request, String userId) {
        try {
            Auction_model auction = Auction_model.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .initialPrice(request.getInitialPrice())
                    .currentPrice(request.getInitialPrice())
                    .minimumIncrement(request.getMinimumIncrement())
                    .reservePrice(request.getReservePrice())
                    .currency(request.getCurrency())
                    .startAt(dateToTimestamp(request.getStartAt()))
                    .endAt(dateToTimestamp(request.getEndAt()))
                    .duration(request.getDuration())
                    .autoExtension(request.getAutoExtension())
                    .extensionThreshold(request.getExtensionThreshold())
                    .extensionTime(request.getExtensionTime())
                    .featured(request.getFeatured())
                    .images(request.getImages())
                    .tags(request.getTags())
                    .visibility(request.getVisibility())
                    .scheduledDate(dateToTimestamp(request.getScheduledDate()))
                    .scheduledTime(dateToTimestamp(request.getScheduledTime()))
                    .status("draft") // Estado inicial
                    .createdBy(userId)
                    .totalBids(0)
                    .uniqueBidders(0)
                    .viewCount(0)
                    .participants(new ArrayList<>())
                    .build();
            
            Auction_model created = auctionRepository.create(auction);
            log.info("Auction created successfully: {}", created.getId());
            
            return toResponse(created);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error creating auction", e);
            throw new RuntimeException("Error creating auction: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar una subasta
     */
    public AuctionResponse updateAuction(String auctionId, UpdateAuctionRequest request, String userId, String userRole) {
        try {
            // Verificar que la subasta existe
            Auction_model existing = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + auctionId));
            
            // Verificar permisos: solo el creador o un admin puede actualizar
            if (!existing.getCreatedBy().equals(userId) && !userRole.equals("admin")) {
                throw new UnauthorizedException("You don't have permission to update this auction");
            }
            
            // Construir el mapa de actualizaciones solo con los campos no nulos
            Map<String, Object> updates = new HashMap<>();
            
            if (request.getTitle() != null) updates.put("title", request.getTitle());
            if (request.getDescription() != null) updates.put("description", request.getDescription());
            if (request.getCategory() != null) updates.put("category", request.getCategory());
            if (request.getInitialPrice() != null) updates.put("initialPrice", request.getInitialPrice());
            if (request.getMinimumIncrement() != null) updates.put("minimumIncrement", request.getMinimumIncrement());
            if (request.getReservePrice() != null) updates.put("reservePrice", request.getReservePrice());
            if (request.getCurrency() != null) updates.put("currency", request.getCurrency());
            if (request.getStartAt() != null) updates.put("startAt", dateToTimestamp(request.getStartAt()));
            if (request.getEndAt() != null) updates.put("endAt", dateToTimestamp(request.getEndAt()));
            if (request.getDuration() != null) updates.put("duration", request.getDuration());
            if (request.getAutoExtension() != null) updates.put("autoExtension", request.getAutoExtension());
            if (request.getExtensionThreshold() != null) updates.put("extensionThreshold", request.getExtensionThreshold());
            if (request.getExtensionTime() != null) updates.put("extensionTime", request.getExtensionTime());
            if (request.getFeatured() != null) updates.put("featured", request.getFeatured());
            if (request.getImages() != null) updates.put("images", request.getImages());
            if (request.getTags() != null) updates.put("tags", request.getTags());
            if (request.getVisibility() != null) updates.put("visibility", request.getVisibility());
            if (request.getScheduledDate() != null) updates.put("scheduledDate", dateToTimestamp(request.getScheduledDate()));
            if (request.getScheduledTime() != null) updates.put("scheduledTime", dateToTimestamp(request.getScheduledTime()));
            if (request.getStatus() != null) updates.put("status", request.getStatus());
            
            Auction_model updated = auctionRepository.update(auctionId, updates);
            log.info("Auction updated successfully: {}", auctionId);
            
            return toResponse(updated);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error updating auction", e);
            throw new RuntimeException("Error updating auction: " + e.getMessage());
        }
    }
    
    /**
     * Obtener una subasta por ID
     */
    public AuctionResponse getAuctionById(String auctionId) {
        try {
            Auction_model auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + auctionId));
            
            // Incrementar el contador de vistas
            Map<String, Object> updates = new HashMap<>();
            updates.put("viewCount", (auction.getViewCount() != null ? auction.getViewCount() : 0) + 1);
            auctionRepository.update(auctionId, updates);
            
            return toResponse(auction);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error getting auction", e);
            throw new RuntimeException("Error getting auction: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todas las subastas de un usuario
     */
    public List<AuctionResponse> getAuctionsByUser(String userId) {
        try {
            List<Auction_model> auctions = auctionRepository.findByCreatedBy(userId);
            return auctions.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error getting auctions by user", e);
            throw new RuntimeException("Error getting auctions: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todas las subastas
     */
    public List<AuctionResponse> getAllAuctions() {
        try {
            List<Auction_model> auctions = auctionRepository.findAll();
            return auctions.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error getting all auctions", e);
            throw new RuntimeException("Error getting auctions: " + e.getMessage());
        }
    }
    
    /**
     * Obtener subastas por estado
     */
    public List<AuctionResponse> getAuctionsByStatus(String status) {
        try {
            List<Auction_model> auctions = auctionRepository.findByStatus(status);
            return auctions.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error getting auctions by status", e);
            throw new RuntimeException("Error getting auctions: " + e.getMessage());
        }
    }
    
    /**
     * Obtener subastas por categoría
     */
    public List<AuctionResponse> getAuctionsByCategory(String category) {
        try {
            List<Auction_model> auctions = auctionRepository.findByCategory(category);
            return auctions.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error getting auctions by category", e);
            throw new RuntimeException("Error getting auctions: " + e.getMessage());
        }
    }
    
    /**
     * Eliminar una subasta
     */
    public void deleteAuction(String auctionId, String userId, String userRole) {
        try {
            Auction_model existing = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + auctionId));
            
            if (!existing.getCreatedBy().equals(userId) && !userRole.equals("admin")) {
                throw new UnauthorizedException("You don't have permission to delete this auction");
            }
            
            auctionRepository.delete(auctionId);
            log.info("Auction deleted successfully: {}", auctionId);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error deleting auction", e);
            throw new RuntimeException("Error deleting auction: " + e.getMessage());
        }
    }
    
    /**
     * Convertir Date a Timestamp de Firestore
     */
    private Timestamp dateToTimestamp(Date date) {
    if (date == null) return null;
    return Timestamp.of(date);
    }
    
    /**
     * Convertir Auction a AuctionResponse
     */
    private AuctionResponse toResponse(Auction_model auction) {
        return AuctionResponse.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .category(auction.getCategory())
                .initialPrice(auction.getInitialPrice())
                .currentPrice(auction.getCurrentPrice())
                .minimumIncrement(auction.getMinimumIncrement())
                .reservePrice(auction.getReservePrice())
                .currency(auction.getCurrency())
                .startAt(timestampToDate(auction.getStartAt()))
                .endAt(timestampToDate(auction.getEndAt()))
                .actualStartAt(timestampToDate(auction.getActualStartAt()))
                .actualEndAt(timestampToDate(auction.getActualEndAt()))
                .duration(auction.getDuration())
                .autoExtension(auction.getAutoExtension())
                .extensionThreshold(auction.getExtensionThreshold())
                .extensionTime(auction.getExtensionTime())
                .featured(auction.getFeatured())
                .images(auction.getImages())
                .tags(auction.getTags())
                .participants(auction.getParticipants())
                .status(auction.getStatus())
                .totalBids(auction.getTotalBids())
                .uniqueBidders(auction.getUniqueBidders())
                .viewCount(auction.getViewCount())
                .visibility(auction.getVisibility())
                .winnerId(auction.getWinnerId())
                .winningBid(auction.getWinningBid())
                .createdBy(auction.getCreatedBy())
                .createdAt(timestampToDate(auction.getCreatedAt()))
                .updatedAt(timestampToDate(auction.getUpdatedAt()))
                .approvedBy(auction.getApprovedBy())
                .approvedAt(timestampToDate(auction.getApprovedAt()))
                .settledAt(timestampToDate(auction.getSettledAt()))
                .build();
    }
    
    /**
     * Convertir Timestamp a Date
     */
    private Date timestampToDate(Timestamp timestamp) {
        if (timestamp == null) return null;
        return new Date(timestamp.getSeconds() * 1000);
    }
}