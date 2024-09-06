package school.faang.user_service.mapper.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapperTest {
    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    private Event event;
    private User user;

    @BeforeEach
    public void setUp() {
        user = createUser(1L, "Misha");

        Skill skill1 = createSkill(1L, "Java");
        Skill skill2 = createSkill(2L, "Spring");
        List<Skill> relatedSkills = Arrays.asList(skill1, skill2);

        event = createEvent(100L, "Test Event", user, relatedSkills, "New York", 50);
    }

    @Test
    public void toDto_ShouldMapEventToEventDto() {
        EventDto actualDto = eventMapper.toDto(event);

        EventDto expectedDto = createExpectedEventDto(event);
        Assertions.assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds")
                .isEqualTo(expectedDto);
    }

    @Test
    public void toDtoList_ShouldMapEventListToEventDtoList() {
        List<Event> events = Arrays.asList(event);

        List<EventDto> actualDtos = eventMapper.toDto(events);

        List<EventDto> expectedDtos = events.stream()
                .map(this::createExpectedEventDto)
                .collect(Collectors.toList());

        Assertions.assertThat(actualDtos)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds")
                .isEqualTo(expectedDtos);
    }

    @Test
    public void toEventWithSubscribersDto_ShouldMapEventToEventWithSubscribersDto() {
        int subscribersCount = 100;

        EventWithSubscribersDto actualDto = eventMapper.toEventWithSubscribersDto(event, subscribersCount);

        EventWithSubscribersDto expectedDto = createExpectedEventWithSubscribersDto(event, subscribersCount);
        Assertions.assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds")
                .isEqualTo(expectedDto);
    }

    @Test
    public void toFilteredEventsDto_ShouldMapEventListToFilteredEventsDto() {
        List<Event> events = Arrays.asList(event);

        List<EventDto> actualDtos = eventMapper.toFilteredEventsDto(events);

        List<EventDto> expectedDtos = events.stream()
                .map(this::createExpectedEventDto)
                .collect(Collectors.toList());

        Assertions.assertThat(actualDtos)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds", "ownerUsername")
                .isEqualTo(expectedDtos);
    }

    private EventDto createExpectedEventDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setOwnerId(event.getOwner().getId());
        dto.setDescription(event.getDescription());
        dto.setRelatedSkillsIds(event.getRelatedSkills().stream().map(Skill::getId).collect(Collectors.toList()));
        dto.setLocation(event.getLocation());
        dto.setType(event.getType());
        dto.setStatus(event.getStatus());
        dto.setMaxAttendees(event.getMaxAttendees());
        return dto;
    }

    private EventWithSubscribersDto createExpectedEventWithSubscribersDto(Event event, int subscribersCount) {
        EventWithSubscribersDto dto = new EventWithSubscribersDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setOwnerId(event.getOwner().getId());
        dto.setDescription(event.getDescription());
        dto.setRelatedSkillsIds(event.getRelatedSkills().stream().map(Skill::getId).collect(Collectors.toList()));
        dto.setLocation(event.getLocation());
        dto.setMaxAttendees(event.getMaxAttendees());
        dto.setSubscribersCount(subscribersCount);
        dto.setType(event.getType());
        dto.setStatus(event.getStatus());
        return dto;
    }

    private Event createEvent(Long id, String title, User owner, List<Skill> relatedSkills, String location, int maxAttendees) {
        Event event = new Event();
        event.setId(id);
        event.setTitle(title);
        event.setOwner(owner);
        event.setRelatedSkills(relatedSkills);
        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now().plusDays(1));
        event.setLocation(location);
        event.setMaxAttendees(maxAttendees);
        event.setType(EventType.WEBINAR);
        event.setStatus(EventStatus.PLANNED);
        return event;
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Skill createSkill(Long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);
        return skill;
    }
}