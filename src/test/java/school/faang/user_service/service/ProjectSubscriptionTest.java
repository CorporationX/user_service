package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.service.project.subscription.ProjectSubscriptionService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionTest {

    @Mock
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;

    @InjectMocks
    private ProjectSubscriptionService projectSubscriptionService;

    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        projectDto = new ProjectDto();
        projectDto.setProjectId(1L);
        projectDto.setOwnerId(2L);
    }

    @Test
    void testFollowProject() {
        Long followerId = 3L;

        projectSubscriptionService.followProject(followerId, projectDto);

        verify(projectFollowerEventPublisher).sendEvent(Mockito.argThat(event ->
                event.getProjectId()==1L &&
                        event.getFollowerId()==followerId &&
                        event.getAuthorId()==2L
        ));
    }
}
