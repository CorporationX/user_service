package school.faang.user_service.model.filter_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.RequestStatus;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestFilterDto {
    private String descriptionPattern;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
