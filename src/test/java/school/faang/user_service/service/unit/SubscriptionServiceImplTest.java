package school.faang.user_service.service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserFilter mockFilter;

    @Mock
    private UserFilter secondMockFilter;

    private SubscriptionServiceImpl subscriptionService;

    private User user1;
    private User user2;
    private UserDto dto1;
    private UserDto dto2;
    private List<ContactDto> contacts;

    @BeforeEach
    void setup() {
        user1 = User.builder().id(1L).username("user1").experience(10).phone("123").build();
        user2 = User.builder().id(2L).username("user2").experience(5).phone("456").build();

        contacts = List.of(new ContactDto("123", "TELEGRAM"));

        dto1 = new UserDto(1L, "user1", "user1@gmail.com", null, contacts);
        dto2 = new UserDto(2L, "user2", "user2@gmail.com", null, contacts);

        subscriptionService = new SubscriptionServiceImpl(subscriptionRepository, List.of(mockFilter, secondMockFilter), userMapper);
    }

    @Test
    @DisplayName("Подписка — если уже подписан, выбрасывается исключение")
    void testFollowUser_whenAlreadyFollowing_thenThrowException() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(1L, 2L));
    }

    @Test
    @DisplayName("Подписка")
    void testFollowUser_whenNotFollowing_thenSuccess() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);

        subscriptionService.followUser(1L, 2L);

        verify(subscriptionRepository).followUser(1L, 2L);
    }

    @Test
    @DisplayName("Отписка")
    void testUnfollowUser_whenCalled_thenSuccess() {
        subscriptionService.unfollowUser(1L, 2L);

        verify(subscriptionRepository).unfollowUser(1L, 2L);
    }

    @Test
    @DisplayName("Счётчик подписчиков и подписок")
    void testGetCounts_whenCalled_thenReturnCorrectValues() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(5L)).thenReturn(3);
        when(subscriptionRepository.findFolloweesAmountByFollowerId(10L)).thenReturn(7);

        assertEquals(3, subscriptionService.getFollowersCount(5L));
        assertEquals(7, subscriptionService.getFollowingCount(10L));
    }

    @Test
    @DisplayName("Получение подписок — все фильтры корректны")
    void testGetFollowing_whenFiltersApplicable_thenApplyThem() {
        when(subscriptionRepository.findByFollowerId(42L)).thenReturn(Stream.of(user1));
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(mockFilter.isApplicable(any())).thenReturn(true);
        when(mockFilter.apply(any(), any())).then(invocation -> invocation.getArgument(0));

        List<UserDto> result = subscriptionService.getFollowing(42L, new UserFilterDto());

        assertEquals(List.of(dto1), result);
        verify(mockFilter).apply(any(), any());
    }

    @Test
    @DisplayName("Получение подписок — фильтры не установлены")
    void testGetFollowing_whenFiltersNotApplicable_thenSkipThem() {
        when(subscriptionRepository.findByFollowerId(42L)).thenReturn(Stream.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);
        when(mockFilter.isApplicable(any())).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowing(42L, new UserFilterDto());

        assertEquals(List.of(dto1, dto2), result);
        verify(mockFilter, never()).apply(any(), any());
    }

    @Test
    @DisplayName("Фильтрация подписок — фильтр срабатывает")
    void testGetFollowers_whenOneFilterApplied_thenFilterUsersCorrectly() {
        when(subscriptionRepository.findByFolloweeId(22L)).thenReturn(Stream.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(mockFilter.isApplicable(any())).thenReturn(true);
        when(mockFilter.apply(any(), any())).then(invocation -> {
            Stream<User> input = invocation.getArgument(0);
            return input.filter(user -> user.equals(user1));
        });

        List<UserDto> result = subscriptionService.getFollowers(22L, new UserFilterDto());

        assertEquals(List.of(dto1), result);
        verify(mockFilter).apply(any(), any());
    }

    @Test
    @DisplayName("Получение подписок — комбинация фильтров, один исключает")
    void testFollowing_whenMultipleFilters_thenApplyAllCorrectly() {
        when(subscriptionRepository.findByFollowerId(22L)).thenReturn(Stream.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(mockFilter.isApplicable(any())).thenReturn(true);
        when(mockFilter.apply(any(), any())).then(invocation -> invocation.getArgument(0));
        when(secondMockFilter.isApplicable(any())).thenReturn(true);
        when(secondMockFilter.apply(any(), any())).thenAnswer(invocation -> {
            Stream<User> input = invocation.getArgument(0);
            return input.filter(user -> user.equals(user1));
        });

        List<UserDto> result = subscriptionService.getFollowing(22L, new UserFilterDto());

        assertEquals(List.of(dto1), result);
        verify(userMapper).toUserDto(user1);
        verify(userMapper, never()).toUserDto(user2);
    }
}

