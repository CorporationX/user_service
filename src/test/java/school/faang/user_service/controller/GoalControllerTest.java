package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.Collections;
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

    @Test
    public void testGoalCreationTitleIsNull() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                null,
                "Some description",
                null,
                null,
                List.of(1L));

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationTitleIsBlank() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "    ",
                "Some description",
                null,
                null,
                List.of(1L));

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationDescriptionIsNull() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                null,
                null,
                null,
                List.of(1L));

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationDescriptionIsBlank() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "     ",
                null,
                null,
                List.of(1L));

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationUserIdsIsNull() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                null);

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationUserIdsIsEmpty() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                Collections.emptyList());

        assertThrows(DataValidationException.class, () -> goalController.create(createGoalDto));

        verify(goalService, never()).create(createGoalDto);
    }

    @Test
    public void testGoalCreationSuccess() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
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
}
