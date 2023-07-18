package school.faang.user_service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.filters.filtersForUserFilterDto.DtoUserFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserAboutFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserCityFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserContactFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserCountryFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserEmailFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserExperienceMaxFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserExperienceMinFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserNameFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserPageFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserPageSizeFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserPhoneFilter;
import school.faang.user_service.filters.filtersForUserFilterDto.UserSkillFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UserFilterTest {

    private UserMapper userMapper = new UserMapperImpl();
    @Mock
    private DtoUserFilter dtoUserFilter ;
    @Mock
    private UserAboutFilter userAboutFilter;
    @Mock
    private UserCityFilter userCityFilter;
    @Mock
    private UserContactFilter userContactFilter;
    @Mock
    private UserCountryFilter userCountryFilter;
    @Mock
    private UserEmailFilter userEmailFilter;
    @Mock
    private UserExperienceMaxFilter userExperienceMaxFilter;
    @Mock
    private UserExperienceMinFilter userExperienceMinFilter;
    @Mock
    private UserNameFilter userNameFilter;
    @Mock
    private UserPageFilter userPageFilter;
    @Mock
    private UserPageSizeFilter userPageSizeFilter;
    @Mock
    private UserPhoneFilter userPhoneFilter;
    @Mock
    private UserSkillFilter userSkillFilter;

    private List<DtoUserFilter> allFilters;
    private UserFilter userFilter;

    @BeforeEach
    void setUp(){
        allFilters = List.of(userAboutFilter, userCityFilter, userContactFilter, userCountryFilter,
                userEmailFilter, userExperienceMaxFilter, userExperienceMinFilter, userNameFilter,
                userPageFilter, userPageSizeFilter, userPhoneFilter, userSkillFilter);
        userFilter = new UserFilter(allFilters, userMapper);
    }


    @Test
    void applyFilterThrowNullPointer(){
        User user = null;
        assertThrows(NullPointerException.class, () ->
                userFilter.applyFilter(List.of(new User(), user), createUserFilterDto()));
    }

    @Test
    void filterUserReturnValidUserDto(){
        User user = createUser();
        UserFilterDto filter = createUserFilterDto();
        assertEquals(1, (userFilter.applyFilter(List.of(user), filter)).size());
    }

    @Test
    void filterUserDontReturnInvalidUserDto(){
        User user = createUser();
        user.setEmail("dsfs");
        UserFilterDto filter = createUserFilterDto();
        assertTrue(userFilter.applyFilter(List.of(user), filter).isEmpty());
    }

    private User createUser(){
        User user = new User();
        user.setUsername("Username");
        user.setEmail("user@mail.com");
        user.setPhone("89991324567");
        user.setAboutMe("I'm interesting person");
        user.setCountry(createCountry());
        user.setCity("UserCity");
        user.setExperience(4);
        user.setContacts(createContacts());
        user.setSkills(createSkills());
        return user;
    }

    private Country createCountry(){
        Country country = new Country();
        country.setTitle("UserCountry");
        return country;
    }
    private List<Contact> createContacts(){
        Contact contact = new Contact();
        Contact contact2 = new Contact();
        contact.setContact("UserContact");
        contact2.setContact("UserContact");
        return List.of(contact, contact2);
    }
    private List<Skill> createSkills(){
        Skill skill = new Skill();
        Skill skill2 = new Skill();
        skill.setTitle("userSkill");
        skill2.setTitle("userSkill");
        return List.of(skill, skill2);
    }

    private UserFilterDto createUserFilterDto(){
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("\\D+");
        userFilterDto.setAboutPattern("\\D+");
        userFilterDto.setEmailPattern("\\D+@\\D+");
        userFilterDto.setContactPattern("\\D+");
        userFilterDto.setCountryPattern("\\D+");
        userFilterDto.setCityPattern("\\D+");
        userFilterDto.setPhonePattern("\\D+");
        userFilterDto.setSkillPattern("\\D+");
        userFilterDto.setExperienceMin(1);
        userFilterDto.setExperienceMax(7);
        return userFilterDto;
    }

}