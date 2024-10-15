package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.model.dto.ProjectSubscriptionDto;
import school.faang.user_service.service.impl.ProjectSubscriptionServiceImpl;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ProjectSubscriptionControllerTest {

    @Mock
    private ProjectSubscriptionServiceImpl projectSubscriptionService;

    @InjectMocks
    private ProjectSubscriptionController projectSubscriptionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(projectSubscriptionController).build();
    }

    @Test
    void testSubscribeToProject_ReturnsSubscriptionDto() throws Exception {
        long userId = 1L;
        long projectId = 1L;
        ProjectSubscriptionDto dto = new ProjectSubscriptionDto();
        dto.setId(1L);
        dto.setFollowerId(userId);
        dto.setProjectId(projectId);

        when(projectSubscriptionService.subscribeUserToProject(anyLong(), anyLong())).thenReturn(dto);

        mockMvc.perform(post("/project-subscriptions/subscribe")
                .param("userId", String.valueOf(userId))
                .param("projectId", String.valueOf(projectId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.followerId").value(dto.getFollowerId()))
                .andExpect(jsonPath("$.projectId").value(dto.getProjectId()));
    }
}




