package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
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

    @Test
    void getFollowersThrowIllegalException() {
        int idUser = -10;
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowers(idUser, new UserFilterDto()));
    }

    @Test
    public void testGetFollowersInvokesFindByFolloweeId() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto();
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());

        subscriptionService.getFollowers(followeeId, filterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
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
}