package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalInvitationValidation;

import static school.faang.user_service.entity.RequestStatus.PENDING;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;

    @Mock
    GoalInvitationValidation goalInvitationValidation;

    @Mock
    GoalInvitationMapperImpl goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testMethodСallGoalInvitationValidation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(2L, 1L, 2L, 2L, PENDING);
        goalInvitationService.createInvitation1(goalInvitationDto);
        Mockito.verify(goalInvitationValidation, Mockito.times(1)).invitationValidationUser(goalInvitationDto);
    }

    @Test
    void testMethodСallMapperToEntity() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(1L, 1L, 2L, 2L, PENDING);
        goalInvitationService.createInvitation1(goalInvitationDto);
        Mockito.verify(goalInvitationMapper, Mockito.times(1)).toEntity(goalInvitationDto);
    }





}