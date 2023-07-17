package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.util.goal.exception.GoalNotFoundException;
import school.faang.user_service.util.goal.exception.UserNotFoundException;
import school.faang.user_service.util.goal.validator.GoalInvitationEntityValidator;

import java.util.Optional;

public class GoalInvitationServiceTest {

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GoalRepository goalRepository;

    @Mock
    GoalInvitationMapper goalInvitationMapper;

    @Mock
    GoalInvitationEntityValidator goalInvitationDtoValidator;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInvitation_InputsAreCorrect_ShouldSaveGoalInvitation() {
        Mockito.when(goalInvitationMapper.toEntity(buildGoalInvitationDto(), goalInvitationService))
                .thenReturn(buildGoalInvitationEntity());
        Mockito.doNothing().when(goalInvitationDtoValidator).validate(buildGoalInvitationEntity());
        Mockito.when(goalInvitationMapper.toDto(buildGoalInvitationEntity())).thenReturn(buildGoalInvitationDto());

        goalInvitationService.createInvitation(GoalInvitationDto.builder().build());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(goalInvitationMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void testFindUserById_InputsAreIncorrect_ShouldThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> goalInvitationService.findUserById(Mockito.anyLong()));
    }

    @Test
    void testFindUserById_InputsAreCorrect_ShouldReturnUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        goalInvitationService.findUserById(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void testFindGoalById_InputsAreIncorrect_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(GoalNotFoundException.class,
                () -> goalInvitationService.findGoalById(Mockito.anyLong()));
    }

    @Test
    void testFindGoalById_InputsAreCorrect_ShouldReturnGoal() {
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));

        goalInvitationService.findGoalById(1L);

        Mockito.verify(goalRepository, Mockito.times(1)).findById(1L);
    }

    private GoalInvitation buildGoalInvitationEntity() {
        return GoalInvitation.builder()
                .id(1L)
                .invited(User.builder().
                        id(1L)
                        .build())
                .inviter(User.builder()
                        .id(1L)
                        .build())
                .goal(Goal.builder()
                        .id(1L)
                        .build())
                .status(RequestStatus.PENDING)
                .build();
    }

    private GoalInvitationDto buildGoalInvitationDto() {
        return GoalInvitationDto.builder()
                .id(1L)
                .invitedUserId(1L)
                .inviterId(1L)
                .goalId(1L)
                .status(RequestStatus.PENDING)
                .build();
    }
}
