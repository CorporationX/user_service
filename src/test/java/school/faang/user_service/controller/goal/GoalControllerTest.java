package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;
    @InjectMocks
    private GoalController goalController;

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "     ", "\t", "\n"})
    public void testGoalCreationTitleIsInValid(String title) {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                title,
                "Some description",
                null,
                null,
                List.of(1L));

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "     ", "\t", "\n"})
    public void testGoalCreationDescriptionIsInvalid(String description) {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                description,
                null,
                null,
                List.of(1L));

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void testGoalCreationUserIdsIsInvalid(List<Long> userIds) {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                userIds);

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationSuccess() {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(1L));
        when(goalService.create(createGoalDto))
                .thenReturn(new GoalDto(
                        "Some title",
                        "Some description",
                        null,
                        null,
                        List.of(1L),
                        null,
                        null));

        GoalDto goalDto = goalController.create(createGoalDto);

        assertNotNull(goalDto);
        assertEquals(createGoalDto.title(), goalDto.title());
        assertEquals(createGoalDto.description(), goalDto.description());
        assertEquals(createGoalDto.userIds(), goalDto.userIds());

        verify(goalService).create(createGoalDto);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "     ", "\t", "\n"})
    public void testGoalUpdateTitleIsInvalid(String title) {
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                title,
                "Some description",
                null,
                null,
                null);
        final long goalId = 5L;

        assertThrows(DataValidationException.class, () -> goalController.update(goalId, updateGoalDto));

        verify(goalService, never()).update(goalId, updateGoalDto);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "     ", "\t", "\n"})
    public void testGoalUpdateDescriptionIsInvalid(String description) {
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "Some title",
                description,
                null,
                null,
                null);
        final long goalId = 5L;

        assertThrows(DataValidationException.class, () -> goalController.update(goalId, updateGoalDto));

        verify(goalService, never()).update(goalId, updateGoalDto);
    }

    @Test
    public void testGoalUpdateSuccess() {
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                null);
        final long goalId = 5L;
        when(goalService.update(goalId, updateGoalDto))
                .thenReturn(new GoalDto(
                        "Some title",
                        "Some description",
                        null,
                        null,
                        null,
                        null,
                        null));

        GoalDto goalDto = goalController.update(goalId, updateGoalDto);

        assertNotNull(goalDto);
        assertEquals(updateGoalDto.title(), goalDto.title());
        assertEquals(updateGoalDto.description(), goalDto.description());

        verify(goalService).update(goalId, updateGoalDto);
    }
}
