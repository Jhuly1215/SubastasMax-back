// AuthController.java
package SubastasMax.admin_service.controller;

import SubastasMax.admin_service.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // provide a minimal local interface so the controller compiles if the external service class is missing
    public interface UserService {
        User getUserById(String id);
        void createUser(User user);
    }

    private final UserService userService;
    private final FirebaseAuth firebaseAuth;

    public AuthController(UserService userService, FirebaseAuth firebaseAuth) {
        this.userService = userService;
        this.firebaseAuth = firebaseAuth;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            UserRecord userRecord = firebaseAuth.getUserByEmail(request.getEmail());
            User user = userService.getUserById(userRecord.getUid());
            if (user == null) {
                user = new User();
                // Firebase UIDs are strings; the domain User expects a Long id, so leave id null
                // and let the service/persistence assign the numeric id.
                user.setId(null);
                user.setEmail(userRecord.getEmail());
                user.setName(userRecord.getDisplayName() != null ? userRecord.getDisplayName() : request.getName());
                user.setRole(request.getRole());
                // tenant handling removed: domain User has no tenantId field; set tenant in persistence if needed
                user.setReputation(request.getRole().equals("participante") ? 50 : 70);
                // email verification is managed by Firebase and/or persistence layer; do not set here because User has no setVerified method
                // plan is handled by the persistence layer or the User model; omit setting it here
                userService.createUser(user);
            }
            String token = firebaseAuth.createCustomToken(userRecord.getUid());
            return ResponseEntity.ok(new AuthResponse(true, user, token, null));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, "Credenciales inválidas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getName());

            // create the user in Firebase and obtain the UserRecord
            UserRecord userRecord = firebaseAuth.createUser(createRequest);

            User user = new User();
            // Firebase UIDs are strings; the domain User expects a Long id, so leave id null
            // and let the service/persistence assign the numeric id.
            user.setId(null);
            user.setEmail(userRecord.getEmail());
            user.setName(userRecord.getDisplayName() != null ? userRecord.getDisplayName() : request.getName());
            user.setRole(request.getRole());
            // tenant handling removed: domain User has no tenantId field; set tenant in persistence if needed
            user.setReputation(request.getRole().equals("participante") ? 50 : 70);
            // email verification is managed by Firebase and/or persistence layer; do not set here because User has no setVerified method
            // plan is handled by the persistence layer or the User model; omit setting it here
            userService.createUser(user);

            String token = firebaseAuth.createCustomToken(userRecord.getUid());
            return ResponseEntity.ok(new AuthResponse(true, user, token, null));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, "Error al registrar usuario"));
        }
    }
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String uid = firebaseAuth.verifyIdToken(token.replace("Bearer ", "")).getUid();
            User user = userService.getUserById(uid);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    static class AuthRequest {
        private String email;
        private String password;
        private String name;
        private String role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    static class AuthResponse {
        private boolean success;
        private User user;
        private String token;
        private String error;

        public AuthResponse(boolean success, User user, String token, String error) {
            this.success = success;
            this.user = user;
            this.token = token;
            this.error = error;
        }

        public boolean isSuccess() { return success; }
        public User getUser() { return user; }
        public String getToken() { return token; }
        public String getError() { return error; }
    }
}