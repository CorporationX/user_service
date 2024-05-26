package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.user.UserAboutFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private List<UserFilter> userFilters;

    private UserFilterDto userFilterDto;

    private UserDto userDto;

    private User user;

    private long followerId;
    private long followeeId;
    private int expectationCount;

    @BeforeEach
    public void setUp() {
        userFilterDto = new UserFilterDto();
        userDto = new UserDto();
        user = new User();

        followerId = 1L;
        followeeId = 2L;
        expectationCount = 15;
    }

    @Test
    public void testFollowUser() {
        subscriptionService.followUser(followerId, followeeId);

        verify(userValidator, times(1)).checkUserInDB(followerId);
        verify(userValidator, times(1)).checkUserInDB(followeeId);
        verify(subscriptionValidator, times(1)).checkSubscriptionExists(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser() {
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionValidator, times(1)).checkSubscriptionNotExists(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        userFilters = List.of(new UserNameFilter(), new UserAboutFilter(), new UserEmailFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, subscriptionValidator, userValidator, userFilters);

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

        UserDto userDtoAnna = UserDto.builder()
                .id(1L)
                .username("Anna Kern")
                .email("annak@examle.com")
                .build();
        UserDto userDtoBeast = UserDto.builder()
                .id(2L)
                .username("Mr Beast")
                .email("mrbeast@gmail.com")
                .build();

        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("Mr")
                .aboutPattern("actor")
                .build();
        UserFilterDto filtersNull = UserFilterDto.builder().build();

        List<User> users = List.of(userAnna, userBeast);

        when(userMapper.toDto(userAnna)).thenReturn(userDtoAnna);
        when(userMapper.toDto(userBeast)).thenReturn(userDtoBeast);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        List<UserDto> expectationUserDto = List.of(userDtoBeast);
        List<UserDto> actualUserDto = subscriptionService.getFollowers(followeeId, filters);
        assertEquals(expectationUserDto, actualUserDto);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        expectationUserDto = List.of(userDtoAnna, userDtoBeast);
        actualUserDto = subscriptionService.getFollowers(followeeId, filtersNull);
        assertEquals(expectationUserDto, actualUserDto);
    }

    @Test
    public void testFindByFollowerIdIsInvoked() {
        userFilters = List.of(new UserNameFilter(), new UserAboutFilter(), new UserEmailFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, subscriptionValidator, userValidator, userFilters);

        Stream<User> users = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> expectedUserDto = List.of(userDto);
        List<UserDto> actualUserDto = subscriptionService.getFollowers(followeeId, userFilterDto);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userValidator, times(1)).checkUserInDB(followeeId);
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowing() {
        userFilters = List.of(new UserNameFilter(), new UserAboutFilter(), new UserEmailFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, subscriptionValidator, userValidator, userFilters);

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

        UserDto userDtoAnna = UserDto.builder()
                .id(1L)
                .username("Anna Kern")
                .email("annak@examle.com")
                .build();
        UserDto userDtoBeast = UserDto.builder()
                .id(2L)
                .username("Mr Beast")
                .email("mrbeast@gmail.com")
                .build();

        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("Mr")
                .aboutPattern("actor")
                .build();
        UserFilterDto filtersNull = UserFilterDto.builder().build();

        List<User> users = List.of(userAnna, userBeast);

        when(userMapper.toDto(userAnna)).thenReturn(userDtoAnna);
        when(userMapper.toDto(userBeast)).thenReturn(userDtoBeast);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users.stream());
        List<UserDto> expectationUserDto = List.of(userDtoBeast);
        List<UserDto> actualUserDto = subscriptionService.getFollowing(followeeId, filters);
        assertEquals(expectationUserDto, actualUserDto);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users.stream());
        expectationUserDto = List.of(userDtoAnna, userDtoBeast);
        actualUserDto = subscriptionService.getFollowing(followeeId, filtersNull);
        assertEquals(expectationUserDto, actualUserDto);
    }

    @Test
    public void testFindByFolloweeIdIsInvoked() {
        userFilters = List.of(new UserNameFilter(), new UserAboutFilter(), new UserEmailFilter());
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, subscriptionValidator, userValidator, userFilters);

        Stream<User> users = Stream.of(user);

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> expectedUserDto = List.of(userDto);
        List<UserDto> actualUserDto = subscriptionService.getFollowing(followerId, userFilterDto);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userValidator, times(1)).checkUserInDB(followerId);
        verify(subscriptionRepository, times(1)).findByFollowerId(followerId);
    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(expectationCount);

        int actualCount = subscriptionService.getFollowersCount(followeeId);

        verify(userValidator, times(1)).checkUserInDB(followeeId);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
        assertEquals(expectationCount, actualCount);
    }

    @Test
    public void testGetFollowingCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectationCount);

        int actualCount = subscriptionService.getFollowingCount(followerId);

        verify(userValidator, times(1)).checkUserInDB(followerId);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
        assertEquals(expectationCount, actualCount);
    }
}