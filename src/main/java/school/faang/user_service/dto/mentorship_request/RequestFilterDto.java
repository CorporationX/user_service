package school.faang.user_service.dto.mentorship_request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private String descriptionPattern;

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;
}
