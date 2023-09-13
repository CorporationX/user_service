package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionServiceTest {
    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private ProjectSubscriptionService projectSubscriptionService;
    private long followerId;
    private long projectId;
    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        followerId = 1L;
        projectId = 11L;
        projectDto = ProjectDto.builder().ownerId(1L).build();
    }

    @Test
    public void followProject_testValidateExistsByFollowerIdAndProjectId() {
        Mockito.when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);
        Mockito.when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(followerId, projectId)).thenReturn(true);
        DataValidException dataValidException = assertThrows(DataValidException.class, () -> projectSubscriptionService.followProject(followerId, projectId));
        assertEquals("Subscription already exists", dataValidException.getMessage());
    }

    @Test
    public void followProject_testValidateFollowYourProject() {
        Mockito.when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);
        DataValidException dataValidException = assertThrows(DataValidException.class, () -> projectSubscriptionService.followProject(followerId, projectId));
        assertEquals("The user cannot subscribe or unsubscribe from his project", dataValidException.getMessage());
    }
}
