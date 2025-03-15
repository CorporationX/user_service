package school.faang.user_service.utils.validationUtils;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.goal.GoalInvitationDto;

@Slf4j
public class GoalInvitationValidation {
    private static final String GOAL_INVITATION_DTO_CANT_BE_NULL = "goalInvitationDto can't be null";
    private static final String GOAL_INVITATION_ID_CANT_BE_NULL = "Goal invitation id can't be null";
    private static final String INVITER_ID_IN_CANT_BE_NULL = "Inviter id in goal invitation can't be null";
    private static final String INVITED_USER_ID_CANT_BE_NULL = "Invited user id in goal invitation can't be null";
    private static final String GOAL_ID_CANT_BE_NULL = "Goal id in goal invitation can't be null";
    private static final String REQUEST_STATUS_CANT_BE_NULL = "Request status in goal invitation can't be null";

    public static void validateGoalInvitationDto(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto == null) {
            log.error(GOAL_INVITATION_DTO_CANT_BE_NULL);
            throw new IllegalArgumentException(GOAL_INVITATION_DTO_CANT_BE_NULL);
        } else if (goalInvitationDto.id() == null) {
            log.error(GOAL_INVITATION_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(GOAL_INVITATION_DTO_CANT_BE_NULL);
        } else if (goalInvitationDto.inviterId() == null) {
            log.error(INVITER_ID_IN_CANT_BE_NULL);
            throw new IllegalArgumentException(INVITER_ID_IN_CANT_BE_NULL);
        } else if (goalInvitationDto.invitedUserId() == null) {
            log.error(INVITED_USER_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(INVITED_USER_ID_CANT_BE_NULL);
        } else if (goalInvitationDto.goalId() == null) {
            log.error(GOAL_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(GOAL_ID_CANT_BE_NULL);
        } else if (goalInvitationDto.status() == null) {
            log.error(REQUEST_STATUS_CANT_BE_NULL);
            throw new IllegalArgumentException(REQUEST_STATUS_CANT_BE_NULL);
        }
    }
}