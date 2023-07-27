package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.filter.user.*;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    static Stream<UserFilter> argsProvider1() {
        return Stream.of(
                new UserNameFilter(),
                new UserAboutFilter(),
                new UserCityFilter(),
                new UserContactFilter(),
                new UserCountryFilter(),
                new UserEmailFilter(),
                new UserExperienceMinFilter(),
                new UserExperienceMaxFilter(),
                new UserPaginationFilter(),
                new UserPhoneFilter(),
                new UserSkillFilter()
        );
    }

    static Stream<Arguments> argsProvider2() {
        return Stream.of(
                Arguments.of(new UserNameFilter(), UserFilterDto.builder().namePattern("d").build()),
                Arguments.of(new UserAboutFilter(), UserFilterDto.builder().aboutPattern("about").build()),
                Arguments.of(new UserEmailFilter(), UserFilterDto.builder().emailPattern("@").build()),
                Arguments.of(new UserContactFilter(), UserFilterDto.builder().contactPattern("Adil").build()),
                Arguments.of(new UserCountryFilter(), UserFilterDto.builder().countryPattern("Kaz").build()),
                Arguments.of(new UserCityFilter(), UserFilterDto.builder().cityPattern("As").build()),
                Arguments.of(new UserPhoneFilter(), UserFilterDto.builder().phonePattern("+7").build()),
                Arguments.of(new UserSkillFilter(), UserFilterDto.builder().skillPattern("respon").build()),
                Arguments.of(new UserExperienceMinFilter(), UserFilterDto.builder().experienceMin(5).build()),
                Arguments.of(new UserPaginationFilter(), UserFilterDto.builder().pageSize(1).build())
        );
    }

    private List<User> users() {
        return List.of(
                User.builder().username("Silvester").build(),
                User.builder().username("Ferdinant").build()
        );
    }

    private List<UserDto> usersDto() {
        return List.of(
                UserDto.builder().username("Silvester").build(),
                UserDto.builder().username("Ferdinant").build()
        );
    }

    @BeforeEach
    void setUp() {
        List<UserFilter> userFilters = List.of(
                new UserNameFilter(),
                new UserAboutFilter()
        );
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilters);
    }

    @Test
    void getFollowersThrowIllegalException() {
        int idUser = -10;
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowers(idUser, new UserFilterDto()));
    }

    @Test
    public void testGetFollowersInvokes() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("F");
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users().stream());
        when(userMapper.toDtoList(List.of(users().get(1)))).thenReturn(List.of(usersDto().get(1)));

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filterDto);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Ferdinant", result.get(0).getUsername());
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    void getFollowingThrowIllegalException() {
        int idUser = -10;
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowing(idUser, new UserFilterDto()));
    }

    @Test
    public void testGetFollowingInvokes() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("F");
        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users().stream());
        when(userMapper.toDtoList(List.of(users().get(1)))).thenReturn(List.of(usersDto().get(1)));

        List<UserDto> result = subscriptionService.getFollowing(followeeId, filterDto);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Ferdinant", result.get(0).getUsername());
        verify(subscriptionRepository, times(1)).findByFollowerId(followeeId);
    }

    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(UserFilter userFilter) {
        UserFilterDto filter = new UserFilterDto("f", "f", "f", "f", "f", "f", "f", "f", 5, 10, 0, 5);
        UserFilterDto filter2 = new UserFilterDto();
        filter2.setExperienceMin(-1);

        boolean result1 = userFilter.isApplicable(filter);
        boolean result2 = userFilter.isApplicable(filter2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @ParameterizedTest
    @MethodSource("argsProvider2")
    public void testApply(UserFilter userFilter, UserFilterDto filter) {
        User user1 = User.builder().username("Adil").aboutMe("about me").email("adil.200191@gmail.com").city("Astana").phone("+7705254465").experience(7).id(1).build();
        Contact contact = Contact.builder().contact("Adil brother").build();
        Skill skill = Skill.builder().title("responsibility").build();
        user1.setContacts(List.of(contact));
        user1.setSkills(List.of(skill));
        user1.setCountry(Country.builder().title("Kazakhstan").build());

        User user2 = User.builder().username("123").aboutMe("its me Mario").email("press F").city("chebypeli").phone("din don").experience(0).id(2).build();
        Contact contact2 = Contact.builder().contact("").build();
        Skill skill2 = Skill.builder().title("").build();
        user2.setContacts(List.of(contact2));
        user2.setSkills(List.of(skill2));
        user2.setCountry(Country.builder().title("").build());

        List<User> result = userFilter.apply(Stream.of(user1, user2), filter).collect(Collectors.toList());

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(user1, result.get(0))
        );
    }

    @Test
    public void testFollowUser_ThrowsExceptionOnExistingSubscription() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidException.class, () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(anyLong(), anyLong());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUser_ThrowsExceptionOnSelfSubscription() {
        long followerId = 1L;

        assertThrows(DataValidException.class, () -> subscriptionService.followUser(followerId, followerId));
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUser_CallsRepositoryOnValidSubscription() {
        long followerId = 1L;
        long followeeId = 2L;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        Mockito.verify(subscriptionRepository, Mockito.times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser_ThrowsExceptionOnSelfUnfollow() {
        long userId = 1;
        assertThrows(DataValidException.class, () -> subscriptionService.unfollowUser(userId, userId));
    }

    @Test
    public void testUnfollowUser_CallsRepositoryOnValidUnfollow() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void testGetFollowersCount() {
        long followeeId = 123;
        int followersCount = 42;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followersCount);

        int result = subscriptionService.getFollowersCount(followeeId);

        assertEquals(followersCount, result);
    }

    @Test
    void testGetFollowingCount() {
        long followerId = 123;
        int expectedCount = 10;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);

        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, result);
    }
}