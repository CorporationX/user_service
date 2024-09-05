package school.faang.user_service.test_data.event;

import lombok.Getter;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TestDataEvent {
    private Country getCountry() {
        return Country.builder()
                .id(1L)
                .title("Country")
                .build();
    }

    public Skill getSkill1() {
        return Skill.builder()
                .id(1L)
                .title("skill1")
                .build();
    }

    private Skill getSkill2() {
        return Skill.builder()
                .id(2L)
                .title("skill2")
                .build();
    }


    public SkillDto getSkillDto1() {
        return SkillDto.builder()
                .id(1L)
                .title("skill1")
                .build();
    }

    private SkillDto getSkillDto2() {
        return SkillDto.builder()
                .id(2L)
                .title("skill2")
                .build();
    }

    public SkillDto getInvalidSkillDto() {
        return SkillDto.builder()
                .id(10L)
                .title("invalidSkillDto")
                .build();
    }

    public User getUser() {
        return User.builder()
                .id(1L)
                .username("User1")
                .email("user1@mail.com")
                .password("123456789")
                .active(true)
                .country(getCountry())
                .skills(new ArrayList<>(List.of(getSkill1(), getSkill2())))
                .build();
    }

    public Event getEvent() {
        return Event.builder()
                .id(1L)
                .title("title1")
                .description("desc1")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .location("location1")
                .maxAttendees(10)
                .owner(getUser())
                .relatedSkills(new ArrayList<>(List.of(getSkill1(), getSkill2())))
                .type(EventType.MEETING)
                .status(EventStatus.PLANNED)
                .build();
    }
    public Event getEvent2() {
        return Event.builder()
                .id(1L)
                .title("title2")
                .description("desc2")
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(4))
                .location("location2")
                .maxAttendees(20)
                .owner(getUser())
                .relatedSkills(new ArrayList<>(List.of(getSkill1(), getSkill2())))
                .type(EventType.GIVEAWAY)
                .status(EventStatus.CANCELED)
                .build();
    }

    public EventDto getEventDto() {
        return EventDto.builder()
                .id(1L)
                .title("title1")
                .description("desc1")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .location("location1")
                .maxAttendees(10)
                .ownerId(getUser().getId())
                .relatedSkills(new ArrayList<>(List.of(getSkillDto1(), getSkillDto2())))
                .type(EventType.MEETING)
                .status(EventStatus.PLANNED)
                .build();
    }

    public EventDto getEventDto2() {
        return EventDto.builder()
                .id(2L)
                .title("title2")
                .description("desc2")
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(4))
                .location("location2")
                .maxAttendees(20)
                .ownerId(getUser().getId())
                .relatedSkills(new ArrayList<>(List.of(getSkillDto1(), getSkillDto2())))
                .type(EventType.GIVEAWAY)
                .status(EventStatus.CANCELED)
                .build();
    }

    public EventFilterDto getEventFilterDto() {
        return EventFilterDto.builder()
                .titlePattern("title1")
                .descriptionPattern("desc1")
                .startDatePattern(LocalDateTime.now().plusDays(1))
                .endDatePattern(LocalDateTime.now().plusDays(2))
                .locationPattern("location1")
                .maxAttendees(10)
                .skillPattern(getSkillDto1().toString())
                .typePattern(EventType.MEETING.getMessage())
                .statusPattern(EventStatus.PLANNED.getMessage())
                .build();
    }
}
