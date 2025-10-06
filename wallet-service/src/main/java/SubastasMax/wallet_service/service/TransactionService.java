package SubastasMax.wallet_service.service;

import SubastasMax.wallet_service.dto.*;
import SubastasMax.wallet_service.exception.DuplicateRequestException;
import SubastasMax.wallet_service.exception.TransactionNotFoundException;
import SubastasMax.wallet_service.model.Transaction;
import SubastasMax.wallet_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private final ConcurrentHashMap<String, String> requestIdCache = new ConcurrentHashMap<>();
    
    public TransactionResponseDTO createTransaction(TransactionCreateDTO createDTO) throws ExecutionException, InterruptedException {
        
        System.out.println("requestId = " + createDTO.getRequestId());

        if (requestIdCache.containsKey(createDTO.getRequestId())) {
            throw new DuplicateRequestException("Request ID already processed: " + createDTO.getRequestId());
        }

        Transaction transaction = Transaction.builder()
            .userId(createDTO.getUserId())
            .amount(createDTO.getAmount())
            .currency(createDTO.getCurrency())
            .type(createDTO.getType())
            .status("pending")
            .description(createDTO.getDescription())
            .requestId(createDTO.getRequestId())
            .balanceBefore(mapToBalance(createDTO.getBalanceBefore()))
            .balanceAfter(mapToBalance(createDTO.getBalanceAfter()))
            .metadata(mapToMetadata(createDTO.getMetadata()))
            .build();

        String transactionId = transactionRepository.createTransaction(transaction);
        requestIdCache.put(createDTO.getRequestId(), transactionId);

        transaction.setTransactionId(transactionId);
        return mapToResponseDTO(transaction);
    }

    public TransactionResponseDTO getTransaction(String userId, String transactionId) throws ExecutionException, InterruptedException {
        Transaction transaction = transactionRepository.getTransaction(userId, transactionId);
        
        System.out.println("Buscando transacción service: userId=" + userId + ", transactionId=" + transactionId);
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction not found: " + transactionId);
        }
        
        return mapToResponseDTO(transaction);
    }

    public TransactionResponseDTO updateTransaction(String userId, String transactionId, TransactionUpdateDTO updateDTO) throws ExecutionException, InterruptedException {
        
        Transaction existingTransaction = transactionRepository.getTransaction(userId, transactionId);
        if (existingTransaction == null) {
            throw new TransactionNotFoundException("Transaction not found: " + transactionId);
        }

        Map<String, Object> updates = new HashMap<>();
        
        if (updateDTO.getStatus() != null) {
            updates.put("status", updateDTO.getStatus());
        }
        
        if (updateDTO.getErrorMessage() != null) {
            updates.put("errorMessage", updateDTO.getErrorMessage());
        }
        
        if (updateDTO.getCompletedAt() != null) {
            updates.put("completedAt", updateDTO.getCompletedAt());
        }
        
        if (updateDTO.getFailedAt() != null) {
            updates.put("failedAt", updateDTO.getFailedAt());
        }
        
        if (updateDTO.getBalanceAfter() != null) {
            updates.put("balanceAfter", mapToBalance(updateDTO.getBalanceAfter()).toMap());
        }
        
        if (updateDTO.getMetadata() != null) {
            updates.put("metadata", mapToMetadata(updateDTO.getMetadata()).toMap());
        }

        transactionRepository.updateTransaction(userId, transactionId, updates);
        
        Transaction updatedTransaction = transactionRepository.getTransaction(userId, transactionId);
        return mapToResponseDTO(updatedTransaction);
    }

    public java.util.List<TransactionResponseDTO> getAllTransactionsByUser(String userId) throws ExecutionException, InterruptedException {
        java.util.List<Transaction> transactions = transactionRepository.getAllTransactionsByUser(userId);
        java.util.List<TransactionResponseDTO> responseDTOs = new java.util.ArrayList<>();
        
        for (Transaction transaction : transactions) {
            responseDTOs.add(mapToResponseDTO(transaction));
        }
        
        return responseDTOs;
    }

    private Transaction.Balance mapToBalance(BalanceDTO dto) {
        if (dto == null) return null;
        return Transaction.Balance.builder()
            .available(dto.getAvailable())
            .frozen(dto.getFrozen())
            .total(dto.getTotal())
            .build();
    }

    private Transaction.TransactionMetadata mapToMetadata(TransactionMetadataDTO dto) {
        if (dto == null) return null;
        return Transaction.TransactionMetadata.builder()
            .auctionId(dto.getAuctionId())
            .bidId(dto.getBidId())
            .bankAccount(dto.getBankAccount())
            .paymentMethod(dto.getPaymentMethod())
            .externalTransactionId(dto.getExternalTransactionId())
            .fromCurrency(dto.getFromCurrency())
            .toCurrency(dto.getToCurrency())
            .exchangeRate(dto.getExchangeRate())
            .convertedAmount(dto.getConvertedAmount())
            .build();
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
            .transactionId(transaction.getTransactionId())
            .userId(transaction.getUserId())
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .type(transaction.getType())
            .status(transaction.getStatus())
            .description(transaction.getDescription())
            .requestId(transaction.getRequestId())
            .balanceBefore(mapBalanceToDTO(transaction.getBalanceBefore()))
            .balanceAfter(mapBalanceToDTO(transaction.getBalanceAfter()))
            .createdAt(transaction.getCreatedAt())
            .completedAt(transaction.getCompletedAt())
            .failedAt(transaction.getFailedAt())
            .errorMessage(transaction.getErrorMessage())
            .metadata(mapMetadataToDTO(transaction.getMetadata()))
            .build();
    }

    private BalanceDTO mapBalanceToDTO(Transaction.Balance balance) {
        if (balance == null) return null;
        return BalanceDTO.builder()
            .available(balance.getAvailable())
            .frozen(balance.getFrozen())
            .total(balance.getTotal())
            .build();
    }

    private TransactionMetadataDTO mapMetadataToDTO(Transaction.TransactionMetadata metadata) {
        if (metadata == null) return null;
        return TransactionMetadataDTO.builder()
            .auctionId(metadata.getAuctionId())
            .bidId(metadata.getBidId())
            .bankAccount(metadata.getBankAccount())
            .paymentMethod(metadata.getPaymentMethod())
            .externalTransactionId(metadata.getExternalTransactionId())
            .fromCurrency(metadata.getFromCurrency())
            .toCurrency(metadata.getToCurrency())
            .exchangeRate(metadata.getExchangeRate())
            .convertedAmount(metadata.getConvertedAmount())
            .build();
    }
}