package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private long followerId;
    private long followeeId;
    private User user1;
    private User user2;
    private List<User> users;
    private List<UserSubResponseDto> userSubResponseDtos;
    private UserFilterDto filterDto;
    private UserFilter mockFilter;

    @BeforeEach
    public void setUp() {
        followerId = 1L;
        followeeId = 2L;
        filterDto = UserFilterDto.builder().emailPattern("example.com").build();
        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        user1.setId(1L);
        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        user2.setId(2L);
        users = List.of(user1, user2);
        userSubResponseDtos = List.of(
                UserSubResponseDto.builder().id(1L).username("user1").email("user1@example.com").build(),
                UserSubResponseDto.builder().id(2L).username("user2").email("user2@example.com").build()
        );
        mockFilter = mock(UserFilter.class);
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, List.of(mockFilter));
    }

    @Test
    public void testIfThrowsExceptionWhenUserIsAlreadyFollowingTheUser() {
        // arrange
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false)
                .thenReturn(true);

        // act, assert, verify
        subscriptionService.followUser(followerId, followeeId);
        assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testFollowUser() {
        // arrange
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        // act
        subscriptionService.followUser(followerId, followeeId);

        // assert
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testIfThrowsExceptionWhenUserIsNotFollowingTheUser() {
        // arrange
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true)
                .thenReturn(false);

        // act, assert, verify
        subscriptionService.unfollowUser(followerId, followeeId);
        assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(followerId, followeeId));

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser() {
        // arrange
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        // act
        subscriptionService.unfollowUser(followerId, followeeId);

        // assert
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        // Arrange
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        when(userMapper.toUserSubResponseList(anyList())).thenReturn(userSubResponseDtos);
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(User.class))).thenReturn(true);

        // Act
        List<UserSubResponseDto> result = subscriptionService.getFollowers(followeeId, filterDto);

        // Assert
        assertEquals(userSubResponseDtos, result);
        verify(subscriptionRepository).findByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowingCount() {
        // arrange
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(2);

        // act
        int followingCount = subscriptionService.getFollowingCount(followeeId);

        // assert
        assertEquals(2, followingCount);
    }

    @Test
    public void testGetFollowing() {
        // Arrange
        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(users.stream());
        when(userMapper.toUserSubResponseList(anyList())).thenReturn(userSubResponseDtos);
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(User.class))).thenReturn(true);

        // Act
        List<UserSubResponseDto> result = subscriptionService.getFollowing(followerId, filterDto);

        // Assert
        assertEquals(userSubResponseDtos, result);
        verify(subscriptionRepository).findByFollowerId(followerId);
    }

    @Test
    public void testGetFollowersCount() {
        // arrange
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(2);

        // act
        int followersCount = subscriptionService.getFollowersCount(followerId);

        // assert
        assertEquals(2, followersCount);
    }
}