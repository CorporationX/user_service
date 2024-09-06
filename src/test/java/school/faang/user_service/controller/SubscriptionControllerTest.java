package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUserWhenFollowerIsDifferentFromFollowee() {
        long followerId = 1L;
        long followeeId = 2L;

        subscriptionService.followUser(followerId, followeeId);
    }

    @Test
    public void testFollowUserWhenFollowerIsSameAsFollowee() {
        long userId = 1L;

        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(userId, userId));
    }

    @Test
    public void testUnFollowUserWhenFollowerIsDifferentFromFollowee() {
        long followerId = 1L;
        long followeeId = 2L;

        subscriptionService.unFollowUser(followerId, followeeId);
    }

    @Test
    public void testUnFollowUserWhenFollowerIsSameFromFollowee() {
        long userId = 1L;

        assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(userId, userId));
    }

    /*@Test
    public void testGetFollowers() {

        UserDto user1 = new UserDto(1L, "Ruslan", "ruslan@example.ru");
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("Ruslan");

        when(subscriptionService.getFollowers(1L, filterDto)).thenReturn(List.of(user1));
        List<UserDto> followers = subscriptionController.getFollowers(1L, filterDto);

        assertEquals(1, followers.size());
        assertEquals("Ruslan", followers.get(0).getUsername());
    }*/

    @Test
    public void testOfGetFollowersCount() {
        long followerId = 1L;

        subscriptionController.getFollowersCount(followerId);
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;

        subscriptionController.getFollowingCount(followerId);
    }
}
