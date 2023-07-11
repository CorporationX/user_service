package school.faang.user_service.service;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void followUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }
    @Test
    void unfollowUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    void followUserThrowIllegalException(){
        int followerId = -11;
        int followeeId = -15;
        Assert.assertThrows(IllegalArgumentException.class,
                ()-> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    void unfollowUserThrowIllegalException(){
        int followerId = -11;
        int followeeId = -15;
        Assert.assertThrows(IllegalArgumentException.class,
                ()-> subscriptionService.unfollowUser(followerId, followeeId));
    }
    @Test
    void followUserThrowDataValidException() {
        int idUser = 11;
        Assert.assertThrows(DataValidationException.class,
                ()-> subscriptionService.followUser(idUser, idUser));
    }

    @Test
    void unfollowUserThrowDataValidException() {
        int idUser = 11;
        Assert.assertThrows(DataValidationException.class,
                ()-> subscriptionService.unfollowUser(idUser, idUser));
    }

    @Test
    void getFollowersThrowIllegalException(){
        int idUser = -10;
        Assert.assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowers(idUser, new UserFilterDto()));
    }

    @Test
    void filterUserReturnTrue(){
        User user = createUser();
        UserFilterDto filter = createUserFilterDto();
        Assertions.assertEquals(true, subscriptionService.filterUser(user, filter));
    }

    @Test
    void filterUserReturnFalse(){
        User user = createUser();
        user.setCity("notCity");
        UserFilterDto filter = createUserFilterDto();
        Assertions.assertEquals(false, subscriptionService.filterUser(user, filter));
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
        userFilterDto.setNamePattern("Username");
        userFilterDto.setAboutPattern("I'm interesting person");
        userFilterDto.setEmailPattern("user@mail.com");
        userFilterDto.setContactPattern("UserContact");
        userFilterDto.setCountryPattern("UserCountry");
        userFilterDto.setCityPattern("UserCity");
        userFilterDto.setPhonePattern("89991324567");
        userFilterDto.setSkillPattern("userSkill");
        userFilterDto.setExperienceMin(1);
        userFilterDto.setExperienceMax(7);
        return userFilterDto;
    }
}