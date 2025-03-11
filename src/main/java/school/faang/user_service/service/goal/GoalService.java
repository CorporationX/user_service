package school.faang.user_service.service.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;
    private final GoalValidator goalValidator;
    private final SkillValidator skillValidator;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;


    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoalDto(goalDto);

        goalValidator.validateCountGoals(goalRepository.countActiveGoalsPerUser(userId), userId);

        List<Long> skillIds = goalDto.getSkillIds();
        skillValidator.validSkills(skillIds, userId);

        skillIds.forEach(this::isExistsSkillId);

        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        log.info("Цель успешно создана для пользователя с идентификатором: {}", userId);

        for (Long skillId : goalDto.getSkillIds()) {
            goalRepository.addSkillToGoal(skillId, goal.getId());
        }

        return goalMapper.toDto(goal);
    }

    private void isExistsSkillId(Long skillId) {
        if (!skillService.existsById(skillId)) {
            log.error("Навык с ID {} отсутствует", skillId);
            throw new IllegalArgumentException("Навык с ID не найден" + skillId);
        }
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Цель не найдена у id: " + goalId));

        goalValidator.validateUpdateStatus(existingGoal, goalDto);
        validation(goalDto);

        goalDto.setStatus(GoalStatus.COMPLETED);

        List<User> users = new ArrayList<>();
        for (Long userId : goalDto.getUsersId()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            users.add(user);
        }
        List<Skill> skills = new ArrayList<>();
        for (Long skillId : goalDto.getSkillIds()) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));
            skills.add(skill);
        }
        for (User user : users) {
            for (Skill skill : skills) {
                user.getSkills().add(skill);
            }
        }

        return goalDto;

    }

    private void validation(GoalDto goalDto) {
        GoalStatus goalStatus = goalDto.getStatus();
        if (goalStatus != GoalStatus.ACTIVE) {
            throw new RuntimeException("цель уже завершена");
        }
        List<Long> skillIds = goalDto.getSkillIds();
        for (Long skillId : skillIds) {
            skillRepository.findById(skillId)
                    .orElseThrow(() -> new NotFoundException("скил не найден"));
        }
    }

    ч

    private void updateSkillsForGoal(Goal goal, List<Long> newSkillIds) {
        goalRepository.removeSkillsFromGoal(goal.getId());
        newSkillIds.forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
    }

    private void deleteGoal(long goalId) {
        goalValidator.validateGoal(goalId);

        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.deleteById(goalId);

    }
}