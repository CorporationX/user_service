package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.service.filters.user.UserCountryFilter;
import school.faang.user_service.service.filters.user.UserEmailFilter;
import school.faang.user_service.service.filters.user.UserFilter;
import school.faang.user_service.service.filters.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    private SubscriptionService subscriptionService;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    private User user1;
    private User user2;

    private List<User> users;
    private List<UserDto> usersDto;
    private List<UserFilter> userFilters;

    @BeforeEach
    public void setUp() {
        this.userFilters = new ArrayList<>();
        userFilters.add(new UserEmailFilter());
        userFilters.add(new UserNameFilter());
        userFilters.add(new UserCountryFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters);
        user1 = User.builder()
                .id(1L)
                .username("46")
                .country(new Country(1, "Russia", new ArrayList<>()))
                .email("46@gmail.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .username("Angie")
                .country(new Country(2, "New Zealand", new ArrayList<>()))
                .email("Angie@gmail.com")
                .build();
        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .username("46")
                .email("46@gmail.com")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("Angie")
                .email("Angie@gmail.com")
                .build();
        usersDto = new ArrayList<>();
        usersDto.add(userDto1);
        usersDto.add(userDto2);
    }

    @Test
    public void subscribeWhen_SubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(user1.getId(), user2.getId()));
    }

    @Test
    public void userFollowedSuccess() {
        subscriptionService.followUser(user1.getId(), user2.getId());
        verify(subscriptionRepository).followUser(user1.getId(), user2.getId());
    }

    @Test
    public void unsubscribeWhen_SubscriptionDoesNotExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId()))
                .thenReturn(false);
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(user1.getId(), user2.getId()));
    }

    @Test
    public void userUnfollowedSuccess() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId()))
                .thenReturn(true);
        subscriptionService.unfollowUser(user1.getId(), user2.getId());
        verify(subscriptionRepository).unfollowUser(user1.getId(), user2.getId());
    }


    @Test
    public void getFollowers_WithFilters(){
        long userId = 3L;
        UserFilterDto filterDto = UserFilterDto.builder()
                .namePattern("46")
                .build();
        Stream<User> userStream = users.stream();
        List<UserDto> expectedResult = usersDto.stream()
                .filter(user -> user.getUsername().equals("46"))
                .toList();

        when(subscriptionRepository.findByFolloweeId(userId)).thenReturn(userStream);
        List<UserDto> result = subscriptionService.getFollowers(userId, filterDto);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getFollowers_NoFilters(){
        List<UserDto> expectedResult = users.stream().map(userMapper::toDto).toList();
        UserFilterDto filterDto = null;

        when(subscriptionRepository.findByFolloweeId(3L)).thenReturn(users.stream());
        List<UserDto> result = subscriptionService.getFollowers(3L, filterDto);

        assertEquals(expectedResult, result);
    }

    @Test
    public void followersCount(){
        subscriptionService.getFollowersCount(3L);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(3L);
    }

    @Test
    public void getFollowings_NoFilter(){
        List<UserDto> expectedResult = users.stream().map(userMapper::toDto).toList();
        UserFilterDto filterDto = null;

        when(subscriptionRepository.findByFollowerId(3L)).thenReturn(users.stream());
        List<UserDto> result = subscriptionService.getFollowing(3l, filterDto);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getFollowings_WithFilters(){
        long userId = 3L;
        UserFilterDto filterDto = UserFilterDto.builder()
                .countryPattern("Russia")
                .build();

        Stream<User> userStream = users.stream();
        List<UserDto> expectedResult = usersDto.stream().filter(user -> user.getUsername().equals("46")).toList();

        when(subscriptionRepository.findByFollowerId(userId)).thenReturn(userStream);
        List<UserDto> result = subscriptionService.getFollowing(userId, filterDto);

        assertEquals(expectedResult, result);
    }

    @Test
    public void followingCount(){
        subscriptionService.getFollowingCount(3L);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }
}
