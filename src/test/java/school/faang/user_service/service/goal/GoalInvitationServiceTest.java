package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;
import school.faang.user_service.service.goal.filter.InvitedIdFilter;
import school.faang.user_service.service.goal.filter.InvitedNamePatternFilter;
import school.faang.user_service.service.goal.filter.InviterIdFilter;
import school.faang.user_service.service.goal.filter.InviterNamePatternFilter;
import school.faang.user_service.service.goal.filter.RequestStatusFilter;
import school.faang.user_service.service.goal.filter.TestData;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.message.MessageForGoalInvitationService.NO_GOAL_INVITATION_IN_DB;
import static school.faang.user_service.exception.message.MessageForGoalInvitationService.NO_GOAL_IN_DB;
import static school.faang.user_service.exception.message.MessageForGoalInvitationService.NO_INVITED_IN_DB;
import static school.faang.user_service.exception.message.MessageForGoalInvitationService.NO_INVITER_IN_DB;


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
        InvitedNamePatternFilter invitedNamePatternFilter = Mockito.spy(InvitedNamePatternFilter.class);
        InvitedIdFilter invitedIdFilter = Mockito.spy(InvitedIdFilter.class);
        InviterNamePatternFilter inviterNamePatternFilter = Mockito.spy(InviterNamePatternFilter.class);
        RequestStatusFilter requestStatusFilter = Mockito.spy(RequestStatusFilter.class);
        List<InvitationFilter> filters = List.of(inviterIdFilter, invitedIdFilter, inviterNamePatternFilter, invitedNamePatternFilter, requestStatusFilter);
        goalInvitationService.setInvitationFilters(filters);
    }

    @Test
    void testForCreateInvitationWhenThereIsNoInviterInDB() {
        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(NO_INVITER_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForCreateInvitationWhenThereIsNoInvitedInDB() {
        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(NO_INVITED_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForCreateInvitationWhenThereIsNoGoalInDB() {
        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(new User()));
        when(goalRepository.findById(goalInvitationDto.getGoalId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
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
    void testForAcceptGoalInvitationIfThereIsNoGoalInvitationInDB() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        assertEquals(NO_GOAL_INVITATION_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForAcceptGoalInvitationIfThereIsNoGoalInDB() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitationSetStatusAddGoal() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.remove(0);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitationAfterSave = captor.getValue();

        assertEquals(goalInvitation.getStatus(), goalInvitationAfterSave.getStatus());
        assertEquals(goalInvitation.getInvited().getSetGoals().get(0), goalInvitationAfterSave.getGoal());
    }

    @Test
    void testForRejectGoalInvitationWithNoGoalInvitation() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_INVITATION_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForRejectGoalInvitationWithNoGoal() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
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
    void testGetInvitationsWithFilters() {
        InvitationFilterDto invitationFilterDto = testData.prepareInvitationFilterDto();
        List<GoalInvitation> goalInvitations = testData.prepareGoalInvitationList();

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        assertEquals(goalInvitations.size(), 3);
        assertEquals(goalInvitationService.getInvitations(invitationFilterDto).size(), 2);
    }
}
