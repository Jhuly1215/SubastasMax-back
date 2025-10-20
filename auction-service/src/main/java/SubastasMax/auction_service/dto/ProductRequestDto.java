
package SubastasMax.auction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private String description;
    private List<String> categorys;
    private String condition;
    private List<String> images;
    private Map<String, Object> specifications;
    private String status;
    private String createdBy;
    private List<String> usedInAuctions;
}