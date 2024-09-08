package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.filter.NameFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Mock
    private NameFilter nameFilter;

    private User user1 = new User();
    private User user2 = new User();

    private void createUser() {
        user1.setId(1L);
        user1.setUsername("Ruslan");
        user1.setEmail("ruslan@example.ru");

        user2.setId(2L);
        user2.setUsername("Bob");
        user2.setEmail("bob@example.ru");
    }


    @Test
    public void testExistingFollowUser() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUserBySubscriptionRepository() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);
    }

    @Test
    public void testUnFollowUser() {
        long followerId = 1L;
        long followeeId = 2L;

        subscriptionService.unFollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowersWithNoFilter() {
        createUser();

        when(subscriptionRepository.findByFolloweeId(1L)).thenReturn(Stream.of(user1, user2));
        List<UserDto> followers = subscriptionService.getFollowers(1L, new UserFilterDto());

        assertEquals(2, followers.size());
        assertEquals("Ruslan", followers.get(0).getUsername());
        assertEquals("Bob", followers.get(1).getUsername());
    }

    @Test
    public void testGetFollowersNoMatchesAfterFilters() {
        createUser();

        when(subscriptionRepository.findByFolloweeId(1L)).thenReturn(Stream.of(user1, user2));
        when(nameFilter.isApplicable(any())).thenReturn(true);
        when(nameFilter.apply(any(), any())).thenAnswer(invocation -> {
            Stream<User> inputUsers = invocation.getArgument(0);
            return inputUsers.filter(user -> user.getUsername().contains("Charlie"));
        });
        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("Charlie");
        List<UserDto> result = subscriptionService.getFollowers(1L, filters);

        assertEquals(0, result.size());
    }

    @Test
    public void testGetFollowerWithNoMatches() {
        createUser();

        when(subscriptionRepository.findByFolloweeId(1L)).thenReturn(Stream.of(user1, user2));
        UserFilterDto filter = new UserFilterDto();
        filter.setEmailPattern("charlie@example.ru");
        List<UserDto> followers = subscriptionService.getFollowers(1L, filter);

        assertEquals(0, followers.size());
    }

    @Test
    public void testOfGetFollowersCount() {
        long followeeId = 1L;

        subscriptionService.getFollowersCount(followeeId);
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;

        subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
