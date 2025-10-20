package SubastasMax.wallet_service.service;

import SubastasMax.wallet_service.dto.WalletRequestDTO;
import SubastasMax.wallet_service.dto.WalletResponseDTO;
import SubastasMax.wallet_service.exception.WalletAlreadyExistsException;
import SubastasMax.wallet_service.exception.WalletNotFoundException;
import SubastasMax.wallet_service.model.Wallet;
import SubastasMax.wallet_service.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public WalletResponseDTO createWallet(WalletRequestDTO requestDTO) {
        try {
            Wallet existingWallet = walletRepository.findByUserId(requestDTO.getUserId());
            if (existingWallet != null) {
                throw new WalletAlreadyExistsException("Wallet already exists for user: " + requestDTO.getUserId());
            }

            Wallet wallet = mapToEntity(requestDTO);
            wallet.setCreatedAt(System.currentTimeMillis());
            wallet.setUpdatedAt(System.currentTimeMillis());

            if (wallet.getBalances().isEmpty()) {
                initializeDefaultBalances(wallet);
            }

            Wallet savedWallet = walletRepository.save(wallet);
            return mapToResponseDTO(savedWallet);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error creating wallet: " + e.getMessage(), e);
        }
    }

    public WalletResponseDTO getWallet(String userId) {
        try {
            Wallet wallet = walletRepository.findByUserId(userId);
            if (wallet == null) {
                throw new WalletNotFoundException("Wallet not found for user: " + userId);
            }
            return mapToResponseDTO(wallet);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching wallet: " + e.getMessage(), e);
        }
    }

    public WalletResponseDTO updateWallet(String userId, WalletRequestDTO requestDTO) {
        try {
            Wallet existingWallet = walletRepository.findByUserId(userId);
            if (existingWallet == null) {
                throw new WalletNotFoundException("Wallet not found for user: " + userId);
            }

            Wallet updatedWallet = mapToEntity(requestDTO);
            updatedWallet.setUserId(userId);
            updatedWallet.setCreatedAt(existingWallet.getCreatedAt());
            updatedWallet.setUpdatedAt(System.currentTimeMillis());

            Wallet savedWallet = walletRepository.update(userId, updatedWallet);
            return mapToResponseDTO(savedWallet);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating wallet: " + e.getMessage(), e);
        }
    }

    private void initializeDefaultBalances(Wallet wallet) {
        Map<String, Wallet.Balance> balances = new HashMap<>();
        
        String[] currencies = {"USD", "BOB", "EUR"};
        for (String currency : currencies) {
            Wallet.Balance balance = new Wallet.Balance();
            balance.setAvailable(0.0);
            balance.setFrozen(0.0);
            balance.setTotal(0.0);
            balances.put(currency, balance);
        }
        
        wallet.setBalances(balances);
    }

    private Wallet mapToEntity(WalletRequestDTO dto) {
        Wallet wallet = new Wallet();
        wallet.setUserId(dto.getUserId());
        wallet.setDefaultCurrency(dto.getDefaultCurrency());

        if (dto.getBalances() != null) {
            Map<String, Wallet.Balance> balances = new HashMap<>();
            dto.getBalances().forEach((currency, balanceDTO) -> {
                Wallet.Balance balance = new Wallet.Balance();
                balance.setAvailable(balanceDTO.getAvailable());
                balance.setFrozen(balanceDTO.getFrozen());
                balance.setTotal(balanceDTO.getTotal());
                balances.put(currency, balance);
            });
            wallet.setBalances(balances);
        }

        if (dto.getActiveBids() != null) {
            Map<String, Wallet.ActiveBid> activeBids = new HashMap<>();
            dto.getActiveBids().forEach((bidId, bidDTO) -> {
                Wallet.ActiveBid activeBid = new Wallet.ActiveBid();
                activeBid.setAuctionId(bidDTO.getAuctionId());
                activeBid.setBidAmount(bidDTO.getBidAmount());
                activeBid.setCurrency(bidDTO.getCurrency());
                activeBid.setFrozenAt(bidDTO.getFrozenAt());
                activeBids.put(bidId, activeBid);
            });
            wallet.setActiveBids(activeBids);
        }

        return wallet;
    }

    private WalletResponseDTO mapToResponseDTO(Wallet wallet) {
        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setUserId(wallet.getUserId());
        dto.setDefaultCurrency(wallet.getDefaultCurrency());
        dto.setCreatedAt(wallet.getCreatedAt());
        dto.setUpdatedAt(wallet.getUpdatedAt());

        Map<String, WalletResponseDTO.BalanceDTO> balances = new HashMap<>();
        wallet.getBalances().forEach((currency, balance) -> {
            WalletResponseDTO.BalanceDTO balanceDTO = new WalletResponseDTO.BalanceDTO();
            balanceDTO.setAvailable(balance.getAvailable());
            balanceDTO.setFrozen(balance.getFrozen());
            balanceDTO.setTotal(balance.getTotal());
            balances.put(currency, balanceDTO);
        });
        dto.setBalances(balances);

        Map<String, WalletResponseDTO.ActiveBidDTO> activeBids = new HashMap<>();
        wallet.getActiveBids().forEach((bidId, activeBid) -> {
            WalletResponseDTO.ActiveBidDTO bidDTO = new WalletResponseDTO.ActiveBidDTO();
            bidDTO.setAuctionId(activeBid.getAuctionId());
            bidDTO.setBidAmount(activeBid.getBidAmount());
            bidDTO.setCurrency(activeBid.getCurrency());
            bidDTO.setFrozenAt(activeBid.getFrozenAt());
            activeBids.put(bidId, bidDTO);
        });
        dto.setActiveBids(activeBids);

        return dto;
    }
}