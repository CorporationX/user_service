package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import school.faang.user_service.service.goal.GoalServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {
    public static final Long MENTOR_ID = 1L;
    public static final Long USER_ID = 2L;
    public static final Long GOAL_ID = 1L;

    @InjectMocks
    private GoalServiceImpl goalService;

    @Spy
    private GoalMapper goalMapperImpl = Mappers.getMapper(GoalMapper.class);
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Mock
    private AuthUserContext authUserContext;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserUpdateProducer userUpdateProducer;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private GoalCreatePolicy goalCreatePolicy;
    @Mock
    private GoalUpdatePolicy goalUpdatePolicy;
    @Mock
    private GoalDeletePolicy goalDeletePolicy;
    @Mock
    private GoalFilterBuilderInterface<Goal, FilterGoalDto> goalFilter;
    @Captor
    private ArgumentCaptor<Specification<Goal>> specCaptor;
    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> guaranteesCaptor;
    @Captor
    private ArgumentCaptor<Goal> goalCaptor;


    @Test
    void testGetGoalsWithPagination() {
        FilterGoalDto dto = new FilterGoalDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        when(goalFilter.buildSpecification(dto, null)).thenReturn(mock(Specification.class));
        when(goalRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<GoalDto> result = goalService.get(dto, pageable);

        verify(goalFilter).buildSpecification(dto, null);
        verify(goalRepository).findAll(any(Specification.class), eq(pageable));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteGoalsByMentor() {
        long id = GOAL_ID + 1L;
        Goal findedGoal = createGoal(GOAL_ID, MENTOR_ID, 1, 0);
        when(goalRepository.getByIdOrThrow(id)).thenReturn(findedGoal);
        when(authUserContext.getUserId()).thenReturn(MENTOR_ID);

        goalService.delete(id);

        verify(goalRepository, times(1)).deleteById(id);
        verify(goalDeletePolicy, times(1)).validate(findedGoal);
        verify(goalRepository, times(0)).deleteUserFromGoal(MENTOR_ID, findedGoal.getId());
    }

    @Test
    public void testDeleteGoalsWhenOnlyOneParticipant() {
        long id = GOAL_ID + 1L;
        Goal findedGoal = createGoal(GOAL_ID, USER_ID, 1, 0);
        when(goalRepository.getByIdOrThrow(id)).thenReturn(findedGoal);
        when(authUserContext.getUserId()).thenReturn(USER_ID);

        goalService.delete(id);

        verify(goalRepository, times(1)).deleteById(id);
        verify(goalDeletePolicy, times(1)).validate(findedGoal);
        verify(goalRepository, times(0)).deleteUserFromGoal(USER_ID, findedGoal.getId());
    }

    @Test
    public void testDeleteGoalsWhenMoreThenOneParticipant() {
        Goal findedGoal = createGoal(GOAL_ID, 1L, 2, 0);
        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(findedGoal);
        when(authUserContext.getUserId()).thenReturn(USER_ID);

        goalService.delete(GOAL_ID);

        verify(goalRepository, times(0)).deleteById(GOAL_ID);
        verify(goalDeletePolicy, times(1)).validate(findedGoal);
        verify(goalRepository, times(1)).deleteUserFromGoal(USER_ID, findedGoal.getId());
    }

    @Test
    public void testUpdateIncompleteGoalWithValidData() {
        Goal findedGoal = createGoal(GOAL_ID, MENTOR_ID, 2, 0);
        UpdateGoalDto dto = new UpdateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(1),
                MENTOR_ID,
                GoalStatus.ACTIVE,
                List.of(1L, 2L)
        );
        List<Skill> skills = dto.skillIds().stream().map(skillId -> {
            Skill skill = new Skill();
            skill.setId(skillId);
            return skill;
        }).toList();
        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(findedGoal);
        when(userRepository.getByIdOrThrow(MENTOR_ID)).thenReturn(findedGoal.getMentor());
        when(skillRepository.findAllById(dto.skillIds())).thenReturn(skills);

        goalService.update(GOAL_ID, dto);

        verify(goalRepository, times(1)).getByIdOrThrow(GOAL_ID);
        verify(goalUpdatePolicy, times(1)).validate(dto, findedGoal);
        verify(skillRepository, times(1)).findAllById(dto.skillIds());
        verify(userRepository, times(1)).getByIdOrThrow(dto.mentorId());
        assertEquals(MENTOR_ID, findedGoal.getMentor().getId());
        assertEquals(findedGoal.getSkillsToAchieve(), skills);
        verify(goalMapperImpl, times(1)).update(findedGoal, dto);
        verify(goalRepository, times(1)).save(findedGoal);
    }

    @Test
    public void testUpdateIncompleteGoalWithEmptyData() {
        Goal findedGoal = createGoal(GOAL_ID, null, 0, 0);
        UpdateGoalDto dto = new UpdateGoalDto(
                null, null,
                null, null,
                null, null
        );
        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(findedGoal);

        goalService.update(GOAL_ID, dto);

        verify(goalRepository, times(1)).getByIdOrThrow(GOAL_ID);
        verify(goalUpdatePolicy, times(1)).validate(dto, findedGoal);
        verify(skillRepository, times(0)).findAllById(anyList());
        verify(userRepository, times(0)).getByIdOrThrow(anyLong());
        assertEquals(null, findedGoal.getMentor());
        assertEquals(null, findedGoal.getSkillsToAchieve());
        verify(goalMapperImpl, times(1)).update(findedGoal, dto);
        verify(goalRepository, times(1)).save(findedGoal);
    }

    @Test
    public void testUpdateCompleteGoalWithUsersAndSkills() {
        Goal findedGoal = createGoal(GOAL_ID, MENTOR_ID, 2, 2);
        UpdateGoalDto dto = new UpdateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(1),
                MENTOR_ID,
                GoalStatus.COMPLETED,
                List.of(1L, 2L)
        );
        List<Skill> skills = dto.skillIds().stream().map(skillId -> {
            Skill skill = new Skill();
            skill.setId(skillId);
            skill.setTitle("title " + skillId);
            return skill;
        }).toList();
        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(findedGoal);
        when(userRepository.getByIdOrThrow(MENTOR_ID)).thenReturn(findedGoal.getMentor());
        when(skillRepository.findAllById(dto.skillIds())).thenReturn(skills);

        goalService.update(GOAL_ID, dto);

        findedGoal.getUsers().forEach(user -> {
            findedGoal.getSkillsToAchieve().forEach(skill -> {
                verify(skillRepository, times(1)).assignSkillToUser(skill.getId(), user.getId());
            });
        });
        verify(userUpdateProducer, times(findedGoal.getUsers().size())).onUserAddSkills(any(UserAddSkills.class));
        verify(skillMapper, times(skills.size())).toSkillFilterDtos(anyList());
    }

    @Test
    public void testUpdateCompleteGoalWithoutUsersAndSkills() {
        Goal findedGoal = createGoal(GOAL_ID, MENTOR_ID, 0, 0);
        UpdateGoalDto dto = new UpdateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(1),
                MENTOR_ID,
                GoalStatus.COMPLETED,
                List.of(1L, 2L)
        );
        List<Skill> skills = dto.skillIds().stream().map(skillId -> {
            Skill skill = new Skill();
            skill.setId(skillId);
            return skill;
        }).toList();
        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(findedGoal);
        when(userRepository.getByIdOrThrow(MENTOR_ID)).thenReturn(findedGoal.getMentor());
        when(skillRepository.findAllById(dto.skillIds())).thenReturn(skills);

        goalService.update(GOAL_ID, dto);

        verify(skillRepository, times(0)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    public void testCreateGoalWithFullData() {
        User mentor = new User();
        mentor.setId(MENTOR_ID);
        List<Long> userIds = List.of(1L, 2L);
        List<Long> skillIds = List.of(3L, 4L);
        when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        when(skillRepository.findAllById(skillIds)).thenReturn(skillIds.stream()
                .map(skillId -> {
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                })
                .toList()
        );
        when(userRepository.findAllById(userIds)).thenReturn(userIds.stream()
                .map(userId -> {
                    User user = new User();
                    user.setId(userId);
                    return user;
                })
                .toList()
        );
        CreateGoalDto dto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(1),
                MENTOR_ID,
                userIds,
                skillIds
        );

        goalService.create(dto);

        verify(goalCreatePolicy, times(1)).validate(dto);
        verify(userRepository, times(1)).findAllById(dto.userIds());
        verify(skillRepository, times(1)).findAllById(dto.skillIds());
        verify(userRepository, times(1)).findById(MENTOR_ID);
        verify(userSkillGuaranteeRepository, times(1)).saveAll(guaranteesCaptor.capture());
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal createdGoal = goalCaptor.getValue();
        assertEquals(MENTOR_ID, createdGoal.getMentor().getId());
        assertEquals(skillIds, createdGoal.getSkillsToAchieve().stream().mapToLong(Skill::getId).boxed().toList());
        assertEquals(skillIds.size(), createdGoal.getUsers().size());
    }

    @Test
    public void testCreateGoalWithMinimumData() {
        CreateGoalDto dto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(1),
                null,
                null,
                null
        );

        goalService.create(dto);

        verify(goalCreatePolicy, times(1)).validate(dto);
        verify(userRepository, times(0)).findAllById(anyList());
        verify(skillRepository, times(0)).findAllById(anyList());
        verify(userRepository, times(0)).findById(anyLong());
        verify(userSkillGuaranteeRepository, times(0)).saveAll(guaranteesCaptor.capture());
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal createdGoal = goalCaptor.getValue();
        assertNull(createdGoal.getMentor());
        assertNull(createdGoal.getSkillsToAchieve());
        assertEquals(Collections.emptyList(), createdGoal.getUsers());
    }

    private Goal createGoal(Long goalId, Long mentorId, int participantCount, int skillsCount) {
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setStatus(GoalStatus.ACTIVE);
        if (mentorId != null) {
            User mentor = new User();
            mentor.setId(mentorId);
            goal.setMentor(mentor);
        }
        if (skillsCount > 0) {
            List<Skill> skills = new ArrayList<>();
            for (long i = 1; i <= skillsCount; i++) {
                Skill skill = new Skill();
                skill.setId(i);
                skills.add(skill);
            }
            goal.setSkillsToAchieve(skills);
        }
        if (participantCount > 0) {
            List<User> users = new ArrayList<>();
            for (long i = 1; i <= participantCount; i++) {
                User newUser = new User();
                newUser.setId(i);
                users.add(newUser);
            }
            goal.setUsers(users);
        }
        return goal;
    }

}