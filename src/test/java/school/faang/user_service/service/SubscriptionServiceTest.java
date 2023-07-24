package school.faang.user_service.service;

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
import school.faang.user_service.service.user.filter.UserNameFilter;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followerId = 2;
    private final long followeeId = 1;
    private List<User> usersStream;

    @BeforeEach
    public void setUpUsersStream() {
        usersStream = List.of(
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
    }

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

    @Test
    public void shouldReturnFollowersList() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePatter("John")
                .build();

        subscriptionService = new SubscriptionService(userMapper, subscriptionRepository,
                userRepository, List.of(new UserNameFilter()));

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(usersStream.stream());

        Stream<User> desiredUsersStream = usersStream.stream()
                .filter(user -> user.getUsername().contains("John"));

        List<UserDto> receivedUsersDto = subscriptionService.getFollowers(followeeId, filters);

        Assertions.assertEquals(userMapper.toDtoList(desiredUsersStream.toList()), receivedUsersDto);
        Mockito.verify(subscriptionRepository).findByFolloweeId(followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenFolloweeNotExistsWithFilter() {
        UserFilterDto filter = new UserFilterDto();

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.getFollowers(followeeId, filter));
    }

    @Test
    public void shouldReturnFolloweesList() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePatter("John")
                .build();

        UserNameFilter nameFilter = Mockito.mock(UserNameFilter.class);
        subscriptionService = new SubscriptionService(userMapper, subscriptionRepository,
                userRepository, List.of(nameFilter));

        Mockito.when(userRepository.existsById(followerId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findByFollowerId(followerId))
                .thenReturn(usersStream.stream());

        Stream<User> desiredUsersStream = usersStream.stream()
                        .filter(user -> user.getUsername().contains("John"));

        Mockito.lenient().when(nameFilter.isApplicable(filters)).thenReturn(true);
        Mockito.lenient().when(nameFilter.apply(usersStream.stream(), filters)).thenReturn(desiredUsersStream);

        List<UserDto> receivedUsersDto = subscriptionService.getFollowing(followerId, filters);

        Assertions.assertEquals(userMapper.toDtoList(desiredUsersStream.toList()), receivedUsersDto);
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
