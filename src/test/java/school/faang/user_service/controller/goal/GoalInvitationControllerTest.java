package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.util.goal.validator.GoalInvitationControllerValidator;

class GoalInvitationControllerTest {

    @Mock
    GoalInvitationService goalService;

    @Spy
    GoalInvitationControllerValidator controllerValidator;

    @InjectMocks
    GoalInvitationController goalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInvitation_InputsAreCorrect_ShouldSaveGoalInvitation() {
        goalController.createInvitation(buildGoalInvitationDto());

        Mockito.verify(goalService, Mockito.times(1)).createInvitation(buildGoalInvitationDto());
    }

    @Test
    void testCreateInvitation_InputsAreIncorrect_StatusShouldNotBeOk() {
        GoalInvitationDto dto = buildGoalInvitationDto().builder().inviterId(-1L).build();
        Mockito.doThrow(RuntimeException.class).when(controllerValidator).validateInvitation(dto, new RuntimeException());

        Assertions.assertNotEquals(HttpStatus.OK, goalController.createInvitation(dto));
    }

    private GoalInvitationDto buildGoalInvitationDto() {
        return GoalInvitationDto.builder()
                .invitedUserId(1L)
                .inviterId(2L)
                .goalId(1L)
                .build();
    }
}
