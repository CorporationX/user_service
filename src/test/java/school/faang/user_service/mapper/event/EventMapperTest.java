package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventMapperTest {
    private EventMapper mapper;
    private EventDto eventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        long eventId = 1L;
        String title = "title";
        String description = "description";
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 10, 10);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 2, 10, 10);
        String location = "location";
        int maxAttendees = 3;
        User attendee = new User();
        attendee.setId(1L);
        List<User> attendees = new ArrayList<>(List.of(attendee));
        Rating rating = new Rating();
        rating.setId(1L);
        List<Rating> ratings = new ArrayList<>(List.of(rating));
        User owner = new User();
        attendee.setId(1L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);
        List<Skill> relatedSkills = new ArrayList<>(List.of(firstSkill, secondSkill));
        EventType type = EventType.MEETING;
        EventStatus status = EventStatus.IN_PROGRESS;
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 8, 10);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 9, 10);

        event = new Event(eventId, title, description, startDate, endDate, location, maxAttendees, attendees,
                ratings, owner, relatedSkills, type, status, createdAt, updatedAt);

        List<Long> relatedSkillIds = relatedSkills.stream()
                .map(Skill::getId)
                .toList();
        eventDto = new EventDto(eventId, title, description, startDate, endDate, location, maxAttendees,
                owner.getId(), relatedSkillIds);

        mapper = new EventMapperImpl();
    }

    @Test
    public void testToDto() {
        EventDto actual = mapper.toDto(event);

        assertEquals(eventDto, actual);
    }

    @Test
    public void testToEntity() {
        Event eventExp = new Event();
        eventExp.setId(eventDto.getId());
        eventExp.setTitle(eventDto.getTitle());
        eventExp.setDescription(eventDto.getDescription());
        eventExp.setStartDate(eventDto.getStartDate());
        eventExp.setEndDate(eventDto.getEndDate());
        eventExp.setLocation(eventDto.getLocation());
        eventExp.setMaxAttendees(eventDto.getMaxAttendees());
        User owner = new User();
        owner.setId(eventDto.getOwnerId());
        eventExp.setOwner(owner);

        Event actual = mapper.toEntity(eventDto);

        assertEquals(eventExp, actual);
    }
}
