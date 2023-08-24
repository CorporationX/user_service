package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.dto.redis.GoalSetEventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.CreateGoalMapper;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.GoalSetPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final CreateGoalMapper createGoalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalSetPublisher goalSetPublisher;
    private static final int MAX_GOALS_PER_USER = 3;

    public void addUser(Goal goal, User user) {
        if (goal.getUsers() == null) {
            goal.setUsers(new ArrayList<>());
        }
        goal.getUsers().add(user);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        if (!existGoalById(goalId)) {
            throw new IllegalArgumentException("Goal is not found");
        }

        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())));

        if (filterDto != null) {
            return filterGoals(goals, filterDto);
        }

        return goalMapper.toDtoList(goals.toList());
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getSubGoalsByFilter(Long parentId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(parentId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())));

        if (filterDto != null) {
            return filterGoals(goals, filterDto);
        }

        return goalMapper.toDtoList(goals.toList());
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filter) {
        Stream<Goal> filteredGoals = goals;

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                filteredGoals = goalFilter.apply(filteredGoals, filter);
            }
        }

        return goalMapper.toDtoList(filteredGoals.toList());
    }

    @Transactional
    public ResponseGoalDto createGoal(Long userId, CreateGoalDto goalDto) {
        validateGoalToCreate(userId, goalDto);
        Goal goal = goalRepository.save(createGoalMapper.toGoalFromCreateGoalDto(goalDto));
        goal.setUsers(List.of(User.builder().id(userId).build()));
        goalSetPublisher.publishMessage(new GoalSetEventDto(goal.getId(), userId));
        return createGoalMapper.toResponseGoalDtoFromGoal(goal);
    }

    @Transactional(readOnly = true)
    public boolean canAddGoalToUser(Long userId) {
        return goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER;
    }

    public boolean existGoalById(Long goalId) {
        return goalRepository.existsById(goalId);
    }

    private void validateGoalToCreate(Long userId, CreateGoalDto goalDto) {
        if (canAddGoalToUser(userId)) {
            throw new IllegalArgumentException("Maximum number of goals for this user reached");
        }

        List<SkillDto> skillsToAchieve = goalDto.getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new IllegalArgumentException("Skill not found");
            }
        });
    }


    @Transactional
    public UpdateGoalDto updateGoal(UpdateGoalDto updateGoalDto) {
        List<SkillDto> skillDtos = updateGoalDto.getSkillDtos();
        List<Long> userIds = updateGoalDto.getUserIds();
        Goal goalToUpdate = goalRepository.findById(updateGoalDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Goal with id " + updateGoalDto.getId() + " is not found"
                ));

        validateUpdate(goalToUpdate, updateGoalDto, skillDtos);

        if (updateGoalDto.getStatus() == GoalStatus.COMPLETED) {
            assignSKillsToUsers(skillDtos, userIds);
            goalToUpdate.setStatus(updateGoalDto.getStatus());

        }

        goalToUpdate.setUpdatedAt(LocalDateTime.now());
        return goalMapper.goalToUpdateGoalDto(goalRepository.save(goalToUpdate));
    }

    private void assignSKillsToUsers(List<SkillDto> skillDtos, List<Long> userIds) {
        skillDtos.forEach(skill -> {
            userIds.forEach(userId -> {
                User user = userRepository.findById(userId).orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
                List<String> userSkills = user.getSkills().stream().map(Skill::getTitle).toList();
                if (!userSkills.contains(skill.getTitle())) {
                    skillRepository.assignSkillToUser(skill.getId(), user.getId());
                }
            });
        });
    }

    private void validateUpdate(Goal goalToUpdate, UpdateGoalDto updateGoalDto, List<SkillDto> skillDtos) {
        if (goalToUpdate.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new IllegalArgumentException("Goal already completed");
        }
        skillDtos.forEach(skillToAchieve -> {
            if (!skillRepository.existsByTitle(skillToAchieve.getTitle())) {
                throw new IllegalArgumentException("Skill " + skillToAchieve.getTitle() + " not found");
            }
        });
    }
}
