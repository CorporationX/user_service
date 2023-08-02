package school.faang.user_service.controller.goal;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import school.faang.user_service.entity.goal.Goal;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.goal.GoalInvitationService;
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
        long invitationId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.put("/goal/invitation/" + invitationId + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    @DisplayName("Accept invitation: Bad request, should return 400")
    void testAcceptInvitationBadRequest() throws Exception {
        Goal goal = new Goal();
        mockMvc.perform(MockMvcRequestBuilders.put("/goal/invitation/" + goal + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
