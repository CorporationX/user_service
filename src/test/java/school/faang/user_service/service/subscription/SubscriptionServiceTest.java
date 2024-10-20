package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class SubscriptionServiceTest {
    private long followerId;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    PremiumRepository premiumRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> filters;

    @Mock
    private EventPublisher eventPublisher;;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        followerId = 1L;
    }

    @Test
    void shouldFollowUserSuccessfully_WhenSubscriptionDoesNotExist() throws Exception, DataValidationException {
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void shouldThrowException_WhenSubscriptionAlreadyExists() {
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository, never()).followUser(followerId, followeeId);
    }

    @Test
    void shouldUnfollowUserSuccessfully_WhenCalled() {
        long followeeId = 2L;

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void shouldReturnFollowers_WhenNoFilterIsApplied() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        User user = User.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .build();

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void shouldReturnFilteredFollowers_WhenFilterIsApplied() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("username");
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        User user = User.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .build();

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void shouldFilterUsersCorrectly_WhenFilterIsApplied() throws Exception {
        Method method = SubscriptionServiceImpl.class.getDeclaredMethod("filterUsers", List.class, UserFilterDto.class);
        method.setAccessible(true);

        UserDto user1 = new UserDto(1L, "user1", "email1@example.com");
        UserDto user2 = new UserDto(2L, "user2", "email2@example.com");
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("user1");

        List<UserDto> users = Arrays.asList(user1, user2);

        UserFilter filterMock = Mockito.mock(UserFilter.class);
        Mockito.when(filterMock.apply(Mockito.any(UserDto.class), Mockito.any(UserFilterDto.class)))
                .thenAnswer(invocation -> {
                    UserDto user = invocation.getArgument(0);
                    UserFilterDto userFilter = invocation.getArgument(1);
                    return user.getUsername().contains(userFilter.getNamePattern());
                });

        SubscriptionServiceImpl service = new SubscriptionServiceImpl(
                subscriptionRepository,
                userMapper,
                Arrays.asList(filterMock),
                eventPublisher
        );

        @SuppressWarnings("unchecked")
        List<UserDto> result = (List<UserDto>) method.invoke(service, users, filter);

        assertEquals(1, result.size());
        assertEquals(user1, result.get(0));
    }

    @Test
    void shouldReturnFollowersCount_WhenCalled() {
        long followeeId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(expectedCount);

        int actualCount = subscriptionService.getFollowersCount(followeeId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionRepository, times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    void shouldReturnFollowing_WhenNoFilterIsApplied() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("username");

        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        User user = User.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .build();

        when(subscriptionRepository.findByFollowerId(followeeId))
                .thenReturn(Stream.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowing(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));

        verify(subscriptionRepository, times(1)).findByFollowerId(followeeId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void shouldReturnFollowingCount_WhenCalled() {
        int expectedCount = 5;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(expectedCount);

        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, result);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }
}
