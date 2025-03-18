package school.faang.user_service.dto.goal;

public record GoalInvitationDto(
        Long id,
        Long inviterId,
        Long invitedUserId,
        Long goalId,
        RequestStatusDto status
) {
}
