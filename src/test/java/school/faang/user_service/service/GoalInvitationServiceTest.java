package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filter.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Spy
    private GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalInvitationServiceValidator goalInvitationServiceValidator;
    @Captor
    private ArgumentCaptor<GoalInvitation> captor;
    private final TestData testData = new TestData();

    @BeforeEach
    void init() {

        InviterIdFilter inviterIdFilter = Mockito.spy(InviterIdFilter.class);
        InvitedIdFilter invitedIdFilter = Mockito.spy(InvitedIdFilter.class);
        InvitedNamePatternFilter invitedNamePatternFilter = Mockito.spy(InvitedNamePatternFilter.class);
        InviterNamePatternFilter inviterNamePatternFilter = Mockito.spy(InviterNamePatternFilter.class);
        RequestStatusFilter requestStatusFilter = Mockito.spy(RequestStatusFilter.class);

        List<InvitationFilter> invitationFilters = List.of(inviterIdFilter, invitedIdFilter, invitedNamePatternFilter, inviterNamePatternFilter, requestStatusFilter);

        goalInvitationService.setInvitationFilters(invitationFilters);
    }

    @Test
    void testCreateInvitationSaveGoalInvitation() {
        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();

        User inviter = new User();
        inviter.setId(goalInvitationDto.getInviterId());

        User invited = new User();
        invited.setId(goalInvitationDto.getInvitedUserId());

        Goal goal = new Goal();
        goal.setId(goalInvitationDto.getGoalId());

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(invited));
        when(goalRepository.findById(goalInvitationDto.getGoalId())).thenReturn(Optional.of(goal));

        goalInvitationService.createInvitation(goalInvitationDto);

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitation = captor.getValue();

        assertEquals(goalInvitationDto.getInviterId(), goalInvitation.getInviter().getId());
        assertEquals(goalInvitationDto.getInvitedUserId(), goalInvitation.getInvited().getId());
    }

    @Test
    void testAcceptGoalInvitationSetStatusAddGoal() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.remove(1);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitationAfterSave = captor.getValue();

        assertEquals(goalInvitation.getStatus(), goalInvitationAfterSave.getStatus());
        assertEquals(goalInvitation.getInvited().getGoals().get(0), goalInvitationAfterSave.getGoal());
    }

    @Test
    void testRejectGoalInvitationSetStatus() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitationService.rejectGoalInvitation(goalInvitation.getId());

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitationAfterSave = captor.getValue();

        assertEquals(goalInvitation.getStatus(), goalInvitationAfterSave.getStatus());
    }

    @Test
    void testGetInvitationsWithNoGoalInvitations() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInvitedId(22L);
        filterDto.setInviterId(23L);
        filterDto.setInviterNamePattern("Jessica");
        filterDto.setInvitedNamePattern("White");
        filterDto.setStatus(RequestStatus.PENDING);

        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();
        List<GoalInvitation> goalInvitations = List.of(goalInvitation);

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        List<GoalInvitationDto> goalInvitationDtos = goalInvitationService.getInvitations(filterDto);
        assertEquals(0, goalInvitationDtos.size());
    }
}
