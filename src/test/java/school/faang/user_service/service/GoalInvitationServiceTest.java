package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    private static GoalInvitationDto invitationDto = new GoalInvitationDto(100L, 1L, 2L, 12L, RequestStatus.PENDING);

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    public void whenGoalInvitationIsCorrect_createGoalInvitation() {
        when(userRepository.isUserPresent(invitationDto.getInvitedUserId())).thenReturn(true);
        when(userRepository.isUserPresent(invitationDto.getInviterId())).thenReturn(true);

        goalInvitationService.createInvitation(invitationDto);

        Mockito.verify(goalInvitationRepository).create(invitationDto.getGoalId(), invitationDto.getInviterId(), invitationDto.getInvitedUserId());
    }

    @Test
    public void whenInviterDoesNotExist_throwException() {
        when(userRepository.isUserPresent(invitationDto.getInviterId())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationService.createInvitation(invitationDto);
        });
    }

    @Test
    public void whenInvitedUserDoesNotExist_throwException() {
        when(userRepository.isUserPresent(invitationDto.getInvitedUserId())).thenReturn(false);
        when(userRepository.isUserPresent(invitationDto.getInviterId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationService.createInvitation(invitationDto);
        });
    }
}
