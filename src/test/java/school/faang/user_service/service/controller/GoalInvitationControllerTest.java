package school.faang.user_service.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.GoalInvitationService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    private GoalInvitationDto invitationDto;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalInvitationController).build();
        objectMapper = new ObjectMapper();

        invitationDto = new GoalInvitationDto(
                TestConstants.INVITATION_ID,
                TestConstants.INVITER_ID,
                TestConstants.INVITED_USER_ID,
                TestConstants.GOAL_ID,
                RequestStatus.PENDING
        );
    }

    @Test
    void createInvitation_ValidInput_ReturnsOk() throws Exception {
        mockMvc.perform(post("/api/goal-invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitationDto)))
                .andExpect(status().isOk());

        verify(goalInvitationService, times(1)).createInvitation(any(GoalInvitationDto.class));
    }

    @Test
    void acceptGoalInvitation_ValidId_ReturnsOk() throws Exception {
        Long id = TestConstants.ID;

        mockMvc.perform(put("/api/goal-invitations/{id}/accept", id))
                .andExpect(status().isOk());

        verify(goalInvitationService, times(1)).acceptGoalInvitation(id);
    }

    @Test
    void rejectGoalInvitation_ValidId_ReturnsOk() throws Exception {
        Long id = TestConstants.ID;

        mockMvc.perform(put("/api/goal-invitations/{id}/reject", id))
                .andExpect(status().isOk());

        verify(goalInvitationService, times(1)).rejectGoalInvitation(id);
    }

    @Test
    void getInvitations_ValidFilter_ReturnsList() throws Exception {
        Long inviterId = TestConstants.INVITER_ID;
        Long invitedId = TestConstants.INVITED_USER_ID;
        RequestStatus status = RequestStatus.PENDING;
        InvitationFilterDto filter = new InvitationFilterDto(inviterId, invitedId, status);
        List<GoalInvitationDto> invitations = Collections.singletonList(invitationDto);

        when(goalInvitationService.getInvitations(filter)).thenReturn(invitations);

        mockMvc.perform(get("/api/goal-invitations")
                        .param("inviterId", inviterId.toString())
                        .param("invitedId", invitedId.toString())
                        .param("status", status.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invitations)));

        verify(goalInvitationService, times(1)).getInvitations(filter);
    }

    @Test
    void getInvitationsByInvitedUserId_ValidId_ReturnsList() throws Exception {
        Long invitedUserId = TestConstants.INVITED_USER_ID;
        List<GoalInvitationDto> invitations = Collections.singletonList(invitationDto);

        when(goalInvitationService.getInvitationsByInvitedUserId(invitedUserId)).thenReturn(invitations);

        mockMvc.perform(get("/api/goal-invitations/invited/{invitedUserId}", invitedUserId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invitations)));

        verify(goalInvitationService, times(1)).getInvitationsByInvitedUserId(invitedUserId);
    }
    @Test
    void createInvitation_InvalidInput_ReturnsBadRequest() throws Exception {
        GoalInvitationDto invalidDto = new GoalInvitationDto(null,null,
                null,null,null);

        mockMvc.perform(post("/api/goal-invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(goalInvitationService, never()).createInvitation(any(GoalInvitationDto.class));
    }

    @ParameterizedTest
    @EnumSource(RequestStatus.class)
    void getInvitations_ValidStatus_ReturnsList(RequestStatus status) throws Exception {
        InvitationFilterDto filter = new InvitationFilterDto(null, null, status);
        List<GoalInvitationDto> invitations = Collections.singletonList(invitationDto);

        when(goalInvitationService.getInvitations(filter)).thenReturn(invitations);

        mockMvc.perform(get("/api/goal-invitations")
                        .param("status", status.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invitations)));

        verify(goalInvitationService, times(1)).getInvitations(filter);
    }
}