package school.faang.user_service.service.goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalMapper goalMapper;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalValidator goalValidator;

    private static final int MAX_ACTIVE_GOALS_PER_USER = 3;

    @Transactional
    public void createGoal(Long userId, GoalDto goalDto) {
        Goal goal = goalMapper.goalDtoToGoal(goalDto);

        goalValidator.validateGoal(goalDto);

        // Проверка на максимальное количество активных целей
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals >= MAX_ACTIVE_GOALS_PER_USER) {
            throw new IllegalArgumentException("Пользователь не может иметь более " + MAX_ACTIVE_GOALS_PER_USER + " активных целей");

        }

        // Проверка существования навыков
        List<Long> skillIds = goalDto.getSkillIds();
        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("Навыки не могут быть пустыми");
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            throw new IllegalArgumentException("Некоторые навыки не существуют");
        }
        goal.setSkillsToAchieve(skills);

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        goal.setUsers(Collections.singletonList(creator));
        goal.setStatus(GoalStatus.ACTIVE);

        goalRepository.save(goal);
    }

    @Transactional
    public void updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена"));

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Нельзя обновлять завершенную цель");
        }

        goalValidator.validateGoal(goalDto);

        // Проверка существования навыков
        List<Long> skillIds = goalDto.getSkillIds();
        if (skillIds == null || skillIds.isEmpty()) {
            // Если список навыков пустой или null, очищаем текущий список навыков цели
            existingGoal.getSkillsToAchieve().clear();
        } else {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            if (skills.size() != skillIds.size()) {
                throw new IllegalArgumentException("Некоторые навыки не существуют");
            }
            // Очищаем текущий список навыков и добавляем новые
            existingGoal.getSkillsToAchieve().clear();
            existingGoal.getSkillsToAchieve().addAll(skills);
        }

        // Обновление полей цели
        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());
        existingGoal.setDeadline(goalDto.getDeadline());

        // Обработка изменения статуса
        if (goalDto.getStatus() == GoalStatus.COMPLETED && existingGoal.getStatus() != GoalStatus.COMPLETED) {
            existingGoal.setStatus(GoalStatus.COMPLETED);
            // Присвоение навыков всем участникам
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            List<Skill> skills = existingGoal.getSkillsToAchieve();
            users.forEach(user -> user.getSkills().addAll(skills));
            userRepository.saveAll(users);
        } else {
            existingGoal.setStatus(goalDto.getStatus());
        }

        goalRepository.save(existingGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Цель с ID " + goalId + " не найдена"));
        goalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findSubtasksByGoalId(long goalId, String titleFilter) {
        Stream<Goal> subTasksStream = goalRepository.findByParent(goalId);

        if (titleFilter != null && !titleFilter.isEmpty()) {
            subTasksStream = subTasksStream.filter(goal -> goal.getTitle().contains(titleFilter));
        }

        List<GoalDto> result = subTasksStream
                .map(goalMapper::goalToGoalDto)
                .collect(Collectors.toList());

        subTasksStream.close();
        return result;
    }
    @Transactional(readOnly = true)
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter) {

        List<Goal> goalList = goalRepository.findGoalsByUserId(userId);

        if (filter == null) {
            // Если фильтр отсутствует, возвращаем все цели пользователя
            return goalList.stream()
                    .map(goalMapper::goalToGoalDto)
                    .collect(Collectors.toList());
        }

        return goalList.stream()
                .filter(goal -> applyFilter(goal, filter))
                .map(goalMapper::goalToGoalDto)
                .collect(Collectors.toList());
    }

    private boolean applyFilter(Goal goal, GoalFilterDto filter) {
        if (filter.getTitle() != null && (goal.getTitle() == null || !goal.getTitle().contains(filter.getTitle()))) {
            return false;
        }
        if (filter.getStatus() != null && goal.getStatus() != filter.getStatus()) {
            return false;
        }
        if (filter.getSkillId() != null && goal.getSkillsToAchieve().stream()
                .noneMatch(skill -> Objects.equals(skill.getId(), filter.getSkillId()))) {
            return false;
        }
        return true;
    }
}