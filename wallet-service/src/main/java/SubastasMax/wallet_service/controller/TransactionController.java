package SubastasMax.wallet_service.controller;

import SubastasMax.wallet_service.dto.TransactionCreateDTO;
import SubastasMax.wallet_service.dto.TransactionResponseDTO;
import SubastasMax.wallet_service.model.Transaction;
import SubastasMax.wallet_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionCreateDTO createDTO) {
        try {
            System.out.println("Creando transacción de " + createDTO.getFromUserId() + " a " + createDTO.getToUserId());
            String transactionId = transactionService.createTransaction(createDTO);
            Transaction transaction = transactionService.getTransaction(transactionId);
            TransactionResponseDTO response = mapToResponseDTO(transaction);
            System.out.println("Transacción creada exitosamente: " + transactionId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la transacción: " + e.getMessage());
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable String transactionId) throws ExecutionException, InterruptedException {
        Transaction transaction = transactionService.getTransaction(transactionId);
        
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        
        System.out.println("Buscando transacción: " + transactionId);
        TransactionResponseDTO response = mapToResponseDTO(transaction);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/received")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsReceivedByUser(
            @PathVariable String userId) throws ExecutionException, InterruptedException {
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        List<TransactionResponseDTO> response = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/sent")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsSentByUser(
            @PathVariable String userId) throws ExecutionException, InterruptedException {
        List<Transaction> transactions = transactionService.getTransactionsSentByUser(userId);
        List<TransactionResponseDTO> response = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactionsByUser(
            @PathVariable String userId) throws ExecutionException, InterruptedException {
        List<Transaction> sentTransactions = transactionService.getTransactionsSentByUser(userId);
        List<Transaction> receivedTransactions = transactionService.getTransactionsByUser(userId);
        
        List<Transaction> allTransactions = new java.util.ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);
        
        allTransactions.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
        
        List<TransactionResponseDTO> response = allTransactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/between/{userId1}/{userId2}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsBetweenUsers(
            @PathVariable String userId1,
            @PathVariable String userId2) throws ExecutionException, InterruptedException {
        List<Transaction> transactions = transactionService.getTransactionsBetweenUsers(userId1, userId2);
        List<TransactionResponseDTO> response = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transactionId}/status")
    public ResponseEntity<TransactionResponseDTO> updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestParam String status) throws ExecutionException, InterruptedException {
        
        transactionService.updateTransactionStatus(transactionId, status);
        Transaction transaction = transactionService.getTransaction(transactionId);
        
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        
        TransactionResponseDTO response = mapToResponseDTO(transaction);
        return ResponseEntity.ok(response);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .transactionId(transaction.getTransactionId())
                .fromUserId(transaction.getFromUserId())
                .toUserId(transaction.getToUserId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .requestId(transaction.getRequestId())
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
                .failedAt(transaction.getFailedAt())
                .errorMessage(transaction.getErrorMessage())
                .build();
    }
}