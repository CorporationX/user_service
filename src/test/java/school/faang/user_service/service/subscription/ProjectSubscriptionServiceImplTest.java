package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.ProjectDto;
import school.faang.user_service.dto.FollowerProjectEvent;
import school.faang.user_service.publisher.FollowerProjectPublisher;
import school.faang.user_service.publisher.UnfollowProjectPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.ProjectSubscriptionServiceImpl;
import school.faang.user_service.service.ProjectSubscriptionValidator;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
class ProjectSubscriptionServiceImplTest {

    @Mock
    private ProjectSubscriptionRepository subscriptionRepository;

    @Mock
    private ProjectSubscriptionValidator validator;

    @Mock
    private FollowerProjectPublisher followerProjectPublisher;

    @Mock
    private UnfollowProjectPublisher unfollowProjectPublisher;

    @InjectMocks
    private ProjectSubscriptionServiceImpl projectSubscriptionService;

    private ProjectDto projectDto;
    private long followerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        followerId = 1L;
        projectDto = new ProjectDto(1L, 1L);
    }

    @Test
    @DisplayName("Пользователь успешно подписался на проект")
    void shouldSuccessfullyFollowProject() {
        when(validator.isAlreadySubscribed(followerId, projectDto.projectId())).thenReturn(false);

        projectSubscriptionService.followProject(followerId, projectDto);

        verify(subscriptionRepository).followProject(followerId, projectDto.projectId());
        verify(followerProjectPublisher).publishFollowerEvent(any(FollowerProjectEvent.class));
        verifyNoMoreInteractions(subscriptionRepository, followerProjectPublisher);
    }

    @Test
    @DisplayName("Ошибка: пользователь уже подписан на проект")
    void shouldThrowExceptionWhenUserAlreadySubscribed() {
        when(validator.isAlreadySubscribed(followerId, projectDto.projectId())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> projectSubscriptionService.followProject(followerId, projectDto));
        assertEquals("Пользователь уже подписан на этот проект.", exception.getMessage());
    }

    @Test
    @DisplayName("Пользователь успешно отписался от проекта")
    void shouldSuccessfullyUnfollowProject() {
        when(validator.isAlreadySubscribed(followerId, projectDto.projectId())).thenReturn(true);

        projectSubscriptionService.unfollowProject(followerId, projectDto);

        verify(subscriptionRepository).unfollowProject(followerId, projectDto.projectId());
        verify(unfollowProjectPublisher).publishUnfollowEvent(any(FollowerProjectEvent.class));
        verifyNoMoreInteractions(subscriptionRepository, unfollowProjectPublisher);
    }

    @Test
    @DisplayName("Ошибка: пользователь не подписан на проект")
    void shouldThrowExceptionWhenUserNotSubscribed() {
        when(validator.isAlreadySubscribed(followerId, projectDto.projectId())).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> projectSubscriptionService.unfollowProject(followerId, projectDto));
        assertEquals("Пользователь не подписан на этот проект.", exception.getMessage());
    }

    @Test
    @DisplayName("Пользователь пытается подписаться на проект, когда уже подписан")
    void shouldThrowExceptionWhenUserAlreadySubscribedForFollow() {
        when(validator.isAlreadySubscribed(followerId, projectDto.projectId())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> projectSubscriptionService.followProject(followerId, projectDto));
        assertEquals("Пользователь уже подписан на этот проект.", exception.getMessage());
    }

    @Test
    @DisplayName("Пользователь пытается отписаться от проекта, но не подписан")
    void shouldThrowExceptionWhenUserNotSubscribedForUnfollow() {
        when(validator.isAlreadySubscribed(followerId, projectDto.projectId())).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> projectSubscriptionService.unfollowProject(followerId, projectDto));
        assertEquals("Пользователь не подписан на этот проект.", exception.getMessage());
    }
}
