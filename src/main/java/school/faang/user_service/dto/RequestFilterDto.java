package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestFilterDto {
    private Long id;
    private String status;
    private Long requesterId;
    private Long receiverId;
    private String descriptionPattern;
}
