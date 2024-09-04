package school.faang.user_service.dto.mentorshipRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestFilterDto {
    private String descriptionPattern;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
