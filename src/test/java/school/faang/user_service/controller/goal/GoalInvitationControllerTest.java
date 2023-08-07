package school.faang.user_service.controller.goal;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.dto.filter.GoalInvitationFilterIDto;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private GoalInvitationDto goalInvitationDto;
    private GoalInvitationDto badGoalInvitationDto;

    private long invitationId;

    private GoalInvitationFilterIDto goalInvitationFilterIDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalInvitationController).build();

        objectMapper = new ObjectMapper();

        goalInvitationDto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(2L)
                .invitedUserId(3L)
                .build();

        badGoalInvitationDto = GoalInvitationDto.builder().build(); // no id, which is mandatory

        invitationId = 1L;

        goalInvitationFilterIDto = GoalInvitationFilterIDto.builder()
                .inviterId(1L)
                .invitedId(2L)
                .inviterNamePattern("Vlad Mishustin")
                .invitedNamePattern("Oleg Kozhanov")
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Create invitation: Valid request, should return 201")
    void testCreateInvitationIsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/goal/invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalInvitationDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Create invitation: Bad request, should return 400")
    void testCreateInvitationBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/goal/invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badGoalInvitationDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Accept invitation: Valid request, should return 202")
    void testAcceptInvitationAccepted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/goal/invitation/" + invitationId + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    @DisplayName("Accept invitation: Bad request, should return 400")
    void testAcceptInvitationBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/goal/invitation/" + goalInvitationDto + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Reject invitation: Valid request, should return 202")
    void testRejectInvitationRejected() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/goal/invitation/" + invitationId + "/reject")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    @DisplayName("Reject invitation: Bad request, should return 400")
    void testRejectInvitationBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/goal/invitation/" + goalInvitationDto + "/reject")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Get invitations: Valid request, should return 200")
    void testGetInvitationsAccepted() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/goal/invitations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(goalInvitationFilterIDto)))
                    .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get invitations: Bad request, should return 400")
    void testGetInvitationsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/goal/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Bugaga"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
