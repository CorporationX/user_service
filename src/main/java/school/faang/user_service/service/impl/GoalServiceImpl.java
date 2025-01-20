package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.request.CreateGoalRequestDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.CreateGoalResponseDto;
import school.faang.user_service.dto.response.GoalDto;
import school.faang.user_service.dto.entity.goal.Goal;
import school.faang.user_service.repository.genericSpecification.GenericSpecification;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;

import java.util.List;

import static school.faang.user_service.constants.AppConstants.MAX_COUNT_OF_ACTIVE_GOALS;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;

    @Override
    @Transactional
    public CreateGoalResponseDto createGoal(Long userId, CreateGoalRequestDto request) {
        validateActiveGoalLimit(userId);
        validateSkills(request.getSkillsToAchieveIds());
        Goal savedGoal = goalRepository.createGoalWithMentor(request.getTitle(), request.getDescription(),
                request.getParentId(), request.getMentorId());
        associateSkillsWithGoal(savedGoal.getId(), request.getSkillsToAchieveIds());
        associateGoalWithUsers(savedGoal.getId(), request.getUserIds());
        return goalMapper.toCreateGoalResponseDto(savedGoal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException(String.format("Goal with ID %d does not exist.", goalId));
        }
        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.removeUsersFromGoal(goalId);
        goalRepository.deleteById(goalId);
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(Long parentGoalId) {
        return goalMapper.toDto(goalRepository.findAllByParentId(parentGoalId));
    }

    @Override
    public List<GoalDto> search(SearchRequest request) {
        GenericSpecification<Goal> spec = new GenericSpecification<>(
                Goal.class, request.getRootGroup(), request.getSort());
        return goalMapper.toDto(goalRepository.findAll(spec));
    }

    private void validateActiveGoalLimit(Long userId) {
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals > MAX_COUNT_OF_ACTIVE_GOALS) {
            throw new IllegalArgumentException(String.format("User with ID %d cannot have more than %d active goals.",
                    userId, MAX_COUNT_OF_ACTIVE_GOALS));
        }
    }

    private void validateSkills(List<Long> skillIds) {
        int existingSkills = skillRepository.countExisting(skillIds);
        if (existingSkills != skillIds.size()) {
            throw new IllegalArgumentException("Some of the provided skills do not exist in the database.");
        }
    }

    private void associateSkillsWithGoal(Long goalId, List<Long> skillIds) {
        skillIds.forEach(skillId -> skillRepository.assignSkillToGoal(goalId, skillId));
    }

    private void associateGoalWithUsers(Long goalId, List<Long> userIds) {
        userIds.forEach(userId -> goalRepository.assignGoalToUser(userId, goalId));
    }

}
