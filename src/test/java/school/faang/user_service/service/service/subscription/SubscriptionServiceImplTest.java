package school.faang.user_service.service.service.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.subscription.SubscriptionAlreadyExistsException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionServiceImpl;
import school.faang.user_service.service.subscription.SubscriptionValidator;
import school.faang.user_service.service.subscription.filters.SubscriptionUserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionUserFilter userFilter;

    @Spy
    private UserMapper userMapper;

    @Mock
    private SubscriptionValidator validator;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private final Long followerId = 2L;
    private final Long followeeId = 1L;
    private final UserFilterDto userFilterDto = new UserFilterDto(null, null, null,
            null, null, null,
            null, null, null,
            null, null, null);

    @Test
    @DisplayName("Successfully subscription creation")
    void testFollowUser_SuccessfullyFollowing() {
        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Subscription with failed subscription validation")
    void testFollowUser_FailedSubscriptionValidation() {
        doThrow(DataValidationException.class)
                .when(validator).checkSubscriptionOnHimself(followerId, followerId);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Subscription with failed subscription existing validation")
    void testFollowUser_FailedSubscriptionExistingValidation() {
        doThrow(SubscriptionAlreadyExistsException.class)
                .when(validator).checkIfSubscriptionExists(followerId, followeeId);

        assertThrows(SubscriptionAlreadyExistsException.class,
                () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    @DisplayName("Subscription with failed users validation")
    void testFollowUser_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUsers(followerId, followeeId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    @DisplayName("Successfully unsubscription")
    void testUnfollowUser_SuccessfullyUnfollowing() {
        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Unsubscription failed with subscription validation")
    void testUnfollowUser_FailedSubscriptionValidation() {
        doThrow(SubscriptionNotFoundException.class)
                .when(validator).checkIfSubscriptionExists(followerId, followeeId);

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    @DisplayName("Successfully getting followers")
    void testGetFollowers_SuccessfullyGettingFollowers() {
        List<User> followers = initUsers();
        List<SubscriptionUserDto> followersDto = getSubscriptionUserDtos(followers);
        Stream<User> followersStream = followers.stream();

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followersStream);
        when(userFilter.filterUsers(followersStream, userFilterDto)).thenReturn(followers);

        var result = subscriptionService.getFollowers(followeeId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        verify(userFilter, times(1)).filterUsers(followersStream, userFilterDto);
        verify(userMapper, times(2)).toSubscriptionUserDtos(followers);
        assertEquals(followersDto, result);
    }

    @Test
    @DisplayName("Getting followers failed with user validation")
    void testGetFollowers_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUser(followeeId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getFollowers(followeeId, userFilterDto));
    }

    @Test
    @DisplayName("Successfully getting followers count")
    void testGetFollowersCount_SuccessfullyGettingFollowersCount() {
        List<User> followers = initUsers();

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followers.size());

        int result = subscriptionService.getFollowersCount(followeeId);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
        assertEquals(followers.size(), result);
    }

    @Test
    @DisplayName("Getting followers count failed with user validation")
    void testGetFollowersCount_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUser(followeeId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getFollowersCount(followeeId));
    }

    @Test
    @DisplayName("Successfully getting following")
    void testGetFollowing_SuccessfullyGettingFollowing() {
        List<User> followings = initUsers();
        List<SubscriptionUserDto> followingsDto = getSubscriptionUserDtos(followings);
        Stream<User> followingsStream = followings.stream();

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(followingsStream);
        when(userFilter.filterUsers(followingsStream, userFilterDto)).thenReturn(followings);

        var result = subscriptionService.getFollowings(followerId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFollowerId(followerId);
        verify(userFilter, times(1)).filterUsers(followingsStream, userFilterDto);
        verify(userMapper, times(2)).toSubscriptionUserDtos(followings);
        assertEquals(followingsDto, result);
    }

    @Test
    @DisplayName("Getting following failed with user validation")
    void testGetFollowing_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUser(followerId);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionService.getFollowings(followerId, userFilterDto));
    }

    @Test
    @DisplayName("Successfully getting following count")
    void testGetFollowingCount_SuccessfullyGettingFollowingCount() {
        List<User> followings = initUsers();

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(followings.size());

        int result = subscriptionService.getFollowingCounts(followerId);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
        assertEquals(followings.size(), result);
    }

    @Test
    @DisplayName("Getting following count failed with user validation")
    void testGetFollowingCount_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUser(followerId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getFollowingCounts(followerId));
    }

    private List<SubscriptionUserDto> getSubscriptionUserDtos(List<User> users) {
        return userMapper.toSubscriptionUserDtos(users);
    }

    private List<User> initUsers() {
        return List.of(
                User.builder()
                        .id(followerId)
                        .build()
        );
    }
}
