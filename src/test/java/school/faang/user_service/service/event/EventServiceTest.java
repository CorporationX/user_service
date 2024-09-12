package school.faang.user_service.service.event;

import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// TODO: Fix NPE in create() test

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    EventService eventService;

    @Spy
    @InjectMocks
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventValidator eventValidator;

    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        Skill skill = Skill.builder()
                .id(1L)
                .title("Skill1")
                .build();

        event = Event.builder()
                .id(1L)
                .title("Event Title")
                .description("Event Description")
                .startDate(LocalDateTime.of(2024, 7, 31, 10, 30))
                .endDate(LocalDateTime.of(2024, 8, 1, 10, 30))
                .location("Event Location")
                .maxAttendees(100)
                .owner(owner)
                .relatedSkills(Collections.singletonList(skill))
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("Event Title")
                .description("Event Description")
                .startDate(LocalDateTime.of(2024, 7, 31, 10, 30))
                .endDate(LocalDateTime.of(2024, 8, 1, 10, 30))
                .location("Event Location")
                .maxAttendees(100)
                .ownerId(1L)
                .relatedSkills(Collections.singletonList(new SkillDto(1L, "Skill1")))
                .build();
    }

    @Test
    void create_shouldReturnEventDto() {
        // Arrange
        when(eventRepository.save(any())).thenReturn(event);

        // Act
        EventDto result = eventService.create(eventDto);

        // Assert
        assertEquals(eventDto, result);
    }

    @Test
    void getEvent_shouldReturnEventDto() {
        // Arrange
        Long eventId = eventDto.id();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        EventDto result = eventService.getEvent(eventId);

        //Assert
        assertEquals(eventDto, result);
    }

}
