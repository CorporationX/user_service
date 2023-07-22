package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.filter.user.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
public class UserFilterTest {
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
                Arguments.of(new UserNameFilter(), new UserFilterDto("A", null, null, null ,null, null ,null ,null, 0, 0, 0, 10)),
                Arguments.of(new UserAboutFilter(), new UserFilterDto(null, "about", null, null ,null, null ,null ,null, 0, 0, 0, 10)),
                Arguments.of(new UserEmailFilter(), new UserFilterDto(null, null, "@", null ,null, null ,null ,null, 0, 0, 0, 10)),
                Arguments.of(new UserContactFilter(), new UserFilterDto(null, null, null, "Adil" ,null, null ,null ,null, 0, 0, 0, 10)),
                Arguments.of(new UserCountryFilter(), new UserFilterDto(null, null, null, null ,"Kaz", null ,null ,null, 0, 0, 0, 10)),
                Arguments.of(new UserCityFilter(), new UserFilterDto(null, null, null, null ,null, "As" ,null ,null, 0, 0, 0, 10)),
                Arguments.of(new UserPhoneFilter(), new UserFilterDto(null, null, null, null ,null, null ,"+7" ,null, 0, 0, 0, 10)),
                Arguments.of(new UserSkillFilter(), new UserFilterDto(null, null, null, null ,null, null ,null ,"respons", 0, 0, 0, 10)),
                Arguments.of(new UserExperienceMinFilter(), new UserFilterDto(null, null, null, null ,null, null ,null ,null, 5, 0, 0, 10)),
                Arguments.of(new UserPaginationFilter(), new UserFilterDto(null, null, null, null ,null, null ,null ,null, 0, 0, 0, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(UserFilter userFilter) {
        UserFilterDto filter = new UserFilterDto("f", "f", "f","f","f","f","f","f",5, 10, 0, 5);
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
