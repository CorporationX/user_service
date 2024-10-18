package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.enums.PreferredContact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.impl.SubscriptionServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final long EXPECTED_COUNT = 5L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "123456789";
    private static final String FOLLOWED_USERNAME = "followedUser";
    private static final String FOLLOWED_EMAIL = "followed@example.com";

    @Mock
    private SubscriptionServiceImpl subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private UserDto createTestUserDto(long id, String username, String email) {
        return new UserDto(id, true, email, "@User", username, "79999999999", PreferredContact.EMAIL);
    }

    private List<UserDto> createUserDtoList(UserDto... users) {
        return new ArrayList<>(List.of(users));
    }

    @Test
    void testFollowUser_checkDataValidationExceptionIfNull() {
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(null, null));
    }

    @Test
    void testFollowUser_Success() {
        subscriptionController.followUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionService, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testUnfollowUser_Success() {
        subscriptionController.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionService, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testGetFollowers_Success() {
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> followers = createUserDtoList(
                createTestUserDto(2L, TEST_USERNAME, TEST_EMAIL)
        );

        when(subscriptionService.getFollowers(FOLLOWER_ID, filter)).thenReturn(followers);

        List<UserDto> result = subscriptionController.getFollowers(FOLLOWER_ID, filter);

        assertEquals(1, result.size());
        assertEquals(TEST_USERNAME, result.get(0).getUsername());
    }

    @Test
    void testGetFollowersCount_Success() {
        when(subscriptionService.getFollowersCount(FOLLOWER_ID)).thenReturn(EXPECTED_COUNT);

        long result = subscriptionController.getFollowersCount(FOLLOWER_ID);

        assertEquals(EXPECTED_COUNT, result);
        verify(subscriptionService, times(1)).getFollowersCount(FOLLOWER_ID);
    }

    @Test
    void testGetFollowing_Success() {
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> following = createUserDtoList(
                createTestUserDto(2L, FOLLOWED_USERNAME, FOLLOWED_EMAIL)
        );

        when(subscriptionService.getFollowing(FOLLOWER_ID, filter)).thenReturn(following);

        List<UserDto> result = subscriptionController.getFollowing(FOLLOWER_ID, filter);

        assertEquals(1, result.size());
        assertEquals(FOLLOWED_USERNAME, result.get(0).getUsername());
    }

    @Test
    void testGetFollowingCount_Success() {
        when(subscriptionService.getFollowingCount(FOLLOWER_ID)).thenReturn(EXPECTED_COUNT);

        long result = subscriptionController.getFollowingCount(FOLLOWER_ID);

        assertEquals(EXPECTED_COUNT, result);
        verify(subscriptionService, times(1)).getFollowingCount(FOLLOWER_ID);
    }
}