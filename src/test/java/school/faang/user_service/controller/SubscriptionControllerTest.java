package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;
    @InjectMocks
    SubscriptionController subscriptionController;
    @Mock
    UserFilterDto userFilterDto;

    @Test
    void testFollowUser(){
        subscriptionController.followUser(22, 23);
        verify(subscriptionService, times(1)).followUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testUnfollowUser(){
        subscriptionController.unfollowUser(22, 23);
        verify(subscriptionService, times(1)).unfollowUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testControllerUnFollowUserByNull(){
        assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(-33, 1));
    }

    @Test
    void testControllerFollowUserByNull(){
        assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(-33, 1));
    }

    @Test
    void testMessageThrowForMethodFollowUser() {
        try {
            subscriptionController.followUser(22, 22);
        } catch (DataValidationException e) {
            assertEquals("Follower and followee can not be the same", e.getMessage());
        }
        verifyNoInteractions(subscriptionService);
    }

    @Test
    void testMessageThrowForMethodUnfollowUser() {
        assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(11, 11));

        try {
            subscriptionController.unfollowUser(11, 11);
        } catch (DataValidationException e) {
            assertEquals("Follower and followee can not be the same", e.getMessage());
        }
        verifyNoInteractions(subscriptionService);
    }

    @Test
    void testGetFollowers() {
        subscriptionController.getFollowers(1111, userFilterDto);

        verify(subscriptionService, times(1)).getFollowers(1111, userFilterDto);
    }

    @Test
    void testGetFollowing() {
        subscriptionController.getFollowing(1111, userFilterDto);

        verify(subscriptionService, times(1)).getFollowing(1111, userFilterDto);
    }

    @Test
    void testGetFollowersCount() {
        subscriptionController.getFollowersCount(1111);

        verify(subscriptionService, times(1)).getFollowersCount(1111);
    }

    @Test
    void testGetFollowingCount() {
        subscriptionController.getFollowingCount(1111);

        verify(subscriptionService, times(1)).getFolloweesCount(1111);
    }
}
