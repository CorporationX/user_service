package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserFilter userNameFilter;

    @Mock
    private UserFilter userPhoneFilter;

    @Mock
    private UserFilter userExperienceFilter;

    @InjectMocks
    private UserSubscriptionServiceImpl service;

    private long followerId;
    private long followeeId;

    @BeforeEach
    void setUp() {
        followerId = 1L;
        followeeId = 2L;
    }

    // -------------------------------------------------
    // followUser()
    // -------------------------------------------------
    @Test
    public void testSuccessfulUserFollow() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        service.followUser(followerId, followeeId);

        verify(subscriptionRepository, timeout(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testFollowAlreadySubscribedUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> service.followUser(followerId, followeeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    // -------------------------------------------------
    // unfollowUser()
    // -------------------------------------------------
    @Test
    public void testSuccessfulUserUnfollow() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        service.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, timeout(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowNotSubscribedUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> service.unfollowUser(followerId, followeeId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    // -------------------------------------------------
    // getFollowersCount()
    // -------------------------------------------------
    @Test
    public void testGetCorrectFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(7);

        assertEquals(7, service.getFollowersCount(followeeId).count());
    }

    // -------------------------------------------------
    // getFolloweesCount()
    // -------------------------------------------------
    @Test
    public void testGetCorrectFolloweesCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(7);

        assertEquals(7, service.getFolloweesCount(followerId).count());
    }

    // -------------------------------------------------
    // getFollowers()
    // -------------------------------------------------
    @Test
    public void testGetFollowersWithFilters_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userPhoneFilter, userExperienceFilter));

        User user1 = User.builder()
                .username("GLEB")
                .phone("123456789")
                .experience(3)
                .build();
        User user2 = User.builder()
                .username("Gleb")
                .phone("222222222")
                .experience(8)
                .build();
        User user3 = User.builder()
                .username("Max")
                .phone("222222222")
                .experience(5)
                .build();
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user1, user2, user3));

        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);

        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase("Gleb"));
        });
        when(userPhoneFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getPhone().equals("123456789"));
        });
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            int min = filters.experienceMin();
            int max = filters.experienceMax();
            return stream.filter(user -> user.getExperience() >= min && user.getExperience() <= max);
        });

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto(null, null, 1, 2));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFollowersWithFilters_GetOne() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userPhoneFilter, userExperienceFilter));

        User user1 = User.builder()
                .username("GLEB")
                .phone("123456789")
                .experience(3)
                .build();
        User user2 = User.builder()
                .username("Gleb")
                .phone("222222222")
                .experience(8)
                .build();
        User user3 = User.builder()
                .username("Max")
                .phone("222222222")
                .experience(5)
                .build();
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user1, user2, user3));

        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);

        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase("Gleb"));
        });
        when(userPhoneFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getPhone().equals("123456789"));
        });
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            int min = filters.experienceMin();
            int max = filters.experienceMax();
            return stream.filter(user -> user.getExperience() >= min && user.getExperience() <= max);
        });

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto(null, null, 1, 3));

        assertEquals(1, result.size());
        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(3, result.get(0).experience());
    }

    @Test
    public void testGetFollowersWithoutPhoneFilter_GetAll() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userExperienceFilter));

        User user1 = User.builder()
                .username("GLEB")
                .phone("123456789")
                .experience(3)
                .build();
        User user2 = User.builder()
                .username("Gleb")
                .phone("222222222")
                .experience(1)
                .build();
        User user3 = User.builder()
                .username("GlEb")
                .phone("222222222")
                .experience(5)
                .build();
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user1, user2, user3));

        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);

        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase("Gleb"));
        });
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            int min = filters.experienceMin();
            int max = filters.experienceMax();
            return stream.filter(user -> user.getExperience() >= min && user.getExperience() <= max);
        });

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto(null, null, 1, 5));

        assertEquals(3, result.size());
        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(3, result.get(0).experience());
        assertEquals("Gleb", result.get(1).username());
        assertEquals("222222222", result.get(1).phone());
        assertEquals(1, result.get(1).experience());
    }

    // -------------------------------------------------
    // getFollowees()
    // -------------------------------------------------
    @Test
    public void testGetFolloweesWithFilters_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userPhoneFilter, userExperienceFilter));

        User user1 = User.builder()
                .username("GLEB")
                .phone("123456789")
                .experience(3)
                .build();
        User user2 = User.builder()
                .username("Gleb")
                .phone("222222222")
                .experience(8)
                .build();
        User user3 = User.builder()
                .username("Max")
                .phone("222222222")
                .experience(5)
                .build();
        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(user1, user2, user3));

        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);

        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase("Gleb"));
        });
        when(userPhoneFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getPhone().equals("123456789"));
        });
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            int min = filters.experienceMin();
            int max = filters.experienceMax();
            return stream.filter(user -> user.getExperience() >= min && user.getExperience() <= max);
        });

        List<UserDto> result = service
                .getFollowees(followerId, new UserFiltersDto(null, null, 1, 2));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFolloweesWithFilters_GetOne() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userPhoneFilter, userExperienceFilter));

        User user1 = User.builder()
                .username("GLEB")
                .phone("123456789")
                .experience(3)
                .build();
        User user2 = User.builder()
                .username("Gleb")
                .phone("222222222")
                .experience(8)
                .build();
        User user3 = User.builder()
                .username("Max")
                .phone("222222222")
                .experience(5)
                .build();
        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(user1, user2, user3));

        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);

        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase("Gleb"));
        });
        when(userPhoneFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getPhone().equals("123456789"));
        });
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            int min = filters.experienceMin();
            int max = filters.experienceMax();
            return stream.filter(user -> user.getExperience() >= min && user.getExperience() <= max);
        });

        List<UserDto> result = service
                .getFollowees(followerId, new UserFiltersDto(null, null, 1, 3));

        assertEquals(1, result.size());
        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(3, result.get(0).experience());
    }

    @Test
    public void testGetFolloweesWithoutPhoneFilter_GetAll() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userExperienceFilter));

        User user1 = User.builder()
                .username("GLEB")
                .phone("123456789")
                .experience(3)
                .build();
        User user2 = User.builder()
                .username("Gleb")
                .phone("222222222")
                .experience(1)
                .build();
        User user3 = User.builder()
                .username("GlEb")
                .phone("222222222")
                .experience(5)
                .build();
        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(user1, user2, user3));

        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);

        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase("Gleb"));
        });
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            int min = filters.experienceMin();
            int max = filters.experienceMax();
            return stream.filter(user -> user.getExperience() >= min && user.getExperience() <= max);
        });

        List<UserDto> result = service
                .getFollowees(followerId, new UserFiltersDto(null, null, 1, 5));

        assertEquals(3, result.size());
        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(3, result.get(0).experience());
        assertEquals("Gleb", result.get(1).username());
        assertEquals("222222222", result.get(1).phone());
        assertEquals(1, result.get(1).experience());
    }

}