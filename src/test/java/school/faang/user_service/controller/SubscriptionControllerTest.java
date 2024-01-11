package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    void followUser_ShouldCallServiceMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUser_ShouldCallServiceMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.unfollowUser(followerId, followeeId);

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
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

}
