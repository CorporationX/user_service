package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.controller.subscription.SubscriptionController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SubscriptionControllerTest {
    private long followerId;

    @Mock
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        followerId = 1l;
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFollowUserSuccessfully_WhenValidInputIsProvided() throws Exception, DataValidationException {
        long followeeId = 2L;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void shouldThrowException_WhenTryingToFollowYourself() throws DataValidationException, DataFormatException {
        long followeeId = 1L;

        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followeeId));
        verify(subscriptionService, never()).followUser(followerId, followeeId);
    }

    @Test
    void shouldUnfollowUserSuccessfully_WhenValidInputIsProvided() throws DataValidationException {
        long followeeId = 2L;

        subscriptionController.unfollowUser(followerId, followeeId);

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void shouldThrowException_WhenTryingToUnfollowYourself() throws DataValidationException {
        long followeeId = 1L;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionController.unfollowUser(followerId, followeeId);
        });

        assertEquals("Нельзя отписываться от самого себя.", exception.getMessage());
        verify(subscriptionService, never()).unfollowUser(followerId, followeeId);
    }

    @Test
    void shouldReturnFilteredFollowersByName_WhenFilterIsApplied() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("John.*");

        UserDto user1 = new UserDto(1L, "JohnDoe", "john@example.com");
        UserDto user2 = new UserDto(2L, "JaneDoe", "jane@example.com");

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(
                List.of(user1)
        );

        List<UserDto> result = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals("JohnDoe", result.get(0).getUsername());
    }

    @Test
    void shouldReturnFilteredFollowersByEmail_WhenEmailPatternIsProvided() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setEmailPattern(".*@example.com");

        UserDto user1 = new UserDto(1L, "JohnDoe", "john@example.com");
        UserDto user2 = new UserDto(2L, "JaneDoe", "jane@another.com");

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(
                List.of(user1)
        );

        List<UserDto> result = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0).getEmail());
    }

    @Test
    void shouldReturnEmptyList_WhenNoFollowersExist() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(
                new ArrayList<>()
        );

        List<UserDto> result = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnAllFollowers_WhenNoFilterIsApplied() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();

        UserDto user1 = new UserDto(1L, "JohnDoe", "john@example.com");
        UserDto user2 = new UserDto(2L, "JaneDoe", "jane@example.com");

        List<UserDto> followers = List.of(user1, user2);
        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(followers);

        List<UserDto> result = subscriptionController.getFollowers(followeeId, filter);

        assertEquals(2, result.size());
        assertEquals("JohnDoe", result.get(0).getUsername());
        assertEquals("JaneDoe", result.get(1).getUsername());
    }
    @Test
    public void shouldReturnFollowersCount_WhenValidFollowerIdIsProvided() {
        int expectedCount = 5;

        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);

        int actualCount = subscriptionController.getFollowersCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionService, times(1)).getFollowersCount(followerId);
    }

    @Test
    void shouldReturnFollowingList_WhenNamePatternFilterIsApplied() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("username");

        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        when(subscriptionService.getFollowing(followeeId, filter))
                .thenReturn(List.of(userDto));

        List<UserDto> result = subscriptionController.getFollowing(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));

        verify(subscriptionService, times(1)).getFollowing(followeeId, filter);
    }
}
