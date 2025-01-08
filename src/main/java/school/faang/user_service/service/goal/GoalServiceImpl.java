package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.goal.GoalMapper;
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
        log.info("success created goal by id: {}", createdGoal.getId());
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
        }
        log.info("success updated goal by id: {}", goalDto.getId());
        return goalMapper.toDto(goal);
    }

    @Override
    @Transactional
    public void delete(long goalId) {
        goalServiceValidator.existsById(goalId);
        goalRepository.deleteById(goalId);
        log.info("success deleted goal by id: {}", goalId);
    }

    @Override
    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, String title, String status) {
        goalServiceValidator.existsById(goalId);
        GoalStatus goalStatus = getGoalStatus(status);
        return goalRepository.findByParentIdAndFilter(goalId, title, goalStatus)
                .map(goalMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<GoalDto> findGoalsByUserIdAndFilter(long userId, String title, String status) {
        userServiceValidator.existsById(userId);
        Integer goalStatus = getGoalStatusOrdinal(status);
        return goalRepository.findByUserIdAndFilter(userId, title, goalStatus)
                .map(goalMapper::toDto)
                .toList();
    }

    private GoalStatus getGoalStatus(String status) {
        return (status != null) ? GoalStatus.valueOf(status) : null;
    }

    private Integer getGoalStatusOrdinal(String status) {
        return (status != null) ? GoalStatus.valueOf(status).ordinal() : null;
    }
}
