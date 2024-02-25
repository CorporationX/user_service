package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private UserContext userContext;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    void followUser_ShouldCallServiceMethod() {
        long followerId = 1;
        long followeeId = 2;

        when(userContext.getUserId()).thenReturn(followerId);
        subscriptionController.followUser(followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
        verify(subscriptionValidator, times(1)).validateUserIds(followerId, followeeId);
    }

    @Test
    void unfollowUser_ShouldCallServiceMethod() {
        long followerId = 1;
        long followeeId = 2;

        when(userContext.getUserId()).thenReturn(followerId);
        subscriptionController.unfollowUser(followeeId);

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
        verify(subscriptionValidator, times(1)).validateUserIds(followerId, followeeId);
    }

    @Test
    void getFollowers_ShouldCallServiceMethod() {
        long followeeId = 1;
        UserFilterDto filters = new UserFilterDto();

        subscriptionController.getFollowers(followeeId, filters);

        verify(subscriptionService, times(1)).getFollowers(followeeId, filters);
    }

    @Test
    void getFollowersCount_ShouldCallServiceMethod() {
        long followeeId = 1;

        subscriptionController.getFollowersCount(followeeId);

        verify(subscriptionService, times(1)).getFollowersCount(followeeId);
    }

    @Test
    void getFollowing_ShouldCallServiceMethod() {
        long followeeId = 1;
        UserFilterDto filters = new UserFilterDto();

        subscriptionController.getFollowing(followeeId, filters);

        verify(subscriptionService, times(1)).getFollowing(followeeId, filters);
    }

    @Test
    void getFollowingCount_ShouldCallServiceMethod() {
        long followerId = 1;

        subscriptionController.getFollowingCount(followerId);

        verify(subscriptionService, times(1)).getFollowingCount(followerId);
    }
}
