package school.faang.user_service.controller.goal;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import static school.faang.user_service.entity.RequestStatus.PENDING;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService service;
    @InjectMocks
    GoalInvitationController controller;

    @Test
    void testCreateInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(1L, 1L, 1L, 1L, PENDING);
        controller.createInvitation(goalInvitationDto);
        Mockito.verify(service, Mockito.times(1))
                .createInvitation1(goalInvitationDto);

    }
}