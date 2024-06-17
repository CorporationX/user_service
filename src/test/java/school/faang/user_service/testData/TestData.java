package school.faang.user_service.testData;

import lombok.Getter;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.user.image.BufferedImagesHolder;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
public class TestData {
    private UserDto userDto;
    private User user;
    private BufferedImage bufferedImage;
    private BufferedImagesHolder bufferedImagesHolder;
    private EventDto eventDto;
    private Event event;

    public TestData() {
        createUserDto();
        createUserEntity();
        createBufferedImage();
        createBufferedImagesHolder();
        createEventDto();
        createEvent();
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
    }

    private void createEvent() {
        event = new Event();
        event.setId(1L);
        event.setTitle("Title");
        event.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        event.setEndDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
        var owner = new User();
        owner.setId(1L);
        event.setOwner(owner);
        event.setDescription("Description");

        var skillA = new Skill();
        skillA.setTitle("SQL");
        var skillB = new Skill();
        skillB.setTitle("Java");
        event.setRelatedSkills(List.of(skillA, skillB));
        event.setLocation("Location");
    }

    private void createBufferedImagesHolder() {
        bufferedImagesHolder = new BufferedImagesHolder(bufferedImage);
    }

    private void createBufferedImage() {
        bufferedImage = new BufferedImage(100, 100, 1);
    }

    private void createUserEntity() {
        user = new User();
        user.setId(1L);
        user.setUsername("nadir");
        user.setPhone("88005553535");
        user.setEmail("nadir@gmail.com");
        user.setPassword("12345678");
        user.setActive(true);
        var country = new Country();
        country.setId(1L);
        user.setCountry(country);
        ContactPreference contactPreference = new ContactPreference();
        contactPreference.setPreference(PreferredContact.SMS);
        user.setContactPreference(contactPreference);
        user.setUserProfilePic(UserProfilePic.builder()
                .smallFileId("smallFileId")
                .fileId("fileId")
                .build());
    }

    private void createUserDto() {
        userDto = new UserDto(1L,
                "nadir",
                "12345678",
                "nadir@gmail.com",
                "88005553535",
                true,
                1L,
                PreferredContact.SMS);
    }

    private void createEventDto() {
        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        eventDto.setEndDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
    }
}
