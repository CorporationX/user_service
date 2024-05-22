package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserAboutFilter;
import school.faang.user_service.service.user.filter.UserEmailFilter;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserNameFilter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    UserFilterDto userFilterDto;

    @Mock
    UserDto userDto;

    @Mock
    private User user;

    @InjectMocks
    private SubscriptionService subscriptionService;

    List<UserFilter> userFilters;
    long followerId = 1L;
    long followeeId = 2L;
    int expectationCount = 15;

    @BeforeEach
    public void setUp() {
        userFilters = List.of(new UserNameFilter(), new UserAboutFilter(), new UserEmailFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters);
    }

    @Test
    public void testExceptionTheSubscriptionExistsForFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUserIsSaved() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testExceptionTheSubscriptionExistsForUnfollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
    }

    @Test
    public void testUnfollowUserIsSaved() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        User userAnna = User.builder()
                .id(1L)
                .username("Anna Kern")
                .aboutMe("Pushkin's muse")
                .email("annak@examle.com")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "Russia", Collections.emptyList()))
                .city("Orel")
                .phone("")
                .skills(List.of(Skill.builder().id(1L).title("skill1Anna").build(), Skill.builder().id(2L).title("skill2Anna").build()))
                .experience(4)
                .build();
        User userBeast = User.builder()
                .id(2L)
                .username("Mr Beast")
                .aboutMe("Youtuber, voice actor, entrepreneur and philanthropist")
                .email("mrbeast@gmail.com")
                .contacts(Collections.emptyList())
                .country(new Country(2L, "USA", Collections.emptyList()))
                .city("Wichita")
                .phone("(123) 123-4567")
                .skills(List.of(Skill.builder().id(3L).title("skill1Beast").build(), Skill.builder().id(4L).title("skill2Beast").build(), Skill.builder().id(5L).title("skill3Beast").build()))
                .experience(7)
                .build();

        UserDto userDtoAnna = new UserDto(1L, "Anna Kern", "annak@examle.com");
        UserDto userDtoBeast = new UserDto(2L, "Mr Beast", "mrbeast@gmail.com");

        UserFilterDto filters = new UserFilterDto("Mr", "actor", null, null, null, null, null, null, 0, 0, 0, 0);
        UserFilterDto filtersNull = new UserFilterDto();

        List<User> users = List.of(userAnna, userBeast);

        when(userMapper.toDto(userAnna)).thenReturn(userDtoAnna);
        when(userMapper.toDto(userBeast)).thenReturn(userDtoBeast);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users.stream());
        List<UserDto> expectationUserDto = List.of(userDtoBeast);
        List<UserDto> actualUserDto = subscriptionService.getFollowers(followeeId, filters);
        assertEquals(expectationUserDto, actualUserDto);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users.stream());
        expectationUserDto = List.of(userDtoAnna, userDtoBeast);
        actualUserDto = subscriptionService.getFollowers(followeeId, filtersNull);
        assertEquals(expectationUserDto, actualUserDto);
    }

    @Test
    public void testFindByFollowerIdIsInvoked() {
        Stream<User> users = Stream.of(user);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> expectedUserDto = List.of(userDto);
        List<UserDto> actualUserDto = subscriptionService.getFollowers(followeeId, userFilterDto);

        assertEquals(expectedUserDto, actualUserDto);
        verify(subscriptionRepository, times(1)).findByFollowerId(followeeId);
    }

    @Test
    public void testGetFollowing() {
        User userAnna = User.builder()
                .id(1L)
                .username("Anna Kern")
                .aboutMe("Pushkin's muse")
                .email("annak@examle.com")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "Russia", Collections.emptyList()))
                .city("Orel")
                .phone("")
                .skills(List.of(Skill.builder().id(1L).title("skill1Anna").build(), Skill.builder().id(2L).title("skill2Anna").build()))
                .experience(4)
                .build();
        User userBeast = User.builder()
                .id(2L)
                .username("Mr Beast")
                .aboutMe("Youtuber, voice actor, entrepreneur and philanthropist")
                .email("mrbeast@gmail.com")
                .contacts(Collections.emptyList())
                .country(new Country(2L, "USA", Collections.emptyList()))
                .city("Wichita")
                .phone("(123) 123-4567")
                .skills(List.of(Skill.builder().id(3L).title("skill1Beast").build(), Skill.builder().id(4L).title("skill2Beast").build(), Skill.builder().id(5L).title("skill3Beast").build()))
                .experience(7)
                .build();

        UserDto userDtoAnna = new UserDto(1L, "Anna Kern", "annak@examle.com");
        UserDto userDtoBeast = new UserDto(2L, "Mr Beast", "mrbeast@gmail.com");

        UserFilterDto filters = new UserFilterDto("Mr", "actor", null, null, null, null, null, null, 0, 0, 0, 0);
        UserFilterDto filtersNull = new UserFilterDto();

        List<User> users = List.of(userAnna, userBeast);

        when(userMapper.toDto(userAnna)).thenReturn(userDtoAnna);
        when(userMapper.toDto(userBeast)).thenReturn(userDtoBeast);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        List<UserDto> expectationUserDto = List.of(userDtoBeast);
        List<UserDto> actualUserDto = subscriptionService.getFollowing(followeeId, filters);
        assertEquals(expectationUserDto, actualUserDto);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        expectationUserDto = List.of(userDtoAnna, userDtoBeast);
        actualUserDto = subscriptionService.getFollowing(followeeId, filtersNull);
        assertEquals(expectationUserDto, actualUserDto);
    }

    @Test
    public void testFindByFolloweeIdIsInvoked() {
        Stream<User> users = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> expectedUserDto = List.of(userDto);
        List<UserDto> actualUserDto = subscriptionService.getFollowing(followerId, userFilterDto);

        assertEquals(expectedUserDto, actualUserDto);
        verify(subscriptionRepository, times(1)).findByFolloweeId(followerId);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(expectationCount);

        int actualCount = subscriptionService.getFollowersCount(followeeId);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
        assertEquals(expectationCount, actualCount);
    }

    @Test
    public void testGetFollowingCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectationCount);

        int actualCount = subscriptionService.getFollowingCount(followerId);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
        assertEquals(expectationCount, actualCount);
    }
}