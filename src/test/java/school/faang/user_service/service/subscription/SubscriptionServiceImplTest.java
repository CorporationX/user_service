package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserFilter userFilter1;

    @Mock
    private UserFilter userFilter2;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User user1;
    private User user2;
    private UserFilterDto filters;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        filters = new UserFilterDto();
        filters.setNamePattern("^[А-ЯA-Z]");

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("Username1");
        user1.setEmail("email1@email.com");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("username2");
        user2.setEmail("email2@email.com");

        userDto1 = UserDto.builder()
                .id(1L)
                .username("username1")
                .email("email1@email.com")
                .phone("+123")
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .username("username2")
                .email("email2@email.com")
                .phone("+123")
                .build();

        when(subscriptionRepository.findByFollowerId(anyLong())).thenReturn(Stream.of(user1, user2));
        when(subscriptionRepository.findByFolloweeId(anyLong())).thenReturn(Stream.of(user1, user2));

        when(userFilters.stream()).thenReturn(Stream.of(userFilter1, userFilter2));

        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userMapper.toDto(user2)).thenReturn(userDto2);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    void followUser_ValidIds(long followerId, long followeeId) {
        subscriptionRepository.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    void unfollowUser_ValidIds(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowers_ShouldReturnFilteredAndMappedUsers() {
        List<UserDto> correctAnswer = List.of(userDto1);
        when(userFilter1.isApplicable(filters)).thenReturn(true);
        when(userFilter2.isApplicable(filters)).thenReturn(false);
        when(userFilter1.apply(user1, filters)).thenReturn(true);
        when(userFilter2.apply(user2, filters)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowers(1L, filters);

        verify(subscriptionRepository).findByFollowerId(1L);
        verify(userMapper).toDto(user1);
        verify(userMapper, never()).toDto(user2);
        assertEquals(correctAnswer, result);
    }

    @Test
    void getFollowers_ShouldReturnEmptyListWhenNoFiltersMatch() {
        int correctAnswer = 0;
        when(userFilter1.isApplicable(filters)).thenReturn(true);
        when(userFilter2.isApplicable(filters)).thenReturn(true);
        when(userFilter1.apply(user1, filters)).thenReturn(false);
        when(userFilter2.apply(user2, filters)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowers(1L, filters);

        assertEquals(correctAnswer, result.size());
    }

    @Test
    void getFollowers_ShouldReturnAllUsersWhenNoFiltersAreApplicable() {
        List<UserDto> correctAnswer = List.of(userDto1, userDto2);
        when(userFilter1.isApplicable(filters)).thenReturn(false);
        when(userFilter2.isApplicable(filters)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowers(1L, filters);

        assertEquals(correctAnswer, result);
    }

    @Test
    void getFollowing_ShouldReturnFilteredAndMappedUsers() {
        List<UserDto> correctAnswer = List.of(userDto1);
        when(userFilter1.isApplicable(filters)).thenReturn(true);
        when(userFilter2.isApplicable(filters)).thenReturn(false);
        when(userFilter1.apply(user1, filters)).thenReturn(true);
        when(userFilter2.apply(user2, filters)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowing(1L, filters);

        verify(subscriptionRepository).findByFolloweeId(1L);
        verify(userMapper).toDto(user1);
        verify(userMapper, never()).toDto(user2);
        assertEquals(correctAnswer, result);
    }

    @Test
    void getFollowing_ShouldReturnEmptyListWhenNoFiltersMatch() {
        int correctAnswer = 0;
        when(userFilter1.isApplicable(filters)).thenReturn(true);
        when(userFilter2.isApplicable(filters)).thenReturn(true);
        when(userFilter1.apply(user1, filters)).thenReturn(false);
        when(userFilter2.apply(user2, filters)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowing(1L, filters);

        assertEquals(correctAnswer, result.size());
    }

    @Test
    void getFollowing_ShouldReturnAllUsersWhenNoFiltersAreApplicable() {
        List<UserDto> correctAnswer = List.of(userDto1, userDto2);
        when(userFilter1.isApplicable(filters)).thenReturn(false);
        when(userFilter2.isApplicable(filters)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowing(1L, filters);

        assertEquals(correctAnswer, result);
    }

    @Test
    void getFollowersCount_ShouldReturnCorrectCount() {
        long followerId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followerId)).thenReturn(expectedCount);
        int result = subscriptionService.getFollowersCount(followerId);

        assertEquals(expectedCount, result);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followerId);
    }

    @Test
    void getFollowersCount_WhenNoFollowers_ShouldReturnZero() {
        long followerId = 2L;
        int expectedCount = 0;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followerId)).thenReturn(expectedCount);
        int result = subscriptionService.getFollowersCount(followerId);

        assertEquals(expectedCount, result);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followerId);
    }

    @Test
    void getFollowersCount_ShouldInvokeRepositoryMethod() {
        long followerId = 3L;

        subscriptionService.getFollowersCount(followerId);

        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followerId);
    }

    @Test
    void getFollowingCount_ShouldReturnCorrectCount() {
        long followerId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);
        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, result);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    void getFollowingCount_WhenNoFollowers_ShouldReturnZero() {
        long followerId = 2L;
        int expectedCount = 0;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);
        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, result);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    void getFollowingCount_ShouldInvokeRepositoryMethod() {
        long followerId = 3L;

        subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }
}