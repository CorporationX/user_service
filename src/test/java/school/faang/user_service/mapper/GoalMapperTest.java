package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalMapperTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalMapperImpl goalMapper;

    private Goal goalActive;
    private Goal goalCompleted;
    private GoalDto goalDtoActive;
    private GoalDto goalDtoCompleted;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        goalActive = Goal.builder().id(1L).title("title1").status(GoalStatus.ACTIVE).deadline(LocalDateTime.now().plusDays(3L))
                .description("description1").build();

        goalCompleted = Goal.builder().id(2L).title("title2").status(GoalStatus.COMPLETED).deadline(LocalDateTime.now().plusDays(3L))
                .description("description2").build();

        goalDtoActive = GoalDto.builder().id(1L).description("description1").title("title1").status(GoalStatus.ACTIVE)
                .deadline(goalActive.getDeadline()).skillIds(Collections.emptyList()).build();
        goalDtoCompleted = GoalDto.builder().id(2L).description("description2").title("title2").status(GoalStatus.COMPLETED)
                .deadline(goalActive.getDeadline()).skillIds(Collections.emptyList()).build();

        Stream<Goal> goalStream = Stream.<Goal>builder().add(goalActive).add(goalCompleted).build();

        Mockito.when(goalRepository.findGoalsByUserId(Mockito.anyLong()))
                .thenReturn(goalStream);
        Mockito.when(goalRepository.findByParent(Mockito.anyLong()))
                .thenReturn(goalStream);
        Mockito.when(goalRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Goal()));
        Mockito.when(skillRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Skill()));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
    }

    @Test
    public void testMapperInvalidMentorNotFound() {
        long mentorId = 2L;
        Mockito.when(userRepository.findById(mentorId))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setMentorId(mentorId);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalMapper.convertDtoDependenciesToEntity(goalDto, goalActive));
        assertEquals("Mentor with given id was not found!", exception.getMessage());
    }

    @Test
    public void testMapperInvalidNotExistedSkill() {
        Mockito.when(skillRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(List.of(1L));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalMapper.convertDtoDependenciesToEntity(goalDto, goalActive));
        assertEquals("There is no way to add a goal with a non-existent skill!", exception.getMessage());
    }

    @Test
    public void testMapperInvalidParentNotFound() {
        long parentId = 2L;
        Mockito.when(goalRepository.findById(parentId))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setParentId(parentId);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalMapper.convertDtoDependenciesToEntity(goalDto, goalActive));
        assertEquals("Goal-parent with given id was not found!", exception.getMessage());
    }
}