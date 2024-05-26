package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserFilterDtoValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @Mock
    UserFilterDtoValidator userFilterDtoValidator;
    private UserFilterDto userFilterDto;

    private long followerId;
    private long followeeId;
    private int expectationCount;
    private List<UserDto> expectationUsersDto;

    @BeforeEach
    public void setUp() {
        userFilterDto = new UserFilterDto();
        UserDto userDto = new UserDto();
        expectationUsersDto = List.of(userDto);
        followerId = 1L;
        followeeId = 2L;
        expectationCount = 55;
    }

    @Test
    public void testFollowUser() {
        subscriptionController.followUser(followerId, followeeId);
        verify(subscriptionValidator, times(1)).checkFollowerAndFolloweeAreDifferent(followerId, followeeId);
        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser() {
        subscriptionController.unfollowUser(followerId, followeeId);
        verify(subscriptionValidator, times(1)).checkFollowerAndFolloweeAreDifferent(followerId, followeeId);
        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        when(subscriptionService.getFollowers(followeeId, userFilterDto)).thenReturn(expectationUsersDto);

        List<UserDto> actualUsersDto = subscriptionController.getFollowers(followeeId, userFilterDto);

        verify(subscriptionValidator, times(1)).checkIdIsGreaterThanZero(followeeId);
        verify(userFilterDtoValidator, times(1)).checkUserFilterDtoIsNull(userFilterDto);
        verify(subscriptionService, times(1)).getFollowers(followeeId, userFilterDto);
        assertEquals(expectationUsersDto, actualUsersDto);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionService.getFollowersCount(followeeId)).thenReturn(expectationCount);

        int actualCount = subscriptionController.getFollowersCount(followeeId);

        verify(subscriptionValidator, times(1)).checkIdIsGreaterThanZero(followeeId);
        verify(subscriptionService, times(1)).getFollowersCount(followeeId);
        assertEquals(expectationCount, actualCount);
    }

    @Test
    public void testGetFollowing() {
        when(subscriptionService.getFollowing(followerId, userFilterDto)).thenReturn(expectationUsersDto);

        List<UserDto> actualUsersDto = subscriptionController.getFollowing(followerId, userFilterDto);

        verify(subscriptionValidator, times(1)).checkIdIsGreaterThanZero(followerId);
        verify(userFilterDtoValidator, times(1)).checkUserFilterDtoIsNull(userFilterDto);
        verify(subscriptionService, times(1)).getFollowing(followerId, userFilterDto);
        assertEquals(expectationUsersDto, actualUsersDto);
    }

    @Test
    public void testGetFollowingCount() {
        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectationCount);

        int actualCount = subscriptionController.getFollowingCount(followerId);

        verify(subscriptionValidator, times(1)).checkIdIsGreaterThanZero(followerId);
        verify(subscriptionService, times(1)).getFollowingCount(followerId);
        assertEquals(expectationCount, actualCount);
    }
}