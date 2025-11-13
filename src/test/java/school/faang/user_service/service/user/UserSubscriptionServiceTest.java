package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.ExperienceFilter;
import school.faang.user_service.filters.NameFilter;
import school.faang.user_service.filters.PhoneFilter;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceTest {

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final long ANOTHER_USER_ID = 3L;
    private static final int EXPECTED_COUNT = 3;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserSubscriptionService userSubscriptionService;

    @BeforeEach
    public void setUp() {
        List<UserFilter> userFilters = List.of(
                new NameFilter(),
                new ExperienceFilter(),
                new PhoneFilter()
        );

        userSubscriptionService = new UserSubscriptionServiceImpl(
                subscriptionRepository,
                userMapper,
                userFilters
        );
    }

    @Test
    void followUser_ShouldFollowWhenValidIds() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID,
                FOLLOWEE_ID))
                .thenReturn(false);

        userSubscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void followUser_ShouldThrowExceptionWhenSameUser() {
        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID));
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void followUser_ShouldThrowExceptionWhenAlreadySubscribed() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID,
                FOLLOWEE_ID))
                .thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID));
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void unfollowUser_ShouldUnfollowWhenValidIds() {

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID,
                FOLLOWEE_ID))
                .thenReturn(true);

        userSubscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void unfollowUser_ShouldThrowExceptionWhenSameUser() {
        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWER_ID));
        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void unfollowUser_ShouldThrowExceptionWhenNotSubscribed() {

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID,
                FOLLOWEE_ID))
                .thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID));
        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void getFollowersCount_ShouldReturnCorrectCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWEE_ID))
                .thenReturn(EXPECTED_COUNT);

        CountResponse result = userSubscriptionService.getFollowersCount(FOLLOWEE_ID);

        assertEquals(new CountResponse(EXPECTED_COUNT), result);
    }

    @Test
    void getFolloweesCount_ShouldReturnCorrectCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(FOLLOWER_ID))
                .thenReturn(EXPECTED_COUNT);

        CountResponse result = userSubscriptionService.getFolloweesCount(FOLLOWER_ID);

        assertEquals(new CountResponse(EXPECTED_COUNT), result);
    }

    @Test
    void getFollowers_ShouldReturnFilteredUsersByName() {
        UserFiltersDto filter = new UserFiltersDto("anna", null, 0,
                Integer.MAX_VALUE);
        User user1 = createUser(FOLLOWEE_ID, "anna", 3);
        User user2 = createUser(ANOTHER_USER_ID, "kirill", 5);
        UserDto expectedUser = createUserDto(FOLLOWEE_ID, "anna");

        assertFollowersFiltered(filter, Stream.of(user1, user2), expectedUser);
    }

    @Test
    void getFollowers_ShouldReturnFilteredUsersByExperience() {
        UserFiltersDto filter = new UserFiltersDto(null, null, 5, 10);
        User user1 = createUser(FOLLOWEE_ID, "user1", 3);
        User user2 = createUser(ANOTHER_USER_ID, "user2", 7);
        UserDto expectedUser = createUserDto(ANOTHER_USER_ID, "user2");

        assertFollowersFiltered(filter, Stream.of(user1, user2), expectedUser);
    }

    @Test
    void getFollowees_ShouldReturnFilteredUsersByName() {
        UserFiltersDto filter = new UserFiltersDto("maria", null, 0,
                Integer.MAX_VALUE);
        User user1 = createUser(FOLLOWEE_ID, "maria", 3);
        User user2 = createUser(ANOTHER_USER_ID, "sergey", 5);
        UserDto expectedUser = createUserDto(FOLLOWEE_ID, "maria");

        assertFolloweesFiltered(filter, Stream.of(user1, user2), expectedUser);
    }

    @Test
    void getFollowees_ShouldReturnFilteredUsersByExperience() {

        UserFiltersDto filter = new UserFiltersDto(null, null, 4, 6);
        User user1 = createUser(FOLLOWEE_ID, "user1", 3);
        User user2 = createUser(ANOTHER_USER_ID, "user2", 5);
        UserDto expectedUser = createUserDto(ANOTHER_USER_ID, "user2");

        assertFolloweesFiltered(filter, Stream.of(user1, user2), expectedUser);
    }

    private User createUser(long id, String username, int experience) {
        return User.builder()
                .id(id)
                .username(username)
                .experience(experience)
                .build();
    }

    private UserDto createUserDto(long id, String username) {
        return new UserDto(id, username, null, null, null);
    }

    private void assertFolloweesFiltered(UserFiltersDto filter, Stream<User> users, UserDto expectedUser) {
        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(users);
        when(userMapper.toUserDto(any(User.class))).thenReturn(expectedUser);

        List<UserDto> result = userSubscriptionService.getFollowees(FOLLOWER_ID, filter);

        verify(subscriptionRepository).findByFollowerId(FOLLOWER_ID);
        assertEquals(List.of(expectedUser), result);
    }

    private void assertFollowersFiltered(UserFiltersDto filter, Stream<User> users, UserDto expectedUser) {
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(users);
        when(userMapper.toUserDto(any(User.class))).thenReturn(expectedUser);

        List<UserDto> result = userSubscriptionService.getFollowers(FOLLOWEE_ID, filter);

        verify(subscriptionRepository).findByFolloweeId(FOLLOWEE_ID);
        assertEquals(List.of(expectedUser), result);
    }
}
