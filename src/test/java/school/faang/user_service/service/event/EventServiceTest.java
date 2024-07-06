package school.faang.user_service.service.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserReadDto;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.filter.event.EventStartDateAfterFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.event.EventCreateEditMapper;
import school.faang.user_service.mapper.event.EventReadMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.valitator.Error;
import school.faang.user_service.valitator.ValidationResult;
import school.faang.user_service.valitator.event.EventCreateEditValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventCreateEditValidator createEditValidator;
    @Mock
    private EventCreateEditMapper createEditMapper;
    @Mock
    private EventReadMapper readMapper;
    @Mock
    private EventRepository repository;

    private EventService eventService;

    private final LocalDateTime DATE_NOW = LocalDateTime.now();

    @BeforeEach
    void init() {
        List<EventFilter> eventFilters = List.of(new EventTitleFilter(), new EventStartDateAfterFilter());
        eventService = new EventService(repository, createEditMapper, readMapper, createEditValidator, readMapper, eventFilters);
    }

    @Test
    void create() {
        EventCreateEditDto eventCreateEditDto = getEventCreateEditDto();
        Event event = getEvent();
        EventReadDto eventReadDto = getEventReadDto();
        doReturn(new ValidationResult()).when(createEditValidator).validate(eventCreateEditDto);
        doReturn(event).when(createEditMapper).map(eventCreateEditDto);
        doReturn(eventReadDto).when(readMapper).map(event);

        EventReadDto actualResult = eventService.create(eventCreateEditDto);

        assertThat(actualResult).isEqualTo(eventReadDto);
        verify(repository).save(event);
    }

    @Test
    void findAllBy() {
        EventFilterDto eventFilterDto = new EventFilterDto("2", DATE_NOW.minusDays(1));
        List<EventReadDto> expectedResult = List.of(getEventReadDto("title2"), getEventReadDto("a2"));
        List<Event> events = List.of(getEvent("title"), getEvent("title2"), getEvent("atitle"), getEvent("a2"));
        doReturn(events).when(repository).findAll();
        doReturn(expectedResult.get(0)).when(readMapper).map(events.get(1));
        doReturn(expectedResult.get(1)).when(readMapper).map(events.get(3));

        List<EventReadDto> actualResult = eventService.findAllBy(eventFilterDto);

        assertThat(actualResult.size()).isEqualTo(2);
        assertThat(actualResult).isEqualTo(expectedResult);
    }


    @Test
    void shouldThrowExceptionIfDtoInvalid() {
        EventCreateEditDto eventCreateEditDto = getEventCreateEditDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.start-date", "start-date не может быть пустым"));
        doReturn(validationResult).when(createEditValidator).validate(eventCreateEditDto);

        assertThrows(DataValidationException.class, () -> eventService.create(eventCreateEditDto));
        verifyNoInteractions(repository, createEditMapper, readMapper);
    }

    private Event getEvent(String title) {
        Event event = getEvent();
        event.setTitle(title);
        return event;
    }


    private Event getEvent() {
        return Event.builder()
                .title("title")
                .startDate(DATE_NOW)
                .endDate(DATE_NOW.plusDays(1))
                .owner(getUser(1L))
                .description("description")
                .relatedSkills(Stream.of(1L, 2L)
                        .map(this::getSkill)
                        .toList())
                .type(EventType.POLL)
                .status(EventStatus.PLANNED)
                .maxAttendees(1)
                .build();
    }

    private EventCreateEditDto getEventCreateEditDto() {
        Event event = getEvent();
        return new EventCreateEditDto(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .toList(),
                event.getLocation(),
                event.getMaxAttendees(),
                event.getType(),
                event.getStatus()
        );
    }

    private EventReadDto getEventReadDto(String title) {
        Event event = getEvent(title);
        return new EventReadDto(
                event.getId(),
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                getUserReadDto(event.getOwner().getId()),
                event.getDescription(),
                event.getLocation(),
                event.getMaxAttendees()
        );
    }

    private EventReadDto getEventReadDto() {
        Event event = getEvent();
        return new EventReadDto(
                event.getId(),
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                getUserReadDto(event.getOwner().getId()),
                event.getDescription(),
                event.getLocation(),
                event.getMaxAttendees()
        );
    }

    private UserReadDto getUserReadDto(Long id) {
        return new UserReadDto(id);
    }


    private User getUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }


    private Skill getSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }
}
