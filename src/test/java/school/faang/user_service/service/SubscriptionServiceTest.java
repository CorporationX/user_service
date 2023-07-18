package school.faang.user_service.service;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.service.user.filter.UserAboutFilter;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserNameFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private List<UserFilter> userFilters;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followerId = 2;
    private final long followeeId = 1;


    @Test
    public void shouldAddNewFollowerById() {
        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfUserSubscribed() {
        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfFollowerDoesNotExist() {
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(userRepository.existsById(followerId)).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfFolloweeDoesNotExist() {
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(false);
        Mockito.lenient().when(userRepository.existsById(followerId)).thenReturn(true);

        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldDeleteFollowerById() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> subscriptionService.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionDoesNotExist() {
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0)).unfollowUser(followerId, followeeId);
    }

/*    @Test
    public void shouldReturnFollowersList() {
        UserFilterDto filter = new UserFilterDto();
        List<UserFilter> userFilters = new ArrayList<>();

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(desiredUsersStream);

        List<UserDto> receivedUsers = subscriptionService.getFollowers(followeeId, filter);

        Assertions.assertEquals(desiredUsersDto, receivedUsers);
        Mockito.verify(subscriptionRepository).findByFolloweeId(followeeId);
    }*/

    @Test
    public void shouldThrowExceptionWhenFolloweeNotExistsWithFilter() {
        UserFilterDto filter = new UserFilterDto();

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.getFollowers(followeeId, filter));
    }

    @Test
    public void shouldReturnFolloweesListByNameFilter() {
        List<User> usersStream = List.of(
                User.builder()
                        .username("MichaelJohnson")
                        .build(),
                User.builder()
                        .username("JohnDoe")
                        .build(),
                User.builder()
                        .username("JaneSmith")
                        .build()
        );

        UserFilterDto filters = UserFilterDto.builder()
                .namePatter("John")
                .build();

        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findByFollowerId(followerId))
                .thenReturn(usersStream.stream());

        List<UserDto> receivedUsersDto = subscriptionService.getFollowing(followerId, filters);

        Assertions.assertEquals(2, receivedUsersDto.size());
        Mockito.verify(subscriptionRepository).findByFollowerId(followerId);
    }

    @Test
    public void shouldThrowExceptionWhenFollowerNotExistsWithFilter() {
        UserFilterDto filter = new UserFilterDto();

        Mockito.when(userRepository.existsById(followerId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.getFollowing(followerId, filter));
    }

    @Test
    public void shouldReturnFollowersCount() {
        int desiredFollowersCount = 3;

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(desiredFollowersCount);
        int followersCount = subscriptionService.getFollowersCount(followeeId);

        Assertions.assertEquals(desiredFollowersCount, followersCount);
        Mockito.verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenFolloweeUserNotExists() {
        Mockito.when(userRepository.existsById(followeeId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowersCount(followeeId));
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void shouldReturnFolloweesCount() {
        int desiredFolloweesCount = 3;

        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(desiredFolloweesCount);
        int followeesCount = subscriptionService.getFollowingCount(followerId);

        Assertions.assertEquals(desiredFolloweesCount, followeesCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    public void shouldThrowExceptionWhenFollowerUserNotExists() {
        Mockito.lenient().when(userRepository.existsById(followeeId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowingCount(followerId));
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findFolloweesAmountByFollowerId(followerId);
    }
}
