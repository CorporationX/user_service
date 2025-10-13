package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.subscription.UserSubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionControllerTest {
    @InjectMocks
    private UserSubscriptionController subscriptionController;

    @Mock
    private UserSubscriptionService subscriptionService;

    @Mock
    private UserContext userContext;

    @Test
    void followUserShouldCallServiceWithCorrectIds() {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId())
                .thenReturn(followerId);

        subscriptionController.followUser(followeeId);

        verify(subscriptionService).followUser(followerId, followeeId);
    }

    @Test
    void followUserShouldThrowWhenFolloweeIdInvalid() {
        long invalidFolloweeId = -1L;

        assertThrows(DataValidationException.class, () ->
                subscriptionController.followUser(invalidFolloweeId));
    }

    @Test
    void followUserShouldThrowWhenFolloweeAndFollowerTheSameUser() {
        long followeeId = 1L;

        when(userContext.getUserId())
                .thenReturn(followeeId);

        assertThrows(ForbiddenException.class, () ->
                subscriptionController.followUser(followeeId));
    }

    @Test
    void unfollowUserShouldCallServiceWithCorrectIds() {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId())
                .thenReturn(followerId);

        subscriptionController.unfollowUser(followeeId);

        verify(subscriptionService).unfollowUser(followerId, followeeId);
    }

    @Test
    void unfollowUserShouldThrowWhenFolloweeIdInvalid() {
        long invalidFolloweeId = -1L;

        assertThrows(DataValidationException.class, () ->
                subscriptionController.unfollowUser(invalidFolloweeId));
    }

    @Test
    void unfollowUserShouldThrowWhenFolloweeAndFollowerTheSameUser() {
        long followeeId = 1L;

        when(userContext.getUserId())
                .thenReturn(followeeId);

        assertThrows(ForbiddenException.class, () ->
                subscriptionController.unfollowUser(followeeId));
    }

    @Test
    void getFollowersCountShouldReturnCountOfFollowersFromService() {
        long followeeId = 1L;

        when(subscriptionService.getFollowersCount(followeeId))
                .thenReturn(new CountResponse(100L));

        CountResponse followersCount = subscriptionController.getFollowersCount(followeeId);

        assertEquals(new CountResponse(100L), followersCount);
    }

    @Test
    void getFollowersCountShouldThrowWhenFolloweeIdInvalid() {
        long invalidFolloweeId = -1L;

        assertThrows(DataValidationException.class, () ->
                subscriptionController.getFollowersCount(invalidFolloweeId));
    }

    @Test
    void getFolloweesCountShouldReturnCountOfFolloweesFromService() {
        long followerId = 1L;

        when(subscriptionService.getFolloweesCount(followerId))
                .thenReturn(new CountResponse(15L));

        CountResponse followeesCount = subscriptionController.getFolloweesCount(followerId);

        assertEquals(new CountResponse(15L), followeesCount);
    }

    @Test
    void getFolloweesCountShouldThrowWhenFollowerIdInvalid() {
        long invalidFollowerId = -1L;

        assertThrows(DataValidationException.class, () ->
                subscriptionController.getFolloweesCount(invalidFollowerId));
    }

    @Test
    void getFollowersShouldReturnFilteredAndMappedFollowersFromService() {
        long followeeId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "John", "123777000", 15, 30);

        UserDto firstFollower = new UserDto(2L, "Johny", null, "11237770009", null);
        UserDto secondFollower = new UserDto(3L, "jOhN1", null, "212377700099", null);
        UserDto thirdFollower = new UserDto(4L, "johhnnyy", null, "1237770007", null);

        List<UserDto> exceptedFollowers = List.of(firstFollower, secondFollower);

        when(subscriptionService.getFollowers(followeeId, userFiltersDto))
                .thenReturn(exceptedFollowers);

        List<UserDto> actualFollowers = subscriptionController.getFollowers(followeeId, userFiltersDto);

        verify(subscriptionService).getFollowers(followeeId, userFiltersDto);
        assertEquals(exceptedFollowers, actualFollowers);
    }

    @Test
    void getFollowersShouldThrowWhenFolloweeIdInvalid() {
        long invalidFolloweeId = -1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "?", "?", 0, 0);

        assertThrows(DataValidationException.class, () ->
                subscriptionController.getFollowers(invalidFolloweeId, userFiltersDto));
    }

    @Test
    void getFollowersShouldThrowWhenUserFiltersDtoInvalid() {
        long followeeId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                null, null, -1000, -1);

        assertThrows(DataValidationException.class, () ->
                subscriptionController.getFollowers(followeeId, userFiltersDto));
    }

    @Test
    void getFolloweesShouldReturnFilteredAndMappedFolloweesFromService() {
        long followerId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "Cyntia", "336699", 30, 45);

        UserDto firstFollowee = new UserDto(2L, "CyntiaJJJ", null, "783366991", null);
        UserDto secondFollowee = new UserDto(3L, "cyntiaaa", null, "331211113", null);
        UserDto thirdFollowee = new UserDto(4L, "CyNtIa54", null, "2333669932", null);

        List<UserDto> exceptedFollowees = List.of(firstFollowee, thirdFollowee);

        when(subscriptionService.getFollowees(followerId, userFiltersDto))
                .thenReturn(exceptedFollowees);

        List<UserDto> actualFollowees = subscriptionController.getFollowees(followerId, userFiltersDto);

        verify(subscriptionService).getFollowees(followerId, userFiltersDto);
        assertEquals(exceptedFollowees, actualFollowees);
    }

    @Test
    void getFolloweesShouldThrowWhenFollowerIdInvalid() {
        long invalidFollowerId = -1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "?", "?", 0, 0);

        assertThrows(DataValidationException.class, () ->
                subscriptionController.getFollowees(invalidFollowerId, userFiltersDto));
    }

    @Test
    void getFolloweesShouldThrowWhenUserFiltersDtoInvalid() {
        long followerId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                null, null, -1000, -1);

        assertThrows(DataValidationException.class, () ->
                subscriptionController.getFollowees(followerId, userFiltersDto));
    }
}
