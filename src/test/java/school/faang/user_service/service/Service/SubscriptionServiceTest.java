package school.faang.user_service.service.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.service.subscription.filter.NameFilter;
import school.faang.user_service.service.subscription.filter.UserFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final String NAME_ALEX = "Alex";
    private static final String NAME_JOHN = "John";
    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final int EXPECTED = 5;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> filters;

    @Mock
    private UserFilter nameFilter;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUser_FollowsSelf() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID);
        });

        assertEquals("You can't subscribe or unsubscribe to yourself", exception.getMessage());
    }

    @Test
    void testFollowUser_FollowsUser() {
        subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUser_success() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(true);

        subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUser_unsuccess() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        });

        assertEquals("Subscription does not exist", exception.getMessage());
    }

    @Test
    public void testGetFollowers() {
        UserFilterDto filter = createNameFilter(NAME_ALEX);
        List<UserDto> expected = List.of(new UserDto(FOLLOWER_ID, null, null));

        User user = createUser(FOLLOWER_ID, NAME_ALEX);
        List<User> users = List.of(user);

        when(filters.iterator()).thenReturn(List.<UserFilter>of().iterator());
        when(subscriptionRepository.findByFolloweeId(FOLLOWER_ID)).thenReturn(users.stream());
        when(userMapper.toDto(user)).thenReturn(expected.get(0));

        List<UserDto> result = subscriptionService.getFollowers(FOLLOWER_ID, filter);

        verify(subscriptionRepository, times(1)).findByFolloweeId(FOLLOWER_ID);
        assertEquals(expected, result);
    }

    @Test
    public void testGetFollowing() {
        UserFilterDto filter =  createNameFilter(NAME_ALEX);
        List<UserDto> expected = List.of(new UserDto(FOLLOWER_ID, null, null));

        User user = createUser(FOLLOWER_ID, NAME_ALEX);
        List<User> users = List.of(user);

        when(filters.iterator()).thenReturn(List.<UserFilter>of().iterator());
        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID)).thenReturn(users.stream());
        when(userMapper.toDto(user)).thenReturn(expected.get(0));

        List<UserDto> result = subscriptionService.getFollowing(FOLLOWER_ID, filter);

        verify(subscriptionRepository, times(1)).findByFollowerId(FOLLOWER_ID);
        assertEquals(expected, result);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID)).thenReturn(EXPECTED);

        int result = subscriptionService.getFollowersCount(FOLLOWER_ID);

        assertEquals(EXPECTED, result);
    }

    @Test
    public void testGetFollowingCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(FOLLOWER_ID)).thenReturn(EXPECTED);

        int result = subscriptionService.getFollowingCount(FOLLOWER_ID);

        assertEquals(EXPECTED, result);
    }

    @Test
    public void testFilterUserWhenOneFilter() {
        List<User> users = createUserList(createUser(0, NAME_ALEX), createUser(1, NAME_JOHN));
        UserFilterDto filter =  createNameFilter(NAME_ALEX);

        when(filters.iterator()).thenReturn(List.of(nameFilter).iterator());
        when(nameFilter.isApplicable(filter)).thenReturn(true);
        when(nameFilter.apply(any(), eq(filter))).thenAnswer(
                inv -> ((Stream<User>) inv.getArgument(0))
                        .filter(u -> u.getUsername().equals(NAME_ALEX)));

        List<User> result = subscriptionService.filterUser(users, filter);

        verify(nameFilter, times(1)).isApplicable(filter);
        verify(nameFilter, times(1)).apply(any(), eq(filter));
        assertEquals(1, result.size());
        assertEquals(NAME_ALEX, result.get(0).getUsername());
    }

    @Test
    public void testFilterUser_whenNoFilterApplicable_returnsOriginalList() {
        List<User> users = createUserList(new User(), new User());
        UserFilterDto filterDto = createNameFilter(null);

        when(filters.iterator()).thenReturn(List.of(nameFilter).iterator());
        when(nameFilter.isApplicable(filterDto)).thenReturn(false);

        List<User> result = subscriptionService.filterUser(users, filterDto);

        verify(nameFilter, times(1)).isApplicable(filterDto);
        assertEquals(users, result);
    }

    private User createUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private UserFilterDto createNameFilter(String name) {
        return new UserFilterDto(name, null, null, null);
    }

    private List<User> createUserList(User... users) {
        return Arrays.asList(users);
    }
}
