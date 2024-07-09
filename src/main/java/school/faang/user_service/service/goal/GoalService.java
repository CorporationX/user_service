package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mappers.GoalDtoMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> filters;
    private final GoalDtoMapper goalDtoMapper;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));
        List<Long> skillsIds = goalDto.getSkillIds();
        if (goalRepository.countActiveGoalsPerUser(userId) > 2) {
            throw new RuntimeException("max number of active goals exceeded");
        } else if (!skillsIds.stream().allMatch(skillRepository::existsById)) {
            throw new RuntimeException("goal has non-existent skill");
        } else {
            Goal goal = goalDtoMapper.toEntity(goalDto);
            goal.setStatus(GoalStatus.ACTIVE);
            goal.setSkillsToAchieve(skillRepository.findAllById(skillsIds));
            goalRepository.save(goal);
            return goalDtoMapper.toDto(goal);
        }
    }

    @Transactional
    public GoalDto updateGoal(Long userId, GoalDto goalDto) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));//это подготовил
        Goal goal = goalRepository.findById(goalDto.getId()).orElseThrow(() -> new RuntimeException("goal not found"));//это подготовил
        List<Long> skillIds = goalDto.getSkillIds();
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new RuntimeException("goal is already completed");
        } else if (!skillIds.stream().allMatch(skillRepository::existsById)) {
            throw new RuntimeException("goal has non-existent skills");
        } else if (goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            goal.getUsers().forEach(user -> {
                user.getSkills().addAll(skillRepository.findAllById(skillIds));
                userRepository.save(user);
                skillIds.forEach(skillId -> skillRepository.assignSkillToUser(user.getId(), skillId));
            });
            goalRepository.save(goalDtoMapper.toEntity(goalDto));
        }
        return goalDto;
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalRepository.findById(goalId).orElseThrow(() -> new RuntimeException("goal not found"));
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtaskByGoalId(Long goalId, GoalFilterDto filter) {
        goalRepository.findById(goalId).orElseThrow(() -> new RuntimeException("goal not found"));
        if (filter.getId() == null) {
            throw new RuntimeException("id in filter not found");
        }
        Stream<Goal> resultGoals = goalRepository.findByParent(goalId);
        for (GoalFilter goalFilter : filters) {
            if (goalFilter.isApplicable(filter)) {
                resultGoals = goalFilter.apply(resultGoals, filter);
            }
        }
        return goalDtoMapper.toDtos(resultGoals.toList());
    }

    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        Stream<Goal> resultGoals = goalRepository.findGoalsByUserId(userId);
        for (GoalFilter goalFilter : filters) {
            if (goalFilter.isApplicable(filter)) {
                resultGoals = goalFilter.apply(resultGoals, filter);
            }
        }
        return goalDtoMapper.toDtos(resultGoals.toList());
    }
}
