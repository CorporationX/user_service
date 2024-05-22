package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.TestUser.FOLLOWEE_ID;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserFilterDto userFilterDto;
    @Mock
    private List<UserFilter> userFilters;

    @Test
    public void testFollowUserToAnotherUser() {
        subscriptionRepository.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUserToAnotherUser() {
        subscriptionRepository.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowersCount() {
        subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(FOLLOWER_ID);

        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID))
                .thenReturn(1);
        int count = subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID);

        assertEquals(1, count);
    }

    @Test
    public void testGetFollowers() {
        User user = mock(User.class);
        Stream<User> userStream = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(FOLLOWER_ID)).thenReturn(userStream);

        subscriptionService.getFollowers(FOLLOWER_ID, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(FOLLOWER_ID);
    }
}