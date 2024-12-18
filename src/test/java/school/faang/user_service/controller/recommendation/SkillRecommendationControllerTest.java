package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.UserSkillGuaranteeService;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SkillRecommendationControllerTest {
    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @InjectMocks
    private SkillRecommendationController eventController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    public void testPublishSkillAcquiredEvent() throws Exception {
        long userId = 1L;
        long skillId = 2L;

        doNothing().when(userSkillGuaranteeService).publishSkillAcquiredEvent(userId, skillId);

        String json = "{\"authorId\": 1, \"receiverId\": 2, \"skillId\": 1}";
        String url = "/recommendations/users/{userId}/skills/{skillId}";

        mockMvc.perform(post(url, userId, skillId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
