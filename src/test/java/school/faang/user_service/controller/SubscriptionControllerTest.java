package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    void testFollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.followUser(followerId, followeeId);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(followerId, followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }

    @Test
    void testFollowUser_sameIds() {
        long followerId = 1;
        long followeeId = 1;

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId)
        );
        assertEquals("Unable to follow/unfollow yourself", e.getMessage());

        Mockito.verifyNoInteractions(subscriptionService);
    }

    @Test
    void testUnfollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionController.unfollowUser(followerId, followeeId);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }

    @Test
    void testUnfollowUser_sameIds() {
        long followerId = 1;
        long followeeId = 1;

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followeeId)
        );
        assertEquals("Unable to follow/unfollow yourself", e.getMessage());

        Mockito.verifyNoInteractions(subscriptionService);
    }

    @Test
    void testGetFollowers() {
        long followeeId = 1;
        UserFilterDto filter = new UserFilterDto();
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .username("name1")
                .email("mail1.ru")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("name2")
                .email("mail2.ru")
                .build();
        Mockito.when(subscriptionService.getFollowers(followeeId, filter))
                .thenReturn(List.of(userDto1, userDto2));

        List<UserDto> resultList = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(userDto1, resultList.get(0));
        assertEquals(userDto2, resultList.get(1));
        assertEquals(2, resultList.size());
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(followeeId, filter);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }

    @Test
    void testGetFollowersCount() {
        long followeeId = 1;
        int expectedCount = 10;
        Mockito.when(subscriptionService.getFollowersCount(followeeId))
                .thenReturn(expectedCount);

        int followersCount = subscriptionController.getFollowersCount(followeeId);

        assertEquals(expectedCount, followersCount);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(followeeId);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }

    @Test
    void testGetFolloweesCount() {
        long followerId = 1;
        int expectedCount = 10;
        Mockito.when(subscriptionService.getFolloweesCount(followerId))
                .thenReturn(expectedCount);

        int followeesCount = subscriptionController.getFolloweesCount(followerId);

        assertEquals(expectedCount, followeesCount);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFolloweesCount(followerId);
        Mockito.verifyNoMoreInteractions(subscriptionService);
    }
}