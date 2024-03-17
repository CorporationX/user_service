package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filters.CityPatternFilter;
import school.faang.user_service.service.filters.CountryPatternFilter;
import school.faang.user_service.service.filters.ExperienceMaxFilter;
import school.faang.user_service.service.filters.NamePatternFilter;
import school.faang.user_service.service.filters.UserFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static Long userId1;
    private static Long userId2;
    private final List<UserFilter> userFilters = new ArrayList<>();

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private SubscriptionService subscriptionService;


    @BeforeEach
    public void initialize() {
        userId1 = 1000L;
        userId2 = 2000L;
        userFilters.add(new CityPatternFilter());
        userFilters.add(new CountryPatternFilter());
        userFilters.add(new NamePatternFilter());
        userFilters.add(new ExperienceMaxFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters);

    }
    @Test
    public void testFollowUserThrowsExceptionWhenFollowsItself() {
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(userId1, userId1));
    }

    @Test
    public void testFollowUserThrowsExceptionWhenSubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(userId1, userId2)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(userId1, userId2));
    }

    @Test
    public void testFollowUser() {
        subscriptionService.followUser(userId1, userId2);
        verify(subscriptionRepository, times(1)).followUser(userId1, userId2);
    }

    @Test
    public void testUnfollowUserThrowsExceptionWhenUnfollowYourself() {
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(userId2, userId2));
    }
    @Test
    public void testUnfollowUser() {
        subscriptionService.unfollowUser(userId1, userId2);
        verify(subscriptionRepository, times(1)).unfollowUser(userId1, userId2);
    }

    @Test
    public void testGetFollowers() {
        User user1 = new User();
        user1.setId(userId1);
        user1.setFollowers(List.of(User.builder().id(userId2).build()));
        when(subscriptionRepository.findByFolloweeId(userId1)).thenReturn(user1.getFollowers().stream());
        List<UserDto> result = subscriptionService.getFollowers(userId1, new UserFilterDto());
        assertEquals(result.get(0).getId(), userId2);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(userId1)).thenReturn(100);
        subscriptionService.getFollowersCount(userId1);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(userId1);
        assertEquals(100, subscriptionService.getFollowersCount(userId1));
    }

    @Test
    public void testGetFollowing() {
        User user1 = new User();
        user1.setId(userId1);
        user1.setFollowees(List.of(User.builder().id(userId2).build()));
        when(subscriptionRepository.findByFolloweeId(userId1)).thenReturn(user1.getFollowees().stream());
        List<UserDto> result = subscriptionService.getFollowing(userId1, new UserFilterDto());
        assertEquals(result.get(0).getId(), userId2);

    }

    @Test
    public void testGetFollowingCount() {
        subscriptionService.getFollowingCount(userId1);
        when(subscriptionRepository.findFolloweesAmountByFollowerId(userId1)).thenReturn(500);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(userId1);
        assertEquals(500, subscriptionService.getFollowingCount(userId1));
    }
}
