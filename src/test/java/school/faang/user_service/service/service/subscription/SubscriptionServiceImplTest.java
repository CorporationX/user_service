package school.faang.user_service.service.service.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.subscription.SubscriptionAlreadyExistsException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionServiceImpl;
import school.faang.user_service.service.subscription.SubscriptionValidator;
import school.faang.user_service.service.subscription.filters.UserFiltersApplier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserFiltersApplier userFilter;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private SubscriptionValidator validator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private final Long followerId = 2L;
    private final Long followeeId = 1L;
    private final UserFilterDto userFilterDto = UserFilterDto.builder().build();

    @Test
    @DisplayName("Successfully subscription creation")
    void testFollowUser_SuccessfullyFollowing() {
        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Subscription with failed subscription validation")
    void testFollowUser_FailedSubscriptionValidation() {
        doThrow(DataValidationException.class)
                .when(validator).checkIfSubscriptionToHimself(followerId, followerId);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Subscription with failed subscription existing validation")
    void testFollowUser_FailedSubscriptionExistingValidation() {
        doThrow(SubscriptionAlreadyExistsException.class)
                .when(validator).checkIfSubscriptionNotExists(followerId, followeeId);

        assertThrows(SubscriptionAlreadyExistsException.class,
                () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    @DisplayName("Subscription with failed users validation")
    void testFollowUser_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUserIds(followerId, followeeId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    @DisplayName("Successfully unsubscription")
    void testUnfollowUser_SuccessfullyUnfollowing() {
        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Unsubscription failed with subscription validation")
    void testUnfollowUser_FailedSubscriptionValidation() {
        doThrow(SubscriptionNotFoundException.class)
                .when(validator).checkIfSubscriptionExists(followerId, followeeId);

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionService.unfollowUser(followerId, followeeId));
    }

    @Test
    @DisplayName("Successfully getting followers")
    void testGetFollowers_SuccessfullyGettingFollowers() {
        List<User> followers = initUsers();
        List<Long> followersIds = followers.stream()
                .map(User::getId)
                .toList();
        Stream<User> followersStream = followers.stream();

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followersStream);
        when(userFilter.applyFilters(followersStream, userFilterDto)).thenReturn(followers);

        var result = subscriptionService.getFollowers(followeeId, userFilterDto);
        List<Long> resultIds = result.stream()
                .map(SubscriptionUserDto::id)
                .toList();

        assertEquals(followersIds, resultIds);
        verify(subscriptionRepository).findByFolloweeId(followeeId);
        verify(userFilter).applyFilters(followersStream, userFilterDto);
        verify(userMapper).toSubscriptionUserDtos(followers);
    }

    @Test
    @DisplayName("Getting followers when followee doesn't have followers")
    void testGetFollowers_GetEmptyFollowers() {
        List<User> followers = new ArrayList<>();
        var followersStream = followers.stream();

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followersStream);
        when(userFilter.applyFilters(followersStream, userFilterDto)).thenReturn(followers);

        var result = subscriptionService.getFollowers(followeeId, userFilterDto);

        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByFolloweeId(followeeId);
        verify(userFilter).applyFilters(followersStream, userFilterDto);
        verify(userMapper).toSubscriptionUserDtos(followers);
    }

    @Test
    @DisplayName("Getting followers failed with user validation")
    void testGetFollowers_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUserIds(followeeId);

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
                .when(validator).validateUserIds(followeeId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getFollowersCount(followeeId));
    }

    @Test
    @DisplayName("Successfully getting following")
    void testGetFollowing_SuccessfullyGettingFollowing() {
        List<User> followings = initUsers();
        List<Long> followingIds = followings.stream()
                .map(User::getId)
                .toList();
        Stream<User> followingsStream = followings.stream();

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(followingsStream);
        when(userFilter.applyFilters(followingsStream, userFilterDto)).thenReturn(followings);

        var result = subscriptionService.getFollowings(followerId, userFilterDto);
        List<Long> resultIds = result.stream()
                .map(SubscriptionUserDto::id)
                .toList();

        assertEquals(followingIds, resultIds);
        verify(subscriptionRepository).findByFollowerId(followerId);
        verify(userFilter).applyFilters(followingsStream, userFilterDto);
        verify(userMapper).toSubscriptionUserDtos(followings);
    }

    @Test
    @DisplayName("Getting following when follower doesn't have following")
    void testGetFollowing_GetEmptyFollowing() {
        List<User> followings = new ArrayList<>();
        var followingsStream = followings.stream();

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(followingsStream);
        when(userFilter.applyFilters(followingsStream, userFilterDto)).thenReturn(followings);

        var result = subscriptionService.getFollowings(followerId, userFilterDto);

        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByFollowerId(followerId);
        verify(userFilter).applyFilters(followingsStream, userFilterDto);
        verify(userMapper).toSubscriptionUserDtos(followings);
    }

    @Test
    @DisplayName("Getting following failed with user validation")
    void testGetFollowing_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUserIds(followerId);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionService.getFollowings(followerId, userFilterDto));
    }

    @Test
    @DisplayName("Successfully getting following count")
    void testGetFollowingCount_SuccessfullyGettingFollowingCount() {
        List<User> followings = initUsers();

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(followings.size());

        int result = subscriptionService.getFollowingCounts(followerId);

        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
        assertEquals(followings.size(), result);
    }

    @Test
    @DisplayName("Getting following count failed with user validation")
    void testGetFollowingCount_FailedUsersValidation() {
        doThrow(EntityNotFoundException.class)
                .when(validator).validateUserIds(followerId);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getFollowingCounts(followerId));
    }

    private List<User> initUsers() {
        return List.of(
                User.builder()
                        .id(followerId)
                        .build()
        );
    }
}
