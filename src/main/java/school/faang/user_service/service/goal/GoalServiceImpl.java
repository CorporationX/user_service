package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.TooManyGoalsException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GoalServiceImpl implements GoalService {
    private static final int MAX_GOALS_AMOUNT = 3;

    private GoalRepository goalRepository;
    private UserRepository userRepository;
    private SkillRepository skillRepository;
    private SkillService skillService;
    private GoalMapper goalMapper;
    private SkillMapper skillMapper;
    private List<GoalFilterDto> filters;


    @Override
    public Stream<Goal> findGoalsByUserId(long userId) {
        return null;
    }

    @Override
    public Goal create(String title, String description, long parent) {
        return null;
    }

    @Override
    public int countActiveGoalsPerUser(long userId) {
        return 0;
    }

    @Override
    public Stream<Goal> findByParent(long goalId) {
        return goalRepository.findByParent(goalId);
    }

    @Override
    public int countUsersSharingGoal(long goalId) {
        return 0;
    }

    @Override
    public List<User> findUsersByGoalId(long goalId) {
        return null;
    }

    @Override
    public void removeSkillsFromGoal(long goalId) {

    }

    @Override
    public void addSkillToGoal(long skillId, long goalId) {

    }

    public GoalDto createGoal(Long userId, Goal goal) {

        User user = getUser(userId);
        validateGoalCreation(user, goal);

        saveGoalSkills(goal);
        Goal goalToCreate = goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());

        return goalMapper.toDto(goalToCreate);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found"));

        if (goalToUpdate.getStatus().equals("COMPLETED")) {
            throw new ValidationException("Goal is already completed");
        }

        if (!allSkillsExist(goalToUpdate)) {
            throw new ValidationException("Goal contains skills that don't exist");
        }

        if (goalDto.getStatus().equals("COMPLETED")) {
            updateSkills(goalId, goalDto);
        }

        goalMapper.update(goalDto, goalToUpdate);
        goalRepository.save(goalToUpdate);
        return goalMapper.toDto(goalToUpdate);
    }

    public void deleteGoal(long goalId) {
        Goal goal = goalRepository
                .findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id: " + goalId + "not found"));

        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        Stream<Goal> subtasks = findByParent(goalId);

        if (subtasks.findAny().isEmpty()) {
            throw new EntityNotFoundException("No subtasks found for goal with id: " + goalId);
        }

        return subtasks
                .sorted(Comparator.comparing(Goal::getId))
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
    }

    private void updateSkills(Long goalId, GoalDto goalDto) {
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        for (User user : users) {
            user.getSkills().add((Skill) goalDto.getSkillIds());
        }
    }

    private void saveGoalSkills(Goal goal) {
        List<Skill> skills = goal.getSkillsToAchieve().stream()
                .peek(skill -> skill.getGoals().add(goal))
                .toList();

        skillService.saveAll(skills);
    }

    private void validateGoalCreation(User user, Goal goal) {
        int goalsAmount = user.getGoals().size();

        if (goalsAmount >= MAX_GOALS_AMOUNT) {
            throw new TooManyGoalsException();
        }

        if (!allSkillsExist(goal)) {
            throw new EntityNotFoundException("Skill not exist");
        }
    }

    private boolean allSkillsExist(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .allMatch(skillService::checkActiveSkill);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto goalFilterDto) {
        Stream<Goal> userGoalsStream = goalRepository.findGoalsByUserId(userId);
        return applyFilterAndMapToDto(userGoalsStream, goalFilterDto);
    }

    private List<GoalDto> applyFilterAndMapToDto(Stream<Goal> userGoalsStream, GoalFilterDto filter) {
        List<GoalDto> filteredGoals = userGoalsStream
                .filter(goal -> matchesFilter(goal, filter))
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
        return filteredGoals;
    }

    private boolean matchesFilter(Goal goal, GoalFilterDto filter) {
        if (filter == null) {
            return true;
        }
        if (filter.getTitle() != null && !goal.getTitle().contains(filter.getTitle())) {
            return false;
        }
        if (filter.getGoalStatus() != null && goal.getStatus() != filter.getGoalStatus()) {
            return false;
        }
        return true;
    }
}
