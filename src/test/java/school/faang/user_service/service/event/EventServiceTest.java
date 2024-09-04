package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserReadDto;
import school.faang.user_service.dto.event.ReadEvetDto;
import school.faang.user_service.dto.event.WriteEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.filter.event.EventStartDateAfterFieldFilter;
import school.faang.user_service.filter.event.EventTitleFieldFilter;
import school.faang.user_service.mapper.event.EventToReadEventDtoMapper;
import school.faang.user_service.mapper.event.WriteEventDtoToEventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.WriteEventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private WriteEventValidator writeEventValidator;
    @Mock
    private WriteEventDtoToEventMapper writeEventDtoToEventMapper;
    @Mock
    private EventToReadEventDtoMapper eventToReadEventDtoMapper;
    @Mock
    private EventRepository repository;
    @Mock
    private EventTitleFieldFilter eventTitleFieldFilter;
    @Mock
    private EventStartDateAfterFieldFilter eventStartDateAfterFieldFilter;

    private EventService eventService;

    private final LocalDateTime DATE_NOW = LocalDateTime.now();

    @BeforeEach
    void init() {
        eventService = new EventService(
                repository,
                writeEventDtoToEventMapper,
                eventToReadEventDtoMapper,
                writeEventValidator,
                List.of(eventTitleFieldFilter, eventStartDateAfterFieldFilter));
    }

    @Test
    void create() {
        WriteEventDto writeEventDto = getWriteEventDto();
        Event event = getEvent();
        ReadEvetDto readEvetDto = getReadEvetDto();
        doReturn(event).when(writeEventDtoToEventMapper).map(writeEventDto);
        doReturn(readEvetDto).when(eventToReadEventDtoMapper).map(event);

        ReadEvetDto actualResult = eventService.create(writeEventDto);

        assertThat(actualResult).isEqualTo(readEvetDto);
        verify(writeEventValidator).validate(writeEventDto);
        verify(repository).save(event);
    }

    @Test
    void findAllByFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto("2", DATE_NOW.minusDays(1));
        List<ReadEvetDto> expectedResult = List.of(getReadEvetDto("title2"), getReadEvetDto("a2"));
        List<Event> events = List.of(getEvent("title"), getEvent("title2"), getEvent("atitle"), getEvent("a2"));
        doReturn(events).when(repository).findAll();
        doReturn(true).when(eventTitleFieldFilter).isApplicable(eventFilterDto);
        doReturn(true).when(eventStartDateAfterFieldFilter).isApplicable(eventFilterDto);
        doReturn(Stream.of(events.get(1))).when(eventTitleFieldFilter).apply(Mockito.any(), Mockito.any());
        doReturn(Stream.of(events.get(3))).when(eventStartDateAfterFieldFilter).apply(Mockito.any(), Mockito.any());
        doReturn(expectedResult.get(0)).when(eventToReadEventDtoMapper).map(events.get(1));
        doReturn(expectedResult.get(1)).when(eventToReadEventDtoMapper).map(events.get(3));

        List<ReadEvetDto> actualResult = eventService.findAllByFilter(eventFilterDto);

        assertThat(actualResult.size()).isEqualTo(2);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void shouldCreateStopIfNotValidated() {
        WriteEventDto writeEventDto = getWriteEventDto();
        doThrow(new DataValidationException("any")).when(writeEventValidator).validate(writeEventDto);

        assertThrows(DataValidationException.class, () -> eventService.create(writeEventDto));
        verify(writeEventValidator).validate(writeEventDto);
        verifyNoInteractions(repository, writeEventDtoToEventMapper, eventToReadEventDtoMapper);
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

    private WriteEventDto getWriteEventDto() {
        Event event = getEvent();
        return new WriteEventDto(
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

    private ReadEvetDto getReadEvetDto(String title) {
        Event event = getEvent(title);
        return new ReadEvetDto(
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

    private ReadEvetDto getReadEvetDto() {
        Event event = getEvent();
        return new ReadEvetDto(
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
