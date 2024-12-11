package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.ProjectSubscriptionService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectSubscriptionService projectSubscriptionService;

    @InjectMocks
    private ProjectSubscriptionController projectSubscriptionController;

    private static final String BASE_URL = "/api/projects";
    private static final String SUBSCRIBE_URL = BASE_URL + "/{projectId}/subscribe";
    private static final String UNSUBSCRIBE_URL = BASE_URL + "/{projectId}/unsubscribe";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectSubscriptionController).build();
    }

    @Test
    @DisplayName("Позитивный тест: успешная подписка на проект")
    void subscribeToProject_ShouldReturnSuccessMessage() throws Exception {
        Long userId = 1L;
        Long projectId = 1L;

        mockMvc.perform(post(SUBSCRIBE_URL, projectId)
                .param("userId", String.valueOf(userId)))
            .andExpect(status().isOk())
            .andExpect(content().string("You have successfully subscribed from the project."));

        verify(projectSubscriptionService, times(1)).subscribeToProject(userId, projectId);
    }

    @Test
    @DisplayName("Позитивный тест: успешная отписка от проекта")
    void unsubscribeFromProject_ShouldReturnSuccessMessage() throws Exception {
        Long userId = 1L;
        Long projectId = 1L;

        mockMvc.perform(delete(UNSUBSCRIBE_URL, projectId)
                .param("userId", String.valueOf(userId)))
            .andExpect(status().isOk())
            .andExpect(content().string("You have successfully unsubscribed from the project."));

        verify(projectSubscriptionService, times(1)).unsubscribeFromProject(userId, projectId);
    }
}
