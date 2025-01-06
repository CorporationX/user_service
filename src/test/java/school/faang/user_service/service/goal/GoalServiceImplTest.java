package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.request.CreateGoalRequestDto;
import school.faang.user_service.dto.request.FilterGroupRequest;
import school.faang.user_service.dto.request.FilterRequest;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.CreateGoalResponseDto;
import school.faang.user_service.dto.response.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.genericSpecification.GenericSpecification;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.impl.GoalServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.enums.FilterOperation.EQUAL;
import static school.faang.user_service.testConstants.GoalTestConstants.ACTIVE_GOALS_COUNT;
import static school.faang.user_service.testConstants.GoalTestConstants.ACTIVE_GOAL_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.GoalTestConstants.ASSERT_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.GoalTestConstants.EXCEEDING_ACTIVE_GOALS;
import static school.faang.user_service.testConstants.GoalTestConstants.EXISTING_SKILLS_COUNT;
import static school.faang.user_service.testConstants.GoalTestConstants.FILTER_FIELD;
import static school.faang.user_service.testConstants.GoalTestConstants.GOAL_DESCRIPTION;
import static school.faang.user_service.testConstants.GoalTestConstants.GOAL_DOES_NOT_EXIST_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.GoalTestConstants.GOAL_TITLE;
import static school.faang.user_service.testConstants.GoalTestConstants.INVALID_GOAL_ID;
import static school.faang.user_service.testConstants.GoalTestConstants.MAX_ACTIVE_GOALS;
import static school.faang.user_service.testConstants.GoalTestConstants.PARENT_GOAL_ID;
import static school.faang.user_service.testConstants.GoalTestConstants.SKILL_IDS;
import static school.faang.user_service.testConstants.GoalTestConstants.USER_ID;
import static school.faang.user_service.testConstants.GoalTestConstants.USER_IDS;
import static school.faang.user_service.testConstants.GoalTestConstants.VALID_GOAL_ID;
import static school.faang.user_service.testConstants.GoalTestConstants.VALID_SKILLS_EXCEPTION_MESSAGE;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @InjectMocks
    GoalServiceImpl goalService;

    @Mock
    GoalRepository goalRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    GoalMapper goalMapper;

    private GenericSpecification<Goal> goalGenericSpecification;
    private CreateGoalResponseDto createGoalResponseDto;
    private CreateGoalRequestDto createGoalRequestDto;
    private FilterGroupRequest filterGroupRequest;
    private FilterRequest filterRequest;
    private SearchRequest searchRequest;
    private GoalDto goalDto;
    private Goal goal;

    @BeforeEach
    public void init() {
        // filterRequest
        filterRequest = new FilterRequest();
        filterRequest.setField(FILTER_FIELD);
        filterRequest.setOperation(EQUAL);
        filterRequest.setValue(VALID_GOAL_ID);

        // filterGroupRequest
        filterGroupRequest = new FilterGroupRequest();
        filterGroupRequest.setGroupOperator("AND");
        filterGroupRequest.setFilters(List.of(filterRequest));

        // searchRequest
        searchRequest = new SearchRequest();
        searchRequest.setRootGroup(filterGroupRequest);

        // goalGenericSpecification
        goalGenericSpecification = new GenericSpecification<>(Goal.class, filterGroupRequest, null);

        // createGoalRequestDto
        createGoalRequestDto = new CreateGoalRequestDto();
        createGoalRequestDto.setTitle(GOAL_TITLE);
        createGoalRequestDto.setDescription(GOAL_DESCRIPTION);
        createGoalRequestDto.setSkillsToAchieveIds(SKILL_IDS);
        createGoalRequestDto.setUserIds(USER_IDS);

        // goal
        goal = new Goal();
        goal.setId(VALID_GOAL_ID);
        goal.setTitle(GOAL_TITLE);
        goal.setDescription(GOAL_DESCRIPTION);

        // goalDto
        goalDto = new GoalDto();
        goalDto.setId(VALID_GOAL_ID);
        goalDto.setParentId(PARENT_GOAL_ID);
        goalDto.setTitle(GOAL_TITLE);
        goalDto.setDescription(GOAL_DESCRIPTION);
        goalDto.setSkillIds(SKILL_IDS);

        // createGoalResponseDto
        createGoalResponseDto = new CreateGoalResponseDto();
        createGoalResponseDto.setTitle(GOAL_TITLE);
        createGoalResponseDto.setDescription(GOAL_DESCRIPTION);
    }

    @Test
    public void testValidateActiveGoalLimit_whenActiveIsMoreThenThree_thenThrowsException() {
        // Arrange
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(EXCEEDING_ACTIVE_GOALS);
        String expectedMessage = String.format(ACTIVE_GOAL_EXCEPTION_MESSAGE, USER_ID, MAX_ACTIVE_GOALS);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(USER_ID, createGoalRequestDto), ASSERT_EXCEPTION_MESSAGE);

        // Assert
        assertEquals(expectedMessage, exception.getMessage(), "Exception message did not match expected value");

        verify(goalRepository, times(1)).countActiveGoalsPerUser(anyLong());
        verifyNoMoreInteractions(goalRepository);
        verifyNoInteractions(skillRepository, goalMapper);
    }

    @Test
    public void testValidateSkills_whenExistingSkillsCountDoesNotMatchWithProvidedSize_thenThrowsException() {
        // Arrange
        when(skillRepository.countExisting(SKILL_IDS)).thenReturn(EXISTING_SKILLS_COUNT);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(USER_ID, createGoalRequestDto), ASSERT_EXCEPTION_MESSAGE);

        // Assert
        assertEquals(VALID_SKILLS_EXCEPTION_MESSAGE, exception.getMessage());

        // verify interactions
        verify(goalRepository, times(1)).countActiveGoalsPerUser(anyLong());
        verify(skillRepository, times(1)).countExisting(SKILL_IDS);
        verifyNoMoreInteractions(skillRepository);
        verifyNoInteractions(goalMapper);
    }

    @Test
    public void testCreateGoal_whenValidRequest_thenSavesGoalAndReturnsResponse() {
        // Arrange
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(ACTIVE_GOALS_COUNT);
        when(skillRepository.countExisting(SKILL_IDS)).thenReturn(SKILL_IDS.size());
        when(goalRepository.createGoalWithMentor(anyString(), anyString(), any(), any())).thenReturn(goal);
        when(goalMapper.toCreateGoalResponseDto(goal)).thenReturn(createGoalResponseDto);

        // Act
        CreateGoalResponseDto result = goalService.createGoal(USER_ID, createGoalRequestDto);

        // Assert
        assertEquals(createGoalResponseDto, result);

        // Verify interactions
        verify(goalRepository, times(1)).countActiveGoalsPerUser(anyLong());
        verify(goalRepository, times(1))
                .createGoalWithMentor(anyString(), anyString(), any(), any());
        verify(skillRepository, times(1)).countExisting(SKILL_IDS);
        verify(goalMapper, times(1)).toCreateGoalResponseDto(goal);
    }

    @Test
    public void testDeleteGoal_whenInvalidGoalId_thenThrowsException() {
        // Arrange
        when(goalRepository.existsById(INVALID_GOAL_ID)).thenReturn(false);
        String expectedMessage = String.format(GOAL_DOES_NOT_EXIST_EXCEPTION_MESSAGE, INVALID_GOAL_ID);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.deleteGoal(INVALID_GOAL_ID), ASSERT_EXCEPTION_MESSAGE);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());

        // Verify interactions
        verify(goalRepository, times(1)).existsById(INVALID_GOAL_ID);
        verifyNoMoreInteractions(goalRepository);
    }

    @Test
    public void testDeleteGoal_whenValidGoalId_thenSuccess() {
        // Arrange
        when(goalRepository.existsById(VALID_GOAL_ID)).thenReturn(true);

        // Act
        goalService.deleteGoal(VALID_GOAL_ID);

        // Verify interactions
        verify(goalRepository, times(1)).existsById(VALID_GOAL_ID);
        verify(goalRepository, times(1)).removeSkillsFromGoal(VALID_GOAL_ID);
        verify(goalRepository, times(1)).removeUsersFromGoal(VALID_GOAL_ID);
        verify(goalRepository, times(1)).deleteById(VALID_GOAL_ID);
        verifyNoMoreInteractions(goalRepository);
        verifyNoInteractions(goalMapper);
    }

    @Test
    public void findSubtasksByGoalId_whenParentGoalId_thenListOfGoalDto() {
        // Arrange
        when(goalRepository.findAllByParentId(PARENT_GOAL_ID)).thenReturn(List.of(goal));
        when(goalMapper.toDto(List.of(goal))).thenReturn(List.of(goalDto));

        // Act
        List<GoalDto> actualResult = goalService.findSubtasksByGoalId(PARENT_GOAL_ID);

        // Assert
        assertEquals(List.of(goalDto), actualResult);

        // Verify interactions
        verify(goalRepository, times(1)).findAllByParentId(VALID_GOAL_ID);
        verify(goalMapper, times(1)).toDto(List.of(goal));
        verifyNoMoreInteractions(goalRepository, goalMapper);
    }

    @Test
    public void testSearch_whenValidSearchRequest_thenListOfGoalDto() {
        // Arrange
        when(goalRepository.findAll(any(GenericSpecification.class))).thenReturn(List.of(goal));
        when(goalMapper.toDto(List.of(goal))).thenReturn(List.of(goalDto));

        // Act
        List<GoalDto> actualResult = goalService.search(searchRequest);

        // Assert
        assertEquals(List.of(goalDto), actualResult);

        // Verify interactions
        verify(goalRepository, times(1)).findAll(any(GenericSpecification.class));
        verify(goalMapper, times(1)).toDto(List.of(goal));
        verifyNoMoreInteractions(goalRepository, goalMapper);
    }

}
