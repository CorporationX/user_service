package school.faang.user_service.dto;

import lombok.*;

@Data
@Builder
public class RequestFilterDto {
    private Long id;
    private String status;
    private Long requesterId;
    private Long receiverId;
}
