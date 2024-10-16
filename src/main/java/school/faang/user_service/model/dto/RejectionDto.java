package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RejectionDto {
    private Long id;
    private String rejectionReason;
}
