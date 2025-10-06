package SubastasMax.wallet_service.controller;

import SubastasMax.wallet_service.dto.TransactionCreateDTO;
import SubastasMax.wallet_service.dto.TransactionResponseDTO;
import SubastasMax.wallet_service.dto.TransactionUpdateDTO;
import SubastasMax.wallet_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionCreateDTO createDTO) {
    try {
        System.out.println("Llamando al servicio...");
        TransactionResponseDTO response = transactionService.createTransaction(createDTO);
        System.out.println("Creating transaction for userId controller = " + response.getUserId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace(); // imprime el error completo en la consola
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la transacción: " + e.getMessage());
    }
}
    @GetMapping("/user/{userId}")
    public ResponseEntity<java.util.List<TransactionResponseDTO>> getAllTransactionsByUser(
            @PathVariable String userId) throws ExecutionException, InterruptedException {
        java.util.List<TransactionResponseDTO> transactions = transactionService.getAllTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{userId}/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId) throws ExecutionException, InterruptedException {
        TransactionResponseDTO response = transactionService.getTransaction(userId, transactionId);
        System.out.println("Buscando transacción: userId=" + userId + ", transactionId=" + transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId,
            @Valid @RequestBody TransactionUpdateDTO updateDTO) throws ExecutionException, InterruptedException {
        TransactionResponseDTO response = transactionService.updateTransaction(userId, transactionId, updateDTO);
        return ResponseEntity.ok(response);
    }
}