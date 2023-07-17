package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        GoalValidator.validateUserId(userId);

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);

        if (filter == null) {
            return goalStream.map(goalMapper::toDto).toList();
        }

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goalStream = goalFilter.apply(goalStream, filter);
            }
        }

        return goalStream.map(goalMapper::toDto).toList();
    }

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        GoalValidator.validateUserId(userId);
        GoalValidator.validateGoal(goalDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with given id was not found!"));
        GoalValidator.validateAdditionGoalToUser(user, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);
        convertDtoDependenciesToEntity(goalDto, goal);
        goal.setUsers(new ArrayList<>());
        goal.getUsers().add(user);

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        GoalValidator.validateGoalId(goalId);
        GoalValidator.validateGoal(goalDto);
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal with given id was not found!"));
        GoalValidator.validateUpdatingGoal(goalToUpdate);

        Goal goal = goalMapper.toEntity(goalDto);
        goal.setId(goalId);
        convertDtoDependenciesToEntity(goalDto, goal);

        checkGoalCompletionAndAssignmentSkills(goalToUpdate, goal);

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    private void checkGoalCompletionAndAssignmentSkills(Goal goalToUpdate, Goal goal) {
        if (goalToUpdate.getStatus() == GoalStatus.ACTIVE && goal.getStatus() == GoalStatus.COMPLETED) {
            List<Skill> skills = goal.getSkillsToAchieve();
            if (skills == null) {
                return;
            }
            List<User> usersCompletedGoal = goalRepository.findUsersByGoalId(goalToUpdate.getId());

            usersCompletedGoal.forEach(user -> {
                skills.forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
            });
        }
    }

    private void convertDtoDependenciesToEntity(GoalDto goalDto, Goal goal) {
        if (goalDto.getMentorId() != null) {
            Optional<User> mentorOptional = userRepository.findById(goalDto.getMentorId());
            if (mentorOptional.isEmpty()) {
                throw new DataValidationException("Mentor with given id was not found!");
            }
            goal.setMentor(mentorOptional.get());
        }

        if (goalDto.getParentId() != null) {
            Optional<Goal> goalOptional = goalRepository.findById(goalDto.getParentId());
            if (goalOptional.isEmpty()) {
                throw new DataValidationException("Goal-parent with given id was not found!");
            }
            goal.setParent(goalOptional.get());
        }

        if (goalDto.getSkillIds() != null) {
            List<Skill> skills = new ArrayList<>();
            goalDto.getSkillIds().forEach(skillId -> {
                Optional<Skill> optionalSkill = skillRepository.findById(skillId);
                if (optionalSkill.isEmpty())
                    throw new DataValidationException("There is no way to add a goal with a non-existent skill!");
                skills.add(optionalSkill.get());
            });
            goal.setSkillsToAchieve(skills);
        }
    }
}
