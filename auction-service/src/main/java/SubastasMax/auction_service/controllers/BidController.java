package SubastasMax.auction_service.controllers;

import SubastasMax.auction_service.dto.BidCreateDTO;
import SubastasMax.auction_service.dto.BidResponseDTO;
import SubastasMax.auction_service.dto.BidUpdateDTO;
import SubastasMax.auction_service.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponseDTO> createBid(@Valid @RequestBody BidCreateDTO bidCreateDTO) 
            throws ExecutionException, InterruptedException {
        BidResponseDTO createdBid = bidService.createBid(bidCreateDTO);
        return new ResponseEntity<>(createdBid, HttpStatus.CREATED);
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidResponseDTO> getBidById(@PathVariable String bidId) 
            throws ExecutionException, InterruptedException {
        BidResponseDTO bid = bidService.getBidById(bidId);
        return ResponseEntity.ok(bid);
    }

    @GetMapping
    public ResponseEntity<List<BidResponseDTO>> getAllBids() 
            throws ExecutionException, InterruptedException {
        List<BidResponseDTO> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByUserId(@PathVariable String userId) 
            throws ExecutionException, InterruptedException {
        List<BidResponseDTO> bids = bidService.getBidsByUserId(userId);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByAuctionId(@PathVariable String auctionId) 
            throws ExecutionException, InterruptedException {
        List<BidResponseDTO> bids = bidService.getBidsByAuctionId(auctionId);
        return ResponseEntity.ok(bids);
    }

    @PutMapping("/{bidId}")
    public ResponseEntity<BidResponseDTO> updateBid(
            @PathVariable String bidId,
            @Valid @RequestBody BidUpdateDTO bidUpdateDTO) 
            throws ExecutionException, InterruptedException {
        BidResponseDTO updatedBid = bidService.updateBid(bidId, bidUpdateDTO);
        return ResponseEntity.ok(updatedBid);
    }

    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable String bidId) 
            throws ExecutionException, InterruptedException {
        bidService.deleteBid(bidId);
        return ResponseEntity.noContent().build();
    }
}