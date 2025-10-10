package SubastasMax.admin_service.controller;

import SubastasMax.admin_service.model.User;
import SubastasMax.admin_service.service.FirestoreUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 🔥 permite acceso desde el frontend (Next.js)
public class UserController {

    private final FirestoreUserService firestoreUserService;

    public UserController(FirestoreUserService firestoreUserService) {
        this.firestoreUserService = firestoreUserService;
    }

    // ✅ Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws ExecutionException, InterruptedException {
        List<User> users = firestoreUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ✅ Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) throws ExecutionException, InterruptedException {
        User user = firestoreUserService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // ✅ Crear nuevo usuario
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        User createdUser = firestoreUserService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // ✅ Actualizar usuario completo
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) throws ExecutionException, InterruptedException {
        User updatedUser = firestoreUserService.updateUser(id, user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) throws ExecutionException, InterruptedException {
        boolean deleted = firestoreUserService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // 🔹 NUEVO: Cambiar el rol de un usuario
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable String id, @RequestParam String role)
            throws ExecutionException, InterruptedException {
        User updatedUser = firestoreUserService.updateUserRole(id, role);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    // 🔹 NUEVO: Cambiar el estado (activo/suspendido)
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable String id, @RequestParam String status)
            throws ExecutionException, InterruptedException {
        User updatedUser = firestoreUserService.updateUserStatus(id, status);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }
}
