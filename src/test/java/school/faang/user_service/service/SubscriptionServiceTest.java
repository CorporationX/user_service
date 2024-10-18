package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.enums.PreferredContact;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.publisher.UserFollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.impl.PaginationServiceImpl;
import school.faang.user_service.service.impl.SubscriptionServiceImpl;
import school.faang.user_service.service.impl.UserFilterService;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionServiceValidator subscriptionServiceValidator;

    @Mock
    private PaginationServiceImpl paginationService;

    @Mock
    private UserFilterDto filter;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserFilterService userFilterService;

    @Mock
    private UserFollowerEventPublisher userFollowerEventPublisher;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@example.com";
    private static final String PHONE = "79998887766";
    private static final String FOLLOWED_USERNAME = "followedUser";
    private static final String FOLLOWED_EMAIL = "followed@example.com";

    private User createTestUser(long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    @Test
    void testFollowUser_Success() {
        doNothing().when(subscriptionServiceValidator).validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID);
        doNothing().when(subscriptionServiceValidator).checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID);
        doNothing().when(subscriptionRepository).followUser(FOLLOWER_ID, FOLLOWEE_ID);

        subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionServiceValidator, times(1)).validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionServiceValidator, times(1)).checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testUnfollowUser_Success() {
        doNothing().when(subscriptionServiceValidator).validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID);
        doNothing().when(subscriptionServiceValidator).checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID);
        doNothing().when(subscriptionRepository).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionServiceValidator, times(1)).validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionServiceValidator, times(1)).checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testFollowUser_Fail_WhenAlreadySubscribed() {
        doNothing().when(subscriptionServiceValidator).validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID);
        doThrow(new DataValidationException("You are already subscribed")).when(subscriptionServiceValidator).checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID));

        verify(subscriptionServiceValidator, times(1)).validateFollowIds(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionServiceValidator, times(1)).checkIfAlreadySubscribed(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository, never()).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testGetFollowers_Success() {
        User user = createTestUser(FOLLOWEE_ID, USERNAME, EMAIL);
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(new UserDto
                (FOLLOWEE_ID, true, EMAIL, "@User", USERNAME, "123456789", PreferredContact.EMAIL));

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(users.stream());
        when(paginationService.applyPagination(users, filter.getPage(), filter.getPageSize())).thenReturn(users);
        when(userFilterService.filterUsers(users, filter)).thenReturn(users);
        when(userMapper.toListUserDto(users)).thenReturn(userDtos);

        List<UserDto> followers = subscriptionService.getFollowers(FOLLOWEE_ID, filter);

        assertEquals(1, followers.size());
        assertEquals(USERNAME, followers.get(0).getUsername());
        assertEquals(EMAIL, followers.get(0).getEmail());
    }

    @Test
    void testGetFollowersCount_Success() {
        long expectedCount = 5L;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWEE_ID)).thenReturn((int) expectedCount);

        long result = subscriptionService.getFollowersCount(FOLLOWEE_ID);

        assertEquals(expectedCount, result);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(FOLLOWEE_ID);
    }

    @Test
    void testGetFollowing_Success() {
        User user = createTestUser(FOLLOWEE_ID, FOLLOWED_USERNAME, FOLLOWED_EMAIL);
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(new UserDto
                (FOLLOWEE_ID, true, FOLLOWED_EMAIL, "@User", FOLLOWED_USERNAME, "123456789", PreferredContact.EMAIL));

        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID)).thenReturn(users.stream());
        when(paginationService.applyPagination(users, filter.getPage(), filter.getPageSize())).thenReturn(users);
        when(userFilterService.filterUsers(users, filter)).thenReturn(users);
        when(userMapper.toListUserDto(users)).thenReturn(userDtos);

        List<UserDto> following = subscriptionService.getFollowing(FOLLOWER_ID, filter);

        assertEquals(1, following.size());
        assertEquals(FOLLOWED_USERNAME, following.get(0).getUsername());
        assertEquals(FOLLOWED_EMAIL, following.get(0).getEmail());
    }

    @Test
    void testGetFollowingCount_Success() {
        long expectedCount = 5L;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(FOLLOWER_ID)).thenReturn((int) expectedCount);

        long result = subscriptionService.getFollowingCount(FOLLOWER_ID);

        assertEquals(expectedCount, result);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(FOLLOWER_ID);
    }
}
