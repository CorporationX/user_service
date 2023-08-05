package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalInvitationValidator;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    private static GoalInvitationDto invitationDto = new GoalInvitationDto(100L, 1L, 2L, 12L, RequestStatus.PENDING);

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @Mock
    private GoalInvitationValidator goalInvitationValidator = new GoalInvitationValidator(userRepository, goalRepository, goalInvitationRepository, goalInvitationMapper);

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    public void whenGoalInvitationIsCorrect_createGoalInvitation() {
        goalInvitationService.createInvitation(invitationDto);

        Mockito.verify(goalInvitationRepository).save(goalInvitationMapper.toEntity(invitationDto));
    }
}
