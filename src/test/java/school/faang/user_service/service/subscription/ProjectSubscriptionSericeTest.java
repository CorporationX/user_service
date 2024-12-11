package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.ProjectFollowerEventDto;
import school.faang.user_service.entity.Project;
import school.faang.user_service.entity.ProjectSubscription;
import school.faang.user_service.entity.User;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.publisher.ProjectUnfollowEventPublisher;
import school.faang.user_service.repository.ProjectRepository;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.ProjectSubscriptionService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProjectSubscriptionServiceTest {

    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;

    @Mock
    private ProjectUnfollowEventPublisher projectUnfollowEventPublisher;

    private ProjectSubscriptionService projectSubscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectSubscriptionService = new ProjectSubscriptionService(
            projectSubscriptionRepository,
            userRepository,
            projectRepository,
            projectFollowerEventPublisher,
            projectUnfollowEventPublisher
        );
    }

    @Test
    @DisplayName("Подписка на проект: успешный сценарий, если пользователь не подписан")
    void subscribeToProject_ShouldCreateSubscription_WhenUserNotSubscribed() {
        Long userId = 1L;
        Long projectId = 1L;
        User user = new User();
        user.setId(userId);
        Project project = new Project();
        project.setId(projectId);
        project.setCreatorId(2L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(userId, projectId)).thenReturn(false);

        projectSubscriptionService.subscribeToProject(userId, projectId);

        verify(projectSubscriptionRepository, times(1)).save(any(ProjectSubscription.class));
        verify(projectFollowerEventPublisher, times(1)).publish(any(ProjectFollowerEventDto.class));
    }

    @Test
    @DisplayName("Подписка на проект: исключение, если пользователь уже подписан")
    void subscribeToProject_ShouldThrowException_WhenUserAlreadySubscribed() {
        Long userId = 1L;
        Long projectId = 1L;

        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(userId, projectId)).thenReturn(true);

        try {
            projectSubscriptionService.subscribeToProject(userId, projectId);
        } catch (IllegalStateException e) {
            assert e.getMessage().equals("Пользователь уже подписан на этот проект");
        }

        verify(projectSubscriptionRepository, never()).save(any(ProjectSubscription.class));
        verify(projectFollowerEventPublisher, never()).publish(any(ProjectFollowerEventDto.class));
    }

    @Test
    @DisplayName("Отписка от проекта: успешный сценарий, если пользователь подписан")
    void unsubscribeFromProject_ShouldDeleteSubscription_WhenSubscribed() {
        Long userId = 1L;
        Long projectId = 1L;
        User user = new User();
        user.setId(userId);
        Project project = new Project();
        project.setId(projectId);
        project.setCreatorId(2L);

        ProjectSubscription subscription = new ProjectSubscription();
        subscription.setFollower(user);
        subscription.setProjectId(projectId);

        when(projectSubscriptionRepository.findByFollowerIdAndProjectId(userId, projectId)).thenReturn(Optional.of(subscription));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectSubscriptionService.unsubscribeFromProject(userId, projectId);

        verify(projectSubscriptionRepository, times(1)).delete(any(ProjectSubscription.class));
        verify(projectUnfollowEventPublisher, times(1)).publishUnfollow(any(ProjectFollowerEventDto.class));
    }


    @Test
    @DisplayName("Подписка на проект: исключение, если пользователь не найден")
    void subscribeToProject_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;
        Long projectId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try {
            projectSubscriptionService.subscribeToProject(userId, projectId);
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Пользователь с ID " + userId + " не найден");
        }

        verify(projectSubscriptionRepository, never()).save(any(ProjectSubscription.class));
        verify(projectFollowerEventPublisher, never()).publish(any(ProjectFollowerEventDto.class));
    }

    @Test
    @DisplayName("Отписка от проекта: исключение, если пользователь не найден")
    void unsubscribeFromProject_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;
        Long projectId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ProjectSubscription mockSubscription = ProjectSubscription.builder()
            .follower(null)  // Пустой пользователь
            .projectId(projectId)
            .build();

        when(projectSubscriptionRepository.findByFollowerIdAndProjectId(userId, projectId))
            .thenReturn(Optional.of(mockSubscription));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
            () -> projectSubscriptionService.unsubscribeFromProject(userId, projectId));

        assertEquals("Пользователь с ID " + userId + " не найден", thrown.getMessage());

        verify(projectSubscriptionRepository, never()).delete(any(ProjectSubscription.class));
        verify(projectUnfollowEventPublisher, never()).publishUnfollow(any(ProjectFollowerEventDto.class));
    }
}
