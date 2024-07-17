package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    @DisplayName("Test follow user")
    public void testFollowUserUsersFollow() {
        subscriptionController.followUser(1L, 2L);

        verify(subscriptionService, Mockito.times(1))
                .followUser(1L, 2L);
    }

    @Test
    @DisplayName("Test unfollow user")
    public void testUnfollowUserUsersUnfollow() {
        subscriptionController.followUser(1L, 2L);

        verify(subscriptionService, Mockito.times(1))
                .followUser(1L, 2L);
    }

    @Test
    @DisplayName("Test get followers returns UserDto list")
    public void testGetFollowersReturnedUsersDto() {
        UserFilterDto userFilterDto = new UserFilterDto();
        subscriptionController.getFollowers(1L, userFilterDto);

        verify(subscriptionService, Mockito.times(1))
                .getFollowers(1L, userFilterDto);
    }

    @Test
    @DisplayName("Test get followers count returns correct count")
    public void testGetFollowersCountReturnedCount() {
        subscriptionController.getFollowersCount(1L);

        verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(1L);
    }

    @Test
    @DisplayName("Test get following returns UserDto list")
    public void testGetFollowingReturnedUsersDto() {
        UserFilterDto userFilterDto = new UserFilterDto();
        subscriptionController.getFollowing(1L, userFilterDto);

        verify(subscriptionService, Mockito.times(1))
                .getFollowing(1L, userFilterDto);
    }

    @Test
    @DisplayName("Test get following count returns correct count")
    public void testGetFollowingCountReturnedCount() {
        subscriptionController.getFollowingCount(1L);

        verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(1L);
    }
}
