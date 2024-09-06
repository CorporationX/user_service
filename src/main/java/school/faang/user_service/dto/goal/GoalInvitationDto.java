package school.faang.user_service.dto.goal;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record GoalInvitationDto(Long id, Long inviterId, Long invitedUserId, Long goalId, RequestStatus status) {
}