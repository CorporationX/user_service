package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionDto;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserSubscriptionFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserSubscriptionFilter userSubscriptionFilter;

    public void setup() {
        when(userSubscriptionFilter.isApplication(any(UserSubscriptionFilterDto.class))).thenReturn(true);
        when(userSubscriptionFilter.apply(any(Stream.class), any(UserSubscriptionFilterDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, List.of(userSubscriptionFilter));
    }

    @Test
    public void testFollowUsersWithExistingSubscription() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(1L, 2L));

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(1L, 2L);
        verify(subscriptionRepository, never()).followUser(1L, 2L);
    }

    @Test
    public void testFollowUsersSuccess() throws DataValidationException {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);
        subscriptionService.followUser(1L, 2L);

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(1L, 2L);
        verify(subscriptionRepository, times(1)).followUser(1L, 2L);
    }

    @Test
    public void testUnfollowUser() {
        long followerId = 1L;
        long followeeId = 2L;
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        setup();
        long followeeId = 1L;
        User user = createUser();
        List<User> mockUsers = List.of(user);
        UserSubscriptionDto userDto = new UserSubscriptionDto();
        userDto.setUsername("testUser");
        List<UserSubscriptionDto> mockUserDtos = List.of(userDto);
        UserSubscriptionFilterDto filter = createFilter();

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(mockUsers.stream());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        List<UserSubscriptionDto> result = subscriptionService.getFollowers(followeeId, filter);

        verify(subscriptionRepository).findByFolloweeId(followeeId);
        verify(userMapper, times(mockUsers.size())).toDto(any(User.class));
        assertEquals(mockUserDtos, result);
    }

    @Test
    public void testGetFollowersCount() {
        long followeeId = 1L;
        long expectedCount = 100L;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn((int) expectedCount);

        long actualCount = subscriptionService.getFollowersCount(followeeId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowing() {
        setup();
        long followerId = 1L;
        User user = createUser();
        List<User> mockUsers = List.of(user);
        UserSubscriptionDto userDto = new UserSubscriptionDto();
        userDto.setUsername("testUser");
        List<UserSubscriptionDto> mockUserDtos = List.of(userDto);
        UserSubscriptionFilterDto filter = createFilter();

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(mockUsers.stream());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        List<UserSubscriptionDto> result = subscriptionService.getFollowing(followerId, filter);

        verify(subscriptionRepository).findByFollowerId(followerId);
        verify(userMapper, times(mockUsers.size())).toDto(any(User.class));
        assertEquals(mockUserDtos, result);
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;
        long expectedCount = 50L;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn((int) expectedCount);

        long actualCount = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    public void testFilterUsers() {
        setup();
        User user = createUser();
        List<User> mockUsers = List.of(user);
        UserSubscriptionFilterDto filter = createFilter();

        List<User> filteredUsers = subscriptionService.filterUsers(mockUsers, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals("testUser", filteredUsers.get(0).getUsername());
    }

    private UserSubscriptionFilterDto createFilter() {
        UserSubscriptionFilterDto filter = new UserSubscriptionFilterDto();
        filter.setNamePattern("testUser");
        filter.setAboutPattern("about");
        filter.setEmailPattern("test@example.com");
        filter.setContactPattern("123456789");
        filter.setCountryPattern("CountryTitle");
        filter.setCityPattern("CityName");
        filter.setPhonePattern("123456789");
        filter.setSkillPattern("SkillTitle");
        filter.setExperienceMin(4);
        filter.setExperienceMax(6);
        filter.setPage(0);
        filter.setPageSize(10);
        return filter;
    }

    private User createUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setAboutMe("about");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        Country country = new Country();
        country.setTitle("CountryTitle");
        user.setCountry(country);
        user.setCity("CityName");
        user.setExperience(5);
        Skill skill = new Skill();
        skill.setTitle("SkillTitle");
        user.setSkills(List.of(skill));
        return user;
    }
}
