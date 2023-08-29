package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GoalInvitationValidationTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    GoalInvitationValidation goalInvitationValidation;

    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .build();
    }

    @Test
    void testExistUsers_ThrowsException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, ()-> goalInvitationValidation.invitationValidationUser(goalInvitationDto));
    }

    @Test
    void existGoal_ThrowsException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(goalRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, ()-> goalInvitationValidation.invitationValidationUser(goalInvitationDto));
    }

    @Test
    void testExistUsers_DoesNotThrows() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(goalRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> goalInvitationValidation.invitationValidationUser(goalInvitationDto));
    }

    @Test
    void emptyUsers_ThrowsException() {
        Mockito.when(userRepository.existsById(null)).thenReturn(true);
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(goalRepository.existsById(1L)).thenReturn(true);
        goalInvitationDto.setInviterId(null);
        assertThrows(IllegalArgumentException.class, () -> goalInvitationValidation.invitationValidationUser(goalInvitationDto));
    }

    @Test
    void sameUsers_ThrowsException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(goalRepository.existsById(1L)).thenReturn(true);
        goalInvitationDto.setInvitedUserId(1L);
        assertThrows(IllegalArgumentException.class, () -> goalInvitationValidation.invitationValidationUser(goalInvitationDto));
    }
}