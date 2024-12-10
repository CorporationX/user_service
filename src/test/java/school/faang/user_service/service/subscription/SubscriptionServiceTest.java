package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.redis.event.UserFollowerEvent;
import school.faang.user_service.redis.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.subscription.SubscriptionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.contact.PreferredContact.EMAIL;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Mock
    private SubscriptionValidator subscriptionValidation;

    @Mock
    private UserValidator userValidator;

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @Mock
    UserService userService;

    SubscriptionService subscriptionService;

    private UserFilterDto filter;
    private User firstUser;
    private User secondUser;
    private UserDto firstUserDto;
    private UserDto secondUserDto;

    private long userId;
    private long followerId;
    private long followeeId;
    private int followingsAmount;

    private Stream<User> users;
    private List<UserDto> expectedUsers;
    private List<UserFilter> userFilters;

    @BeforeEach
    public void setUp() {
        UserFilter MockUserNameFilter = mock(UserNameFilter.class);
        UserFilter MockUserEmailFilter = mock(UserEmailFilter.class);
        userFilters = new ArrayList<>(List.of(MockUserNameFilter, MockUserEmailFilter));

        subscriptionService = new SubscriptionService(subscriptionRepository,
                userMapper, userFilters, subscriptionValidation, userValidator,
                userService, followerEventPublisher);
    }

    @Test
    public void followUserTest() {
        ArgumentCaptor<UserFollowerEvent> followerEventCaptor = ArgumentCaptor.forClass(UserFollowerEvent.class);
        followerId = 1L;
        followeeId = 2L;
        boolean isExists = true;

        when(userService.existsById(followerId)).thenReturn(isExists);
        when(userService.existsById(followeeId)).thenReturn(isExists);
        doNothing().when(userValidator).validateUserExistence(isExists);
        doNothing().when(subscriptionValidation).isFollowingExistsValidate(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);

        verify(userValidator, times(2)).validateUserExistence(isExists);
        verify(subscriptionValidation).isFollowingExistsValidate(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
        verify(followerEventPublisher, times(1)).publish(followerEventCaptor.capture());

        UserFollowerEvent eventToSend = followerEventCaptor.getValue();
        assertEquals(followerId, eventToSend.getFollowerId());
        assertEquals(followeeId, eventToSend.getFolloweeId());
    }

    @Test
    public void unfollowUserTest() {
        followerId = 1L;
        followeeId = 2L;
        boolean isExists = true;

        when(userService.existsById(followerId)).thenReturn(isExists);
        when(userService.existsById(followeeId)).thenReturn(isExists);
        doNothing().when(userValidator).validateUserExistence(isExists);
        doNothing().when(subscriptionValidation).isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(userValidator, times(2)).validateUserExistence(isExists);
        verify(subscriptionValidation).isFollowingNotExistsValidate(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void getFollowersTest() {
        followerId = 1L;
        followeeId = 2L;
        userId = 10L;
        boolean isExists = true;

        firstUser = User.builder()
                .id(followerId)
                .username("firstUser")
                .email("first@email.com")
                .telegramChatId(98125891L)
                .contactPreference(new ContactPreference(1, firstUser, EMAIL))
                .build();

        secondUser = User.builder()
                .id(followeeId)
                .username("secondUser")
                .email("second@email.com")
                .telegramChatId(3454353L)
                .contactPreference(new ContactPreference(2, secondUser, EMAIL))
                .build();

        users = Stream.of(firstUser, secondUser);

        filter = UserFilterDto.builder()
                .namePattern("first")
                .emailPattern("first")
                .build();

        firstUserDto = new UserDto(followerId, "firstUser", "first@email.com", 98125891L, EMAIL);
        secondUserDto = new UserDto(followeeId, "secondUser", "second@email.com", 3454353L, EMAIL);
        expectedUsers = new ArrayList<>(List.of(firstUserDto, secondUserDto));

        when(userService.existsById(userId)).thenReturn(isExists);
        doNothing().when(userValidator).validateUserExistence(isExists);
        when(subscriptionRepository.findByFolloweeId(userId)).thenReturn(users);
        when(userFilters.get(0).isApplicable(filter)).thenReturn(true);
        when(userFilters.get(0).apply(users, filter)).thenReturn(users);
        when(userFilters.get(1).isApplicable(filter)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowers(userId, filter);

        verify(userValidator).validateUserExistence(isExists);
        verify(subscriptionRepository).findByFolloweeId(userId);
        verify(userMapper).entityStreamToDtoList(users);
        verify(userFilters.get(0)).isApplicable(filter);
        verify(userFilters.get(1)).isApplicable(filter);
        verify(userFilters.get(0)).apply(users, filter);

        assertEquals(expectedUsers.size(), result.size());
        assertEquals(expectedUsers.get(0), result.get(0));
        assertEquals(expectedUsers.get(1), result.get(1));
    }

    @Test
    public void getFollowersCountTest() {
        followerId = 1L;
        followingsAmount = 3;
        boolean isExists = true;

        when(userService.existsById(followeeId)).thenReturn(isExists);
        doNothing().when(userValidator).validateUserExistence(isExists);
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followingsAmount);

        int actualResult = subscriptionService.getFollowersCount(followeeId);

        verify(userValidator).validateUserExistence(isExists);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);

        assertEquals(followingsAmount, actualResult);
    }

    @Test
    public void getFollowingTest() {
        followerId = 1L;
        followeeId = 2L;
        userId = 10L;
        boolean isExists = true;

        firstUser = User.builder()
                .id(followerId)
                .username("firstUser")
                .email("first@email.com")
                .telegramChatId(9821491L)
                .contactPreference(new ContactPreference(1, firstUser, EMAIL))
                .build();

        secondUser = User.builder()
                .id(followeeId)
                .username("secondUser")
                .email("second@email.com")
                .telegramChatId(894189742L)
                .contactPreference(new ContactPreference(2, secondUser, EMAIL))
                .build();

        users = Stream.of(firstUser, secondUser);

        filter = UserFilterDto.builder()
                .namePattern("first")
                .emailPattern("first")
                .build();

        firstUserDto = new UserDto(followerId, "firstUser", "first@email.com", 9821491L, EMAIL);
        secondUserDto = new UserDto(followeeId, "secondUser", "second@email.com", 894189742L, EMAIL);
        expectedUsers = new ArrayList<>(List.of(firstUserDto, secondUserDto));

        when(userService.existsById(userId)).thenReturn(isExists);
        doNothing().when(userValidator).validateUserExistence(isExists);
        when(subscriptionRepository.findByFollowerId(userId)).thenReturn(users);
        when(userFilters.get(0).isApplicable(filter)).thenReturn(true);
        when(userFilters.get(0).apply(users, filter)).thenReturn(users);
        when(userFilters.get(1).isApplicable(filter)).thenReturn(true);
        when(userFilters.get(1).apply(users, filter)).thenReturn(users);

        List<UserDto> result = subscriptionService.getFollowing(userId, filter);

        verify(userValidator).validateUserExistence(isExists);
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper).entityStreamToDtoList(users);
        verify(userFilters.get(0)).isApplicable(filter);
        verify(userFilters.get(0)).apply(users, filter);
        verify(userFilters.get(1)).isApplicable(filter);
        verify(userFilters.get(1)).apply(users, filter);

        assertEquals(2, result.size());
        assertEquals(expectedUsers.get(0), result.get(0));
        assertEquals(expectedUsers.get(1), result.get(1));
    }

    @Test
    public void getFollowingCountTest() {
        followeeId = 2L;
        followingsAmount = 3;
        boolean isExists = true;

        when(userService.existsById(followeeId)).thenReturn(isExists);
        doNothing().when(userValidator).validateUserExistence(isExists);
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followeeId)).thenReturn(followingsAmount);

        int actualResult = subscriptionService.getFollowingCount(followeeId);

        verify(userValidator).validateUserExistence(isExists);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followeeId);

        assertEquals(followingsAmount, actualResult);
    }
}