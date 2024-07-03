package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUserIdenticalIDs() {
        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionController.followUser(1L, 1L);
        });
    }

    @Test
    public void testFollowUserUsersFollow() {
        subscriptionController.followUser(1L, 2L);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUserIdenticalIDs() {
        Assert.assertThrows(DataValidationException.class, () -> {
            subscriptionController.followUser(1L, 1L);
        });
    }

    @Test
    public void testUnfollowUserUsersUnfollow() {
        subscriptionController.followUser(1L, 2L);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(1L, 2L);
    }

    @Test
    public void testGetFollowersReturnedUsersDto() {
        UserFilterDto userFilterDto = new UserFilterDto();
        subscriptionController.getFollowers(1L, userFilterDto);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(1L, userFilterDto);
    }

    @Test
    public void testGetFollowersCountReturnedCount() {
        subscriptionController.getFollowersCount(1L);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(1L);
    }

    @Test
    public void testGetFollowingReturnedUsersDto() {
        UserFilterDto userFilterDto = new UserFilterDto();
        subscriptionController.getFollowing(1L, userFilterDto);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(1L, userFilterDto);
    }

    @Test
    public void testGetFollowingCountReturnedCount() {
        subscriptionController.getFollowingCount(1L);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(1L);
    }
}
