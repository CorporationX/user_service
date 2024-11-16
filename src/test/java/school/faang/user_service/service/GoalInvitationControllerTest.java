package school.faang.user_service.service;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.GoalInvitationResponseDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



@WebMvcTest(GoalInvitationController.class)
public class GoalInvitationControllerTest {
    private final long ID = 1L;

    @Mock
    private GoalInvitationService service;
    @InjectMocks
    private GoalInvitationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCreateInvitationWhenInviterAndInviteeTheSamePerson() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(ID);
        goalInvitationDto.setInvitedUserId(ID);
        when(service.createInvitation(goalInvitationDto)).thenThrow(new IllegalAccessException());
        Assert.assertThrows(IllegalAccessException.class, () -> service.createInvitation(goalInvitationDto));
    }
    @Test
    public void testCreateWhenUserNotExist() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(null);
        when(service.createInvitation(goalInvitationDto)).thenThrow(new IllegalAccessException());
        Assert.assertThrows(IllegalAccessException.class, () -> service.createInvitation(goalInvitationDto));
    }

    @Test
    public void testCreateSaveUser() throws Exception {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        when(service.createInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);
        service.createInvitation(goalInvitationDto);
        Mockito.verify(service).createInvitation(goalInvitationDto);
    }
    @Test
    public void testAcceptGoalInvitation() throws Exception {
        GoalInvitationResponseDto responseDto = new GoalInvitationResponseDto();
        when(service.acceptGoalInvitation(ID)).thenReturn(responseDto);
        Mockito.verify(service).acceptGoalInvitation(ID);
    }

    @Test
    public void testAcceptNotFoundGoalInvitation() throws Exception {
        Long id = null;
        when(service.rejectGoalInvitation(id)).thenReturn(false);
        Mockito.verify(service).acceptGoalInvitation(id);
    }

    @Test
    public void testAcceptGoalInvitationWhenMoreThanThreeActiveGoals() throws Exception {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(ID);
        List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();
        receivedGoalInvitations.add(new GoalInvitation());
        receivedGoalInvitations.add(new GoalInvitation());
        receivedGoalInvitations.add(new GoalInvitation());
        goalInvitation.getInvited().setReceivedGoalInvitations(receivedGoalInvitations);
        when(service.rejectGoalInvitation(ID)).thenReturn(false);
        Mockito.verify(service).acceptGoalInvitation(ID);
    }

    @Test
    public void testAcceptAlreadyHaveSuchGoalInvitation() throws Exception {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(ID);
        goalInvitation.setInvited(new User());
        goalInvitation.getInvited().getReceivedGoalInvitations().add(goalInvitation);
        when(service.rejectGoalInvitation(ID)).thenReturn(false);
        Mockito.verify(service).acceptGoalInvitation(ID);
    }

    @Test
    public void testAcceptInvitation() throws Exception {
        when(service.rejectGoalInvitation(ID)).thenReturn(true);
        Mockito.verify(service).acceptGoalInvitation(ID);
    }

    @Test
    public void testRejectGoalInvitationNotFound() throws Exception {
        Long id = null;
        when(service.rejectGoalInvitation(id)).thenReturn(false);
        Mockito.verify(service).rejectGoalInvitation(id);
    }

    @Test
    public void testRejectGoalInvitation() throws Exception {
        when(service.rejectGoalInvitation(ID)).thenReturn(true);
        Mockito.verify(service).rejectGoalInvitation(ID);
    }

    @Test
    public void testGetInvitations() throws Exception {
        GoalInvitationFilterDto filter = new GoalInvitationFilterDto();
        List<GoalInvitation> goalInvitations = Collections.singletonList(new GoalInvitation());
        when(service.getInvitationsByFilter(any(GoalInvitationFilterDto.class))).thenReturn(goalInvitations);
        Mockito.verify(service).getInvitationsByFilter(filter);
    }
}
