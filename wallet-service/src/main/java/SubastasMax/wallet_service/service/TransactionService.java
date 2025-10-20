package SubastasMax.wallet_service.service;

import SubastasMax.wallet_service.dto.BalanceDTO;
import SubastasMax.wallet_service.dto.TransactionCreateDTO;
import SubastasMax.wallet_service.dto.TransactionMetadataDTO;
import SubastasMax.wallet_service.model.Transaction;
import SubastasMax.wallet_service.repository.TransactionRepository;
import SubastasMax.wallet_service.util.RequestIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public String createTransaction(TransactionCreateDTO dto) throws ExecutionException, InterruptedException {
        // Genera requestId si no viene en el DTO
        String requestId = (dto.getRequestId() != null && !dto.getRequestId().isEmpty())
            ? dto.getRequestId()
            : RequestIdUtil.generateRequestId(dto.getFromUserId());
        
        Transaction transaction = Transaction.builder()
            .fromUserId(dto.getFromUserId())
            .toUserId(dto.getToUserId())
            .amount(dto.getAmount())
            .currency(dto.getCurrency())
            .type(dto.getType())
            .status("PENDING")
            .description(dto.getDescription())
            .requestId(requestId)
            .balanceBefore(mapToBalance(dto.getBalanceBefore()))
            .balanceAfter(mapToBalance(dto.getBalanceAfter()))
            .metadata(mapToMetadata(dto.getMetadata()))
            .build();

        return transactionRepository.createTransaction(transaction);
    }

    public Transaction getTransaction(String transactionId) throws ExecutionException, InterruptedException {
        return transactionRepository.getTransaction(transactionId);
    }

    public List<Transaction> getTransactionsByUser(String userId) throws ExecutionException, InterruptedException {
        return transactionRepository.getAllTransactionsByUser(userId);
    }

    public List<Transaction> getTransactionsSentByUser(String userId) throws ExecutionException, InterruptedException {
        return transactionRepository.getTransactionsByFromUser(userId);
    }

    public List<Transaction> getTransactionsBetweenUsers(String userId1, String userId2) throws ExecutionException, InterruptedException {
        return transactionRepository.getTransactionsBetweenUsers(userId1, userId2);
    }

    public void updateTransactionStatus(String transactionId, String status) throws ExecutionException, InterruptedException {
        transactionRepository.updateTransaction(transactionId, java.util.Map.of("status", status));
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
}