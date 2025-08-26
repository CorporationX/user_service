package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.avro.common.SkillFilter;
import school.faang.avro.user.UserAddSkills;
import school.faang.user_service.config.context.AuthUserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.filter.goal.GoalFilterBuilderInterface;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.kafka.producer.UserUpdateProducer;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.policy.goal.GoalCreatePolicy;
import school.faang.user_service.policy.goal.GoalDeletePolicy;
import school.faang.user_service.policy.goal.GoalUpdatePolicy;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private static final int MIN_USERS_TO_DELETE_GOAL = 1;

    private final AuthUserContext authUserContext;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final GoalFilterBuilderInterface<Goal, FilterGoalDto> goalFilter;
    private final GoalCreatePolicy goalCreatePolicy;
    private final GoalUpdatePolicy goalUpdatePolicy;
    private final GoalDeletePolicy goalDeletePolicy;
    private final UserUpdateProducer userUpdateProducer;

    @Override
    @Transactional
    public GoalDto create(CreateGoalDto createGoalDto) {
        goalCreatePolicy.validate(createGoalDto);
        Goal goal = goalMapper.toGoal(createGoalDto);

        List<User> users = createGoalDto.userIds() == null || createGoalDto.userIds().isEmpty()
                ? Collections.emptyList()
                : userRepository.findAllById(createGoalDto.userIds());

        goal.setUsers(users);
        goal.setStatus(GoalStatus.ACTIVE);

        setGuaranteeAndSkills(createGoalDto, users, goal);

        goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    private void setGuaranteeAndSkills(CreateGoalDto createGoalDto, List<User> users, Goal goal) {
        if (createGoalDto.skillIds() != null && !createGoalDto.skillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(createGoalDto.skillIds());
            if (createGoalDto.mentorId() != null) {
                userRepository.findById(createGoalDto.mentorId())
                        .ifPresent(mentor -> {
                            List<UserSkillGuarantee> guarantees = new ArrayList<>();
                            users.forEach(user -> {
                                skills.forEach(skill -> {
                                    UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                            .user(user)
                                            .skill(skill)
                                            .guarantor(mentor)
                                            .build();
                                    guarantees.add(userSkillGuarantee);
                                });
                            });
                            userSkillGuaranteeRepository.saveAll(guarantees);
                            goal.setMentor(mentor);
                        });
            }
            goal.setSkillsToAchieve(skills);
        }
    }

    @Override
    public Page<GoalDto> get(FilterGoalDto dto, Pageable pageable) {
        Specification<Goal> specification = goalFilter.buildSpecification(dto, null);
        Page<Goal> goals = goalRepository.findAll(specification, pageable);
        return goals.map(goalMapper::toGoalDto);
    }

    @Override
    @Transactional
    public GoalDto update(Long id, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(id);
        goalUpdatePolicy.validate(updateGoalDto, goal);

        Optional.ofNullable(updateGoalDto.mentorId())
                .ifPresent(mentorId -> {
                    User mentor = userRepository.getByIdOrThrow(mentorId);
                    goal.setMentor(mentor);
                });

        Optional.ofNullable(updateGoalDto.skillIds())
                .ifPresent(skillIds -> {
                    List<Skill> skills = skillRepository.findAllById(skillIds);
                    goal.setSkillsToAchieve(skills);
                });

        goalMapper.update(goal, updateGoalDto);

        Goal updatedGoal = goalRepository.save(goal);

        if (goal.getStatus() == GoalStatus.COMPLETED && goal.getSkillsToAchieve() != null && goal.getUsers() != null) {
            goal.getUsers().forEach(user -> {
                List<SkillFilter> skills = skillMapper.toSkillFilterDtos(goal.getSkillsToAchieve());

                skills.forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));

                userUpdateProducer.onUserAddSkills(
                        UserAddSkills.newBuilder()
                                .setId(user.getId())
                                .setSkills(skills)
                                .build()
                );
            });
        }

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Override
    public void delete(Long id) {
        Goal goal = goalRepository.getByIdOrThrow(id);
        goalDeletePolicy.validate(goal);
        long usersSize = goal.getUsers().size();
        long currentUserId = authUserContext.getUserId();
        boolean isMentor = goal.getMentor() != null
                           && goal.getMentor().getId().equals(currentUserId);
        if (isMentor || usersSize <= MIN_USERS_TO_DELETE_GOAL) {
            goalRepository.deleteById(id);
        } else {
            goalRepository.deleteUserFromGoal(currentUserId, goal.getId());
        }
    }
}
