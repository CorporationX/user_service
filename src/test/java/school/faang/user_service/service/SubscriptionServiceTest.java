package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    private final UserMapper userMapper = new UserMapperImpl();

    private final List<UserFilter> userFilters = List.of(new UserNameFilter(), new UserEmailFilter());

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters, subscriptionValidator);
    }


    @Test
    void followUser_ShouldCallValidatorAndRepositoryMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionValidator, times(1)).validateSubscription(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUser_ShouldCallValidatorAndRepositoryMethod() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionValidator, times(1)).validateUnsubscription(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowers_ShouldApplyFiltersAndReturnUserDtoList() {
        long followeeId = 1;
        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("Ivan");
        filters.setEmailPattern("ivan@example.com");

        User user1 = new User();
        user1.setUsername("Ivan");
        user1.setEmail("ivan@example.com");
        User user2 = new User();
        user2.setUsername("Ivan");
        user2.setEmail("ivan@example.com");
        User user3 = new User();
        user3.setUsername("Anna");
        user3.setEmail("ivan@example.com");
        List<User> followers = List.of(user1, user2, user3);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(followers.stream());

        List<UserDto> actualDtos = subscriptionService.getFollowers(followeeId, filters);

        assertEquals(2, actualDtos.size());
        assertEquals("Ivan", actualDtos.get(0).getUsername());
        assertEquals("Ivan", actualDtos.get(1).getUsername());
        assertEquals("ivan@example.com", actualDtos.get(0).getEmail());
        assertEquals("ivan@example.com", actualDtos.get(1).getEmail());
    }
}

