package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RejectionDto {
    private Long requestId;
    private String status;
    private String reason;
}
