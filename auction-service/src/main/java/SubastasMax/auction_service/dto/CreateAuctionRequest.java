package SubastasMax.auction_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// DTO para crear subasta
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Initial price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Initial price must be greater than 0")
    private Double initialPrice;
    
    @NotNull(message = "Minimum increment is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum increment must be greater than 0")
    private Double minimumIncrement;
    
    private Double reservePrice;
    
    @NotBlank(message = "Currency is required")
    private String currency;
    
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startAt;
    
    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endAt;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Long duration;
    
    @Builder.Default
    private Boolean autoExtension = false;
    
    private Long extensionThreshold;
    
    private Long extensionTime;
    
    @Builder.Default
    private Boolean featured = false;
    
    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @Builder.Default
    private Integer visibility = 1;
    
    private Date scheduledDate;
    
    private Date scheduledTime;
}

