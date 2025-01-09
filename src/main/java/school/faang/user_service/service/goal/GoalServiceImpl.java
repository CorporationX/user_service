package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.goal.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillServiceInterface;
import school.faang.user_service.validator.goal.GoalServiceValidator;
import school.faang.user_service.validator.skill.SkillServiceValidator;
import school.faang.user_service.validator.user.UserServiceValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalServiceValidator goalServiceValidator;
    private final SkillServiceValidator skillServiceValidator;
    private final UserServiceValidator userServiceValidator;
    private final SkillServiceInterface skillService;
    private final GoalMapper goalMapper;
    private final UserMapper userMapper;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Override
    @Transactional
    public GoalDto create(Long userId, GoalDto goalDto) {
        userServiceValidator.existsById(userId);
        goalServiceValidator.validateActiveGoalsLimit(userId);
        skillServiceValidator.validateSkillsExist(goalDto.getSkillsToAchieveIds());

        Goal createdGoal = goalRepository.
                create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
        createdGoal.setSkillsToAchieve(skillService.getSKillsByIds(goalDto.getSkillsToAchieveIds()));
        createdGoal.setUsers(List.of(User.builder().id(userId).build()));
        return goalMapper.toDto(createdGoal);
    }

    @Override
    @Transactional
    public GoalDto update(UpdateGoalDto goalDto) {
        goalServiceValidator.validateForUpdating(goalDto);
        Goal goal = goalRepository.findById(goalDto.getId()).get();
        goalMapper.updateGoalFromDto(goalDto, goal);
        if (goalDto.getSkillsToAchieveIds() != null) {
            goal.setSkillsToAchieve(skillService.getSKillsByIds(goalDto.getSkillsToAchieveIds()));
        }
        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            skillService.addSkillsToUsersByGoalId(goal.getId());
            goalCompletedEventPublisher.publish(GoalCompletedEvent.builder()
                    .userIds(userMapper.mapUsersToUserIds(goal.getUsers()))
                    .goalId(goal.getId())
                    .build());
        }
        return goalMapper.toDto(goal);
    }
}
