package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@Builder
public class RequestFilterDto {
    private String descriptionPattern;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
