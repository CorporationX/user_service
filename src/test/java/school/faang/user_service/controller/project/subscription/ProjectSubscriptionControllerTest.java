package school.faang.user_service.controller.project.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.model.dto.ProjectDto;
import school.faang.user_service.service.impl.project.subscription.ProjectSubscriptionServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionControllerTest {

    @Mock
    private ProjectSubscriptionServiceImpl subscriptionService;

    @InjectMocks
    private ProjectSubscriptionController subscriptionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFollowProjectOk() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .projectId(1L)
                .ownerId(2L)
                .build();

        String projectDtoJson = objectMapper.writeValueAsString(projectDto);
        mockMvc.perform(post("/api/v1/users/{userId}/projects", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectDtoJson))
                .andExpect(status().isOk());
    }
}
