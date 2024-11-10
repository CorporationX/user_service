package school.faang.user_service.dto.goal;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GoalResponseDto(
        Long id,
        Long parentId,
        String title,
        String description,
        GoalStatusDto status,
        LocalDateTime deadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long mentorId,
        List<GoalInvitationResponseDto> invitations,
        List<Long> usersId,
        List<Long> skillsToAchieveIds
) {
}