package school.faang.user_service.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.subscription.SubscriptionController;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.exception.message.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.USER_UNFOLLOWING_HIMSELF_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private ArgumentCaptor<Long> followerArgumentCaptor;
    private ArgumentCaptor<Long> followeeArgumentCaptor;
    private ArgumentCaptor<UserFilterDto> filterArgumentCaptor;

    private Long followerId;
    private Long followeeId;

    @BeforeEach
    void init() {
        followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        filterArgumentCaptor = ArgumentCaptor.forClass(UserFilterDto.class);

        followerId = 1L;
        followeeId = 2L;
    }

    @Nested
    class PositiveTests {
        @DisplayName("should call subscriptionService.followUser() when following not yourself")
        @Test
        void shouldFollowUserWhenFollowingNotYourself() {
            var followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
            var followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);

            subscriptionController.followUser(followerId, followeeId);

            verify(subscriptionService).followUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should call subscriptionService.unfollowUser() when unfollowing not yourself")
        @Test
        void shouldUnfollowUserWhenUnfollowingNotYourself() {
            var followerArgumentCaptor = ArgumentCaptor.forClass(Long.class);
            var followeeArgumentCaptor = ArgumentCaptor.forClass(Long.class);

            subscriptionController.unfollowUser(followerId, followeeId);

            verify(subscriptionService).unfollowUser(followerArgumentCaptor.capture(), followeeArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should call subscriptionService.getFollowers()")
        @Test
        void shouldReturnFollowers() {
            var filter = new UserFilterDto();

            subscriptionController.getFollowers(followeeId, filter);

            verify(subscriptionService).getFollowers(followeeArgumentCaptor.capture(), filterArgumentCaptor.capture());
            assertEquals(filter, filterArgumentCaptor.getValue());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should call subscriptionService.getFollowersCount()")
        @Test
        void shouldReturnFollowersCount() {
            subscriptionController.getFollowersCount(followeeId);

            verify(subscriptionService).getFollowersCount(followeeArgumentCaptor.capture());
            assertEquals(followeeId, followeeArgumentCaptor.getValue());
        }

        @DisplayName("should call subscriptionService.getFollowing()")
        @Test
        void shouldReturnSubscriptions() {
            var filter = new UserFilterDto();

            subscriptionController.getFollowing(followerId, filter);

            verify(subscriptionService).getFollowing(followerArgumentCaptor.capture(), filterArgumentCaptor.capture());
            assertEquals(filter, filterArgumentCaptor.getValue());
            assertEquals(followerId, followerArgumentCaptor.getValue());
        }

        @DisplayName("should call subscriptionService.getFollowingCount()")
        @Test
        void shouldReturnSubscriptionsCount() {
            subscriptionController.getFollowingCount(followerId);

            verify(subscriptionService).getFollowingCount(followerArgumentCaptor.capture());
            assertEquals(followerId, followerArgumentCaptor.getValue());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when followee is follower during following")
        @Test
        void shouldThrowExceptionWhenFollowingYourself() {
            followeeId = followerId;

            var actualException = assertThrows(DataValidationException.class,
                    () -> subscriptionController.followUser(followerId, followeeId));

            assertEquals(USER_FOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
            verify(subscriptionService, times(0)).followUser(followerId, followeeId);
        }

        @DisplayName("should throw exception when followee is follower during unfollowing")
        @Test
        void shouldThrowExceptionWhenUnfollowingYourself() {
            followeeId = followerId;

            var actualException = assertThrows(DataValidationException.class,
                    () -> subscriptionController.unfollowUser(followerId, followeeId));

            assertEquals(USER_UNFOLLOWING_HIMSELF_EXCEPTION.getMessage(), actualException.getMessage());
            verify(subscriptionService, times(0)).followUser(followerId, followeeId);
        }
    }
}