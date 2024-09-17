package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.filter.UserFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;


import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private List<UserFilter> userFilter;

    private User user1 = new User();
    private User user2 = new User();


    @BeforeEach
    public void setUp() {
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.ru");

        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.ru");
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

        assertDoesNotThrow(() -> subscriptionRepository.unfollowUser(followerId, followeeId));
    }

    @Test
    public void testGetFollowersWithNoFilter() {
        when(subscriptionRepository.findByFolloweeId(1L)).thenReturn(Stream.of(user1, user2));
        UserDto userDto1 = new UserDto(1L, "user1","user1@example.ru");
        UserDto userDto2 = new UserDto(2L, "user2","user2@example.ru");
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);
        List<UserDto> followers = subscriptionService.getFollowers(1L, new UserFilterDto());

        assertEquals(2, followers.size());
        assertEquals(1L, followers.get(0).id());
        assertEquals(2L, followers.get(1).id());

    }

    @Test
    public void testGetFollowersWithEmptyResult() {
        long followeeId = 1L;
        UserFilterDto filters = new UserFilterDto();
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());
        List<UserDto> result = subscriptionService.getFollowers(followeeId, filters);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFollowersNoMatchingFilters() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user1));
        when(userMapper.toUserDto(user1))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    public void testGetFollowersWithMatchingFilters() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("username");
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user1));
        when(userMapper.toUserDto(user1))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    public void testOfGetFollowersCount() {
        long followeeId = 1L;

        assertDoesNotThrow(() -> userMapper.toCountDto(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)));
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;

        assertDoesNotThrow(() -> userMapper.toCountDto(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)));
    }

    @Test
    public void testGetFollowing() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(Stream.of(user1));
        when(userMapper.toUserDto(user1)).thenReturn(userDto);
        when(userFilter.stream()).thenReturn(Stream.empty());

        List<UserDto> result = subscriptionService.getFollowing(followeeId, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }
}
