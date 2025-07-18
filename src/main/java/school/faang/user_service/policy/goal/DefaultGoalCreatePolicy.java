package school.faang.user_service.policy.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultGoalCreatePolicy implements GoalCreatePolicy {
    @Value("${user.goal.limit-active:2}")
    private int userMaxGoalLimit;
    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;
    private final GoalPolicyUtils goalPolicyUtils;

    @Override
    public void validate(CreateGoalDto dto) {
        long currentUserId = userContext.getUserId();
        Long mentorId = dto.mentorId();
        boolean isMentee = mentorId != null
                           && mentorshipRepository.existsByMentorIdAndMenteeIds(mentorId, dto.userIds());

        goalPolicyUtils.denyIfNotSelfAndMentee(
                currentUserId,
                dto.userIds(),
                isMentee,
                () -> deny("Cannot create goal", currentUserId, dto)
        );

        if (isParticipantsGoalLimitExceeded(dto.userIds())) {
            deny("User cannot have more then "
                 + userMaxGoalLimit + " active goals", currentUserId, dto);
        }
    }

    private void deny(String msg, long userId, CreateGoalDto dto) {
        String msgDetails = String.format("User %d cannot create goal for userIds=%s", userId, dto.userIds());
        throw new DataValidationException(msg, msg + ", " + msgDetails);
    }

    private boolean isParticipantsGoalLimitExceeded(List<Long> userIds) {
        if (userIds == null) {
            return false;
        }
        return goalRepository.countUsersExceedingGoals(
                userIds,
                GoalStatus.ACTIVE,
                userMaxGoalLimit
        ) > 0;
    }
}

