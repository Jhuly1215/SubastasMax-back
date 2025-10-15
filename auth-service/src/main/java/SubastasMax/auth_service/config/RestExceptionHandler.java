// auth-service/src/main/java/SubastasMax/auth_service/config/RestExceptionHandler.java
package SubastasMax.auth_service.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> badRequest(IllegalArgumentException ex){
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,Object> serverError(Exception ex){
        return Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage());
    }
}
