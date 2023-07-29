package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

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
        mockMvc.perform(MockMvcRequestBuilders.post("/goal/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalInvitationDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Create invitation: Bad request, should return 400")
    void testCreateInvitationBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/goal/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badGoalInvitationDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


}
