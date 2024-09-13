package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.constant.ErrorMessages;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserFilter userFilter;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private long followerId;
    private long followeeId;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private UserFilterDto userFilterDto;

    private List<User> users;

    private String email;

    @BeforeEach
    public void setUp() {
        followeeId = 1L;
        followerId = 2L;

        email = "john@test.com";

        User user1 = new User();
        user1.setId(followeeId);
        user1.setUsername("John");
        user1.setEmail(email);

        User user2 = new User();
        user2.setId(followeeId);
        user2.setUsername("Dan");
        user2.setEmail("dan@test.com");

        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("Tolik");
        user3.setEmail("tolik@test.com");

        users = List.of(user1, user2, user3);

        userDto1 = new UserDto();
        userDto1.setId(followerId);
        userDto1.setUsername(user1.getUsername());
        userDto1.setEmail(email);

        userDto2 = new UserDto();
        userDto2.setId(user1.getId());
        userDto2.setUsername(user2.getUsername());
        userDto2.setEmail(user1.getEmail());

        userDto3 = new UserDto();
        userDto3.setId(user3.getId());
        userDto3.setUsername(user3.getUsername());
        userDto3.setEmail(user1.getEmail());

        userFilterDto = new UserFilterDto();
    }

    @Test
    public void testFollowUser_successSubscribe() {

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testFollowUser_failSubscribeUserTriedToSubscribeToSelf() {
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followerId)
        );

        assertTrue(ErrorMessages.CANNOT_SUBSCRIBE_OR_UNSUBSCRIBE_TO_SELF.equals(exception.getMessage()));
        verify(subscriptionRepository, never()).followUser(followerId, followeeId);
    }

    @Test
    public void testFollowUser_failSubscribeTriedToSubscribeTwiceToUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId)
        );

        assertTrue(ErrorMessages.ALREADY_SUBSCRIBE.equals(exception.getMessage()));
        verify(subscriptionRepository, never()).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser_successUnsubscribe() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser_failUnsubscribeUserTriedToUnsubscribeToSelf() {
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(followerId, followerId)
        );

        assertTrue(ErrorMessages.CANNOT_SUBSCRIBE_OR_UNSUBSCRIBE_TO_SELF.equals(exception.getMessage()));
        verify(subscriptionRepository, never()).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers_returnFilteredUserDto() {
        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(users.stream());

        List<User> expected = List.of(users.get(0), users.get(1), users.get(2));
        List<User> actual = subscriptionService.getFollowers(followeeId, userFilterDto);

        assertEquals(expected, actual);

        verify(subscriptionRepository).findByFolloweeId(followeeId);
    }

    @Test
    void testApplyPagination_paginationCorrectBehavior() {
        int page = 1;
        int pageSize = 3;

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPage(page);
        filterDto.setPageSize(pageSize);

        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        User user5 = new User();
        User user6 = new User();

        Stream<User> userStream = Stream.of(user1, user2, user3, user4, user5, user6);
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(userStream);

        List<User> result = subscriptionService.getFollowers(followeeId, filterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        assertEquals(pageSize, result.size());
    }

    @Test
    void testApplyPagination_paginationNotCorrectBehavior() {
        int page = 1;
        int pageSize = 3;
        int notCorrectPageSize = 4;

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPage(page);
        filterDto.setPageSize(pageSize);

        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        User user5 = new User();
        User user6 = new User();

        Stream<User> userStream = Stream.of(user1, user2, user3, user4, user5, user6);
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(userStream);

        List<User> result = subscriptionService.getFollowers(followeeId, filterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        assertNotEquals(notCorrectPageSize, result.size());
    }

    @Test
    void testFilterUsers() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setEmailPattern(email);
        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(users.stream());

        when(userFilter.isApplicable(filterDto)).thenReturn(true);
        when(userFilter.getPredicate(filterDto)).thenReturn(user -> user.getEmail().contains(email));

        userFilters = List.of(userFilter);
        subscriptionService = new SubscriptionService(subscriptionRepository, userFilters);

        List<User> result = subscriptionService.getFollowers(followeeId, filterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        verify(userFilter, times(1)).isApplicable(filterDto);
        verify(userFilter, times(1)).getPredicate(filterDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFollowersCount() {
        int followersAmount = 3;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(followersAmount);
        int result = subscriptionService.getFollowersCount(followeeId);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);

        assertEquals(followersAmount, result);
    }

    @Test
    void testGetFollowingCount() {
        int followeesAmount = 3;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(followeesAmount);
        int result = subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);

        assertEquals(followeesAmount, result);
    }


    @Test
    public void testGetFollowing_returnFilteredUsers() {
        when(subscriptionRepository.findByFollowerId(followerId))
                .thenReturn(users.stream());

        List<User> expected = List.of(users.get(0), users.get(1), users.get(2));
        List<User> actual = subscriptionService.getFollowing(followerId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFollowerId(followerId);

        assertEquals(expected, actual);
    }

}
