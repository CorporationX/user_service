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

        verify(subscriptionRepository, timeout(1000)).followUser(followerId, followeeId);
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

        verify(subscriptionRepository, timeout(1000)).unfollowUser(followerId, followeeId);
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
    public void testGetFollowersWithNameFilter_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter));

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(sampleUsers());
        when(userNameFilter.isApplicable(any())).thenReturn(true);
        setUserNameFilter();

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto("Oleg", null, 0, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFollowersWithPhoneFilter_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userPhoneFilter));

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(sampleUsers());
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        setUserPhoneFilter();

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto(null, "000000000", 0, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFollowersWithExperienceFilter_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userExperienceFilter));

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(sampleUsers());
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);
        setUserExperienceFilter();

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto(null, null, 0, 1));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFollowersWithFilters_GetOne() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userPhoneFilter, userExperienceFilter));

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(sampleUsers());
        mockFilters_IsApplicable();
        setUserNameFilter();
        setUserPhoneFilter();
        setUserExperienceFilter();

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto("Gleb", "123456789", 0, 2));

        assertEquals(1, result.size());

        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(2, result.get(0).experience());
    }

    @Test
    public void testGetFollowersWithoutPhoneFilter_GetAll() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userExperienceFilter));

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(sampleUsers());
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);
        setUserExperienceFilter();

        List<UserDto> result = service
                .getFollowers(followeeId, new UserFiltersDto(null, null, 0, 5));

        assertEquals(4, result.size());

        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(2, result.get(0).experience());

        assertEquals("gleb", result.get(1).username());
        assertEquals("123456789", result.get(1).phone());
        assertEquals(3, result.get(1).experience());

        assertEquals("GleB", result.get(2).username());
        assertEquals("222222222", result.get(2).phone());
        assertEquals(4, result.get(2).experience());

        assertEquals("Max", result.get(3).username());
        assertEquals("222222222", result.get(3).phone());
        assertEquals(5, result.get(3).experience());
    }

    // -------------------------------------------------
    // getFollowees()
    // -------------------------------------------------
    @Test
    public void testGetFolloweesWithNameFilter_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter));

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(sampleUsers());
        when(userNameFilter.isApplicable(any())).thenReturn(true);
        setUserNameFilter();

        List<UserDto> result = service
                .getFollowers(followerId, new UserFiltersDto("Oleg", null, 0, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFolloweesWithPhoneFilter_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userPhoneFilter));

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(sampleUsers());
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        setUserPhoneFilter();

        List<UserDto> result = service
                .getFollowers(followerId, new UserFiltersDto(null, "000000000", 0, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFolloweesWithExperienceFilter_GetNone() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userExperienceFilter));

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(sampleUsers());
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);
        setUserExperienceFilter();

        List<UserDto> result = service
                .getFollowers(followerId, new UserFiltersDto(null, null, 0, 1));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFolloweesWithFilters_GetOne() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userPhoneFilter, userExperienceFilter));

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(sampleUsers());
        mockFilters_IsApplicable();
        setUserNameFilter();
        setUserPhoneFilter();
        setUserExperienceFilter();

        List<UserDto> result = service
                .getFollowers(followerId, new UserFiltersDto("Gleb", "123456789", 0, 2));

        assertEquals(1, result.size());

        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(2, result.get(0).experience());
    }

    @Test
    public void testGetFolloweesWithoutPhoneFilter_GetAll() {
        service = new UserSubscriptionServiceImpl(subscriptionRepository, userMapper, List.of(
                userNameFilter, userExperienceFilter));

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(sampleUsers());
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);
        setUserExperienceFilter();

        List<UserDto> result = service
                .getFollowers(followerId, new UserFiltersDto(null, null, 0, 5));

        assertEquals(4, result.size());

        assertEquals("GLEB", result.get(0).username());
        assertEquals("123456789", result.get(0).phone());
        assertEquals(2, result.get(0).experience());

        assertEquals("gleb", result.get(1).username());
        assertEquals("123456789", result.get(1).phone());
        assertEquals(3, result.get(1).experience());

        assertEquals("GleB", result.get(2).username());
        assertEquals("222222222", result.get(2).phone());
        assertEquals(4, result.get(2).experience());

        assertEquals("Max", result.get(3).username());
        assertEquals("222222222", result.get(3).phone());
        assertEquals(5, result.get(3).experience());
    }

    // -------------------------------------------------
    // Additional methods
    // -------------------------------------------------
    private Stream<User> sampleUsers() {
        return Stream.of(
                user("GLEB", "123456789", 2),
                user("gleb", "123456789", 3),
                user("GleB", "222222222", 4),
                user("Max", "222222222", 5)
        );
    }

    private User user(String username, String phone, int experience) {
        return User.builder()
                .username(username)
                .phone(phone)
                .experience(experience)
                .build();
    }

    private void mockFilters_IsApplicable() {
        when(userNameFilter.isApplicable(any())).thenReturn(true);
        when(userPhoneFilter.isApplicable(any())).thenReturn(true);
        when(userExperienceFilter.isApplicable(any())).thenReturn(true);
    }

    private void setUserNameFilter() {
        when(userNameFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            return stream.filter(user -> user.getUsername().equalsIgnoreCase(filters.namePattern()));
        });
    }

    private void setUserPhoneFilter() {
        when(userPhoneFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            return stream.filter(user -> user.getPhone().equalsIgnoreCase(filters.phonePattern()));
        });
    }

    private void setUserExperienceFilter() {
        when(userExperienceFilter.apply(any(), any())).thenAnswer((Answer<Stream<User>>) invocation -> {
            Stream<User> stream = invocation.getArgument(0);
            UserFiltersDto filters = invocation.getArgument(1);
            return stream.filter(user -> user.getExperience() >= filters.experienceMin()
                    && user.getExperience() <= filters.experienceMax());
        });
    }
}