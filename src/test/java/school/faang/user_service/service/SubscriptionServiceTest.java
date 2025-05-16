package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.UserFollowersFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    private static final Long FOLLOWER_ID = 1L;
    private static final Long FOLLOWEE_ID = 2L;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserFollowersFilter filter1;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;
    private UserFilterDto userFilterDto;
    private User userA;
    private User userB;
    private UserDto userDtoA;
    private UserDto userDtoB;

    @BeforeEach
    void setup() {
        userFilterDto = new UserFilterDto();
        userA = new User();
        userB = new User();
        subscriptionService = new SubscriptionService(
                subscriptionRepository,
                List.of(filter1),
                userMapper
        );
        userA.setUsername("User1");
        userA.setEmail("user1@email.com");
        userB.setUsername("User2");
        userB.setEmail("user2@email.com");
        userDtoA = userMapper.toDto(userA);
        userDtoB = userMapper.toDto(userB);
    }

    @Test
    @DisplayName("Follow user test")
    void followUser_whenNotFollowing() {
        // Given
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);
        // When
        subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        // Then
        verify(subscriptionRepository)
                .followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    @DisplayName("Follow user when already following")
    void followUser_whenAlreadyFollowing() {
        // Given
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);
        // When + Then
        DataValidationException ex = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID),
                "Expected exception when following twice"
        );
        assertEquals(
                "You are already followed this account!",
                ex.getMessage()
        );
        verify(subscriptionRepository, never())
                .followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    @DisplayName("Unfollow user test")
    void unfollowUser_whenFollowing() {
        // Given
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);
        // When
        subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        // Then
        verify(subscriptionRepository)
                .unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    @DisplayName("Unfollow user when not following test")
    void unfollowUser_whenNotFollowing() {
        // Given
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        // When + Then
        DataValidationException ex = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID),
                "Expected exception when following twice"
        );
        assertEquals(
                "You are not following this user!",
                ex.getMessage()
        );
        verify(subscriptionRepository, never())
                .unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    @DisplayName("Get followers when all filter pass test")
    void getFollowers_allFiltersPass_returnAllDtos() {
        // Given
        when(subscriptionRepository
                .findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(userA, userB));
        // When
        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, null);
        // Then
        assertEquals(2, result.size(), "Should return all DTOs when no filter excludes");
        assertTrue(result.containsAll(List.of(userDtoA, userDtoB)));
    }

    @Test
    @DisplayName("Get followers with filter test")
    void getFollowers_withFilters() {
        // Given
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(userA, userB));
        when(filter1.apply(userA, userFilterDto))
                .thenReturn(true);
        when(filter1.apply(userB, userFilterDto))
                .thenReturn(false);
        // When
        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, userFilterDto);
        // THen
        assertEquals(1, result.size(), "Only one follower should pass filter");
        assertEquals(userDtoA, result.get(0), "Remaining follower must be the one who passed filter");
    }

    @Test
    @DisplayName("Get followers with no filter pass test")
    void getFollowers_withNoFiltersPass() {
        // Given
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(userA, userB));
        when(filter1.apply(userA, userFilterDto))
                .thenReturn(false);
        when(filter1.apply(userB, userFilterDto))
                .thenReturn(false);
        // When
        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, userFilterDto);
        // THen
        assertEquals(0, result.size(), "No one should pass filter");
    }

    @Test
    @DisplayName("Get following when all filter pass test")
    void getFollowing_allFiltersPass_returnAllDtos() {
        // Given
        when(subscriptionRepository
                .findByFollowerId(FOLLOWEE_ID))
                .thenReturn(Stream.of(userA, userB));
        // When
        List<UserDto> result = subscriptionService.getFollowing(FOLLOWEE_ID, null);
        // Then
        assertEquals(2, result.size(), "Should return all DTOs when no filter excludes");
        assertTrue(result.containsAll(List.of(userDtoA, userDtoB)));
    }

    @Test
    @DisplayName("Get following with filter test")
    void getFollowing_withFilters() {
        // Given
        when(subscriptionRepository.findByFollowerId(FOLLOWEE_ID))
                .thenReturn(Stream.of(userA, userB));
        when(filter1.apply(userA, userFilterDto))
                .thenReturn(true);
        when(filter1.apply(userB, userFilterDto))
                .thenReturn(false);
        // When
        List<UserDto> result = subscriptionService.getFollowing(FOLLOWEE_ID, userFilterDto);
        // THen
        assertEquals(1, result.size(), "Only one following should pass filter");
        assertEquals(userDtoA, result.get(0), "Remaining following must be the one who passed filter");
    }

    @Test
    @DisplayName("Get following with no filter pass test")
    void getFollowing_withNoFiltersPass() {
        // Given
        when(subscriptionRepository.findByFollowerId(FOLLOWEE_ID))
                .thenReturn(Stream.of(userA, userB));
        when(filter1.apply(userA, userFilterDto))
                .thenReturn(false);
        when(filter1.apply(userB, userFilterDto))
                .thenReturn(false);
        // When
        List<UserDto> result = subscriptionService.getFollowing(FOLLOWEE_ID, userFilterDto);
        // THen
        assertEquals(0, result.size(), "No one should pass filter");
    }
}
