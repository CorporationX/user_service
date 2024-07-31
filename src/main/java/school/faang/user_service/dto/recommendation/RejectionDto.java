package school.faang.user_service.dto.recommendation;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RejectionDto {
    private Long id;
    private String reason;
}
