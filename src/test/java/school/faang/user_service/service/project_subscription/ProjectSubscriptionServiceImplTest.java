package school.faang.user_service.service.project_subscription;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.dto.event.FollowerEvent;
import school.faang.user_service.dto.event.ProjectFollowerEvent;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.validator.ProjectSubscriptionValidator;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectSubscriptionServiceImplTest {
    private static final long FOLLOWER_ID = 1L;
    private static final long PROJECT_ID = 2L;
    private static final long OWNER_ID = 2L;

    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;
    @Mock
    private FollowerEventPublisher followerEventPublisher;
    @Mock
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;
    @Mock
    private ProjectSubscriptionValidator projectSubscriptionValidator;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private ProjectSubscriptionServiceImpl projectSubscriptionService;
    private FollowerEvent followerEvent;
    private ProjectFollowerEvent projectFollowerEvent;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        followerEvent = FollowerEvent.builder()
                .followerId(FOLLOWER_ID)
                .projectId(PROJECT_ID)
                .followingDate(LocalDateTime.now().withNano(0))
                .build();
        projectFollowerEvent = ProjectFollowerEvent.builder()
                .followerId(FOLLOWER_ID)
                .projectId(PROJECT_ID)
                .ownerId(OWNER_ID)
                .build();
        projectDto = ProjectDto.builder()
                .id(PROJECT_ID)
                .ownerId(OWNER_ID)
                .build();
    }

    @Test
    public void whenFollowProjectSuccessfully() {
        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(FOLLOWER_ID, PROJECT_ID)).thenReturn(false);
        when(projectServiceClient.getProjectById(PROJECT_ID)).thenReturn(projectDto);
        projectSubscriptionService.followProject(FOLLOWER_ID, PROJECT_ID);
        verify(projectSubscriptionRepository).followProject(FOLLOWER_ID, PROJECT_ID);
        verify(followerEventPublisher).publish(followerEvent);
        verify(projectFollowerEventPublisher).publish(projectFollowerEvent);


    }

    @Test
    public void whenFollowProjectAndSubscriptionExistsThenThrowsException() {
        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(FOLLOWER_ID, PROJECT_ID)).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> projectSubscriptionService.followProject(FOLLOWER_ID, PROJECT_ID));
    }
}