package school.faang.user_service.filters;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserFilterTest {
    UserFilter userFilter = new UserFilter();

    @Test
    void applyFilterThrowNullPointer(){
        User user = null;
        assertThrows(NullPointerException.class, () ->
                userFilter.applyFilter(List.of(new User(), user), createUserFilterDto()));
    }

    @Test
    void applyFilterWorks(){
        User user = createUser();
        UserFilterDto filterDto = createUserFilterDto();
        assertFalse(userFilter.applyFilter(List.of(user), filterDto).isEmpty());
    }

    @Test
    void filterUserReturnValidUserDto(){
        User user = createUser();
        UserFilterDto filter = createUserFilterDto();
        assertFalse(userFilter.applyFilter(List.of(user), filter).isEmpty());
    }

    @Test
    void filterUserDontReturnInvalidUserDto(){
        User user = createUser();
        user.setCity("1city");
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
        userFilterDto.setPhonePattern("\\d+");
        userFilterDto.setSkillPattern("\\D+");
        userFilterDto.setExperienceMin(1);
        userFilterDto.setExperienceMax(7);
        return userFilterDto;
    }

}