package school.faang.user_service.dto;

import lombok.Data;

@Data
public class AcceptationDto {
    private Long requestId;
    private Long requesterId;
    private Long receiverId;
    private String status;
}
