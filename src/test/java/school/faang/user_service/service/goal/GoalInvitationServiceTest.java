package school.faang.user_service.service.goal;

import org.mockito.Spy;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.validation.goal.GoalInvitationValidator;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Spy
    GoalInvitationMapperImpl goalInvitationMapper;

    @Mock
    GoalInvitationValidator goalInvitationValidator;
    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    private GoalInvitation goalInvitation;
    private GoalInvitation badRequest;

    private GoalInvitationDto goalInvitationDto;
    private GoalInvitationDto badRequestDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(2L)
                .invitedUserId(3L)
                .build();

        badRequestDto = GoalInvitationDto.builder().inviterId(1L).invitedUserId(1L).build();
        badRequest = goalInvitationMapper.toEntity(badRequestDto);
    }

    @Test
    @DisplayName("Create invitation: Positive scenario")
    void testCreateInvitationIsOk() {
        Assertions.assertDoesNotThrow(() -> goalInvitationService.createInvitation(goalInvitationDto));
    }

    @Test
    @DisplayName("Create invitation: Inviter and invited user are the same person")
    void shouldThrowDataValidationException() {
        doThrow(DataValidationException.class).when(goalInvitationValidator).validate(any());
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationService.createInvitation(badRequestDto));
    }
}
