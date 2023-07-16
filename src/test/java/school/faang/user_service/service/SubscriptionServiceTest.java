package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.SubscriptionService.getPage;

public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFollowersThrowIllegalException(){
        int idUser = -10;
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowing(idUser, new UserFilterDto()));
    }
    @Test
    void filterUserReturnTrue(){
        User user = createUser("Username");
        UserFilterDto filter = createUserFilterDto();
        Assertions.assertEquals(true, subscriptionService.filterUser(user, filter));
    }

    @Test
    void filterUserReturnFalse(){
        User user = createUser("Username");
        user.setCity("1231");
        UserFilterDto filter = createUserFilterDto();
        Assertions.assertEquals(false, subscriptionService.filterUser(user, filter));
    }

    @Test
    void filterUserPagination() {
        long followeeId = 123;
        User user1 = createUser("Usernameone");
        User user2 = createUser("Usernametwo");
        User user3 = createUser("Usernamethree");
        List<User> mockUsers = Arrays.asList(user1, user2 ,user3);
        List<UserDto> resultList = Arrays.asList((new UserDto(user2.getId(), user2.getUsername(), user2.getEmail())));

        UserFilterDto filter = createUserFilterDto();
        filter.setPage(1);
        filter.setPageSize(1);

        when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(mockUsers.stream());

        assertEquals(resultList, subscriptionService.getFollowing(followeeId, filter));
    }


    @Test
    void filterUserPaginationGetPage() {
        List<String> resultList = Arrays.asList("one", "two", "three");
        assertEquals("two", getPage(resultList, 1, 1).get(0));
    }




    private User createUser(String username){
        User user = new User();
        user.setUsername(username);
        user.setEmail("user@mail.com");
        user.setPhone("89991324567");
        user.setAboutMe("I'm interesting person");
        user.setCountry(createCountry());
        user.setCity("UserCity");
        user.setExperience(5);
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
        userFilterDto.setExperienceMax(10);
        return userFilterDto;
    }
}


