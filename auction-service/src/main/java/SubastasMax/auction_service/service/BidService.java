package SubastasMax.auction_service.service;

import SubastasMax.auction_service.dto.BidCreateDTO;
import SubastasMax.auction_service.dto.BidResponseDTO;
import SubastasMax.auction_service.dto.BidUpdateDTO;
import SubastasMax.auction_service.exception.BidNotFoundException;
import SubastasMax.auction_service.model.Bid;
import SubastasMax.auction_service.repository.BidRepository;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public BidResponseDTO createBid(BidCreateDTO bidCreateDTO) throws ExecutionException, InterruptedException {
        Bid bid = new Bid();
        bid.setAmount(bidCreateDTO.getAmount());
        bid.setAuctionId(bidCreateDTO.getAuctionId());
        bid.setUserId(bidCreateDTO.getUserId());
        bid.setUserName(bidCreateDTO.getUserName());
        bid.setStatus(bidCreateDTO.getStatus() != null ? bidCreateDTO.getStatus() : "ACTIVE");
        bid.setTimestamp(Timestamp.now());

        String bidId = bidRepository.save(bid);
        bid.setId(bidId);

        return bidToBidResponseDTO(bid);
    }

    public BidResponseDTO getBidById(String bidId) throws ExecutionException, InterruptedException {
        Bid bid = bidRepository.findById(bidId);
        if (bid == null) {
            throw new BidNotFoundException("Bid not found with id: " + bidId);
        }
        return bidToBidResponseDTO(bid);
    }

    public List<BidResponseDTO> getAllBids() throws ExecutionException, InterruptedException {
        List<Bid> bids = bidRepository.findAll();
        return bids.stream()
                .map(this::bidToBidResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BidResponseDTO> getBidsByUserId(String userId) throws ExecutionException, InterruptedException {
        List<Bid> bids = bidRepository.findByUserId(userId);
        return bids.stream()
                .map(this::bidToBidResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BidResponseDTO> getBidsByAuctionId(String auctionId) throws ExecutionException, InterruptedException {
        List<Bid> bids = bidRepository.findByAuctionId(auctionId);
        return bids.stream()
                .map(this::bidToBidResponseDTO)
                .collect(Collectors.toList());
    }

    public BidResponseDTO updateBid(String bidId, BidUpdateDTO bidUpdateDTO) throws ExecutionException, InterruptedException {
        Bid existingBid = bidRepository.findById(bidId);
        if (existingBid == null) {
            throw new BidNotFoundException("Bid not found with id: " + bidId);
        }

        Map<String, Object> updates = new HashMap<>();
        if (bidUpdateDTO.getAmount() != null) {
            updates.put("amount", bidUpdateDTO.getAmount());
            existingBid.setAmount(bidUpdateDTO.getAmount());
        }
        if (bidUpdateDTO.getStatus() != null) {
            updates.put("status", bidUpdateDTO.getStatus());
            existingBid.setStatus(bidUpdateDTO.getStatus());
        }

        if (!updates.isEmpty()) {
            bidRepository.update(bidId, updates);
        }

        return bidToBidResponseDTO(existingBid);
    }

    public void deleteBid(String bidId) throws ExecutionException, InterruptedException {
        Bid bid = bidRepository.findById(bidId);
        if (bid == null) {
            throw new BidNotFoundException("Bid not found with id: " + bidId);
        }
        bidRepository.delete(bidId);
    }

    private BidResponseDTO bidToBidResponseDTO(Bid bid) {
        return new BidResponseDTO(
                bid.getId(),
                bid.getAmount(),
                bid.getAuctionId(),
                bid.getUserId(),
                bid.getUserName(),
                bid.getStatus(),
                bid.getTimestamp()
        );
    }
}