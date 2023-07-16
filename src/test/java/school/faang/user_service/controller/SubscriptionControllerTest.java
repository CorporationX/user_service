package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @InjectMocks
    SubscriptionController subscriptionController;
    @Mock
    SubscriptionService subscriptionService;
    @Mock
    UserFilterDto userFilterDto;

    long followerId;
    long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 2L;
        followeeId = 2L;
    }

    @Test
    public void testAssertThrowsDataValidationExceptionForMethodFollowUser() {
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUser() {
        followerId = 1L;
        followeeId = 2L;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testMessageThrowForMethodFollowUser() {
        try {
            subscriptionController.followUser(followerId, followerId);
        } catch (DataValidationException e) {
            assertEquals("Follower and folowee can not be the same", e.getMessage());
        }
        verifyNoInteractions(subscriptionService);
    }

    @Test
    public void testUnfollowUser() {
        followerId = 1L;
        followeeId = 2L;

        subscriptionController.unfollowUser(followerId, followeeId);

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testMessageThrowForMethodUnfollowUser() {
        assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(followerId, followeeId));

        try {
            subscriptionController.unfollowUser(followerId, followerId);
        } catch (DataValidationException e) {
            assertEquals("Follower and folowee can not be the same", e.getMessage());
        }
        verifyNoInteractions(subscriptionService);
    }

    @Test
    public void testGetFollowers() {
        subscriptionController.getFollowers(followeeId, userFilterDto);

        verify(subscriptionService, times(1)).getFollowers(followeeId, userFilterDto);
    }
}