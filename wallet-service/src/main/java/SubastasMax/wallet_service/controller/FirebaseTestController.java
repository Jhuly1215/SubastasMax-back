package SubastasMax.wallet_service.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.google.api.Authentication;
import com.google.firebase.database.FirebaseDatabase;

@RestController
@RequestMapping("/test")
public class FirebaseTestController {

    @GetMapping("/firebase")
    public String testFirebase() {

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth en /test/** => " + auth);
        
        try {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            return "✅ Firebase conectado: " + db.getReference().toString();
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}
