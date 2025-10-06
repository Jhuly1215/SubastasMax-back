package SubastasMax.auction_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAuctionRequest {
    
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    private String description;
    
    private String category;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Initial price must be greater than 0")
    private Double initialPrice;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum increment must be greater than 0")
    private Double minimumIncrement;
    
    private Double reservePrice;
    
    private String currency;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endAt;
    
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Long duration;
    
    private Boolean autoExtension;
    
    private Long extensionThreshold;
    
    private Long extensionTime;
    
    private Boolean featured;
    
    private List<String> images;
    
    private List<String> tags;
    
    private Integer visibility;
    
    private Date scheduledDate;
    
    private Date scheduledTime;
    
    private String status;
}
