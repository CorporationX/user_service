package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalInvitationMapper mapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .id(null).inviterId(1L).invitedUserId(2L).goalId(3L).status(null).build();
    }

    @Test
    void testCreateInvitation_positive() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        Mockito.when(goalRepository.findById(3L)).thenReturn(Optional.of(new Goal()));
        Mockito.when(mapper.toEntity(Mockito.any())).thenReturn(new GoalInvitation());

        goalInvitationService.createInvitation(goalInvitationDto);

        Mockito.verify(goalInvitationRepository).save(Mockito.any(GoalInvitation.class));
    }

    @Test
    void testCreateInvitation_inviterIdAndInvitedUserIdEquals() {
        goalInvitationDto.setInvitedUserId(1L);

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("User invited and user inviter cannot be equals, id " + 1L, exception.getMessage());
    }

    @Test
    void testCreateInvitation_inviterIdNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("User inviter with id " + 1L + " user does not exist", exception.getMessage());
    }

    @Test
    void testCreateInvitation_invitedUserIdNotExist() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("User invited with id " + 2L + " user does not exist", exception.getMessage());
    }

    @Test
    void testCreateInvitation_goalIdNotExist() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        Mockito.when(goalRepository.findById(3L)).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("Goal with id " + 3L + " does not exist", exception.getMessage());
    }
}