package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private long followerId;
    private long followeeId;
    private UserFilterDto dto;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        dto = new UserFilterDto();
        followerId = 2L;
        followeeId = 1L;
    }


    @Test
    void testFollowUserSuccessfullyFollowed() {
        subscriptionController.follow(followerId, followeeId);
        assertNotEquals(followerId, followeeId);
        verify(subscriptionService, atLeastOnce()).follow(followerId, followeeId);
    }


    @Test
    void testUnfollowUserSuccessfullyUnfollowed() {
        subscriptionController.unfollow(followerId, followeeId);
        assertNotEquals(followerId, followeeId);
        verify(subscriptionService, atLeastOnce()).unfollow(followerId, followeeId);
    }


    @Test
    void testGetFollowersSuccessfully() {
        subscriptionController.getFollowers(followerId, dto);
        verify(subscriptionService, atLeastOnce()).getFollowers(followerId, dto);
    }

    @Test
    void testGetFollowingSuccessfully() {
        subscriptionController.getFollowing(followeeId, dto);
        verify(subscriptionService, atLeastOnce()).getFollowing(followeeId, dto);
    }

    @Test
    void testGetFollowersCountSuccessfully() {
        subscriptionController.getFollowersCount(followerId);
        verify(subscriptionService, atLeastOnce()).getFollowersCount(followerId);
    }

    @Test
    void testGetFollowingCountSuccessfully() {
        subscriptionController.getFollowingCount(followerId);
        verify(subscriptionService, atLeastOnce()).getFollowingCount(followerId);
    }
}