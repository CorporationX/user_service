package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;

class GoalInvitationControllerTest {
    private static final Long INVITATION_ID = 1L;
    private static final Long FIRS_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;
    private static final Long GOAL_ID = 1L;
    private static final RequestStatus STATUS = RequestStatus.ACCEPTED;
    private static final int CREATED_RESPONSE_CODE = 201;

    private final GoalInvitationDto invitationDto =
            new GoalInvitationDto(null, FIRS_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);
    private final GoalInvitationDto expectedInvitationDto =
            new GoalInvitationDto(INVITATION_ID, FIRS_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);

    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @Test
    @DisplayName("Given invitationDto when create invitation then return invitationDto")
    void testCreateInvitationSuccessful() {
        var expectedInvitation = getInvitation(INVITATION_ID, FIRS_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);
        when(goalInvitationService.createInvitation(goalInvitationMapper.toEntity(invitationDto),
                FIRS_USER_ID, SECOND_USER_ID, GOAL_ID)).thenReturn(expectedInvitation);

        ResponseEntity<GoalInvitationDto> response = goalInvitationController.createInvitation(invitationDto);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("statusCode.value", CREATED_RESPONSE_CODE)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedInvitationDto);
    }

    @Test
    @DisplayName("Given invitation id when accept invitation return response")
    void testAcceptInvitationSuccessful() {
        var response = goalInvitationController.acceptGoalInvitation(INVITATION_ID);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.OK);
    }

    @Test
    @DisplayName("Given invitation id when reject invitation return response")
    void testRejectInvitationSuccessful() {
        var response = goalInvitationController.rejectGoalInvitation(INVITATION_ID);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.OK);
    }

    @Test
    @DisplayName("Given invitationFilterDto when get invitations then return response")
    void getInvitationsSuccessful() {
        var filter = new InvitationFilterDto(null, null, null, null, null);
        when(goalInvitationService.getInvitations(filter))
                .thenReturn(List.of(mock(GoalInvitation.class)));
        var response = goalInvitationController.getInvitations(filter);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.FOUND);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}