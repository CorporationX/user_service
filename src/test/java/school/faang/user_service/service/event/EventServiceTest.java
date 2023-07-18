package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.filters.EventEndDateFilter;
import school.faang.user_service.service.event.filters.EventFilter;
import school.faang.user_service.service.event.filters.EventStartDateFilter;
import school.faang.user_service.service.event.filters.EventTitleFilter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
  EventDto eventDto;

  @Mock
  private EventRepository eventRepository;
  @Mock
  private EventMapper eventMapper;
  @Mock
  private SkillRepository skillRepository;

  @Mock
  private List<EventFilter> eventFilters;

  private EventService eventService;

  private Skill userSkill = new Skill();


  @BeforeEach
  public void init() {
    EventFilter eventTitleFilter = new EventTitleFilter();
    List<EventFilter> eventFilterList = List.of(eventTitleFilter);
    eventService = new EventService(eventRepository, skillRepository, eventMapper, eventFilterList);

    userSkill.setTitle("Coding");
    userSkill.setId(1L);
    eventDto = new EventDto(1L, "Hiring", LocalDateTime.now(), LocalDateTime.now(),
        1L, "Hiring event", List.of(1L), "USA", 5);
  }

  @Test
  public void testCreateEvent() {
    Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(new Event());

    eventService.create(eventDto);
    Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEntity(eventDto));
  }

  @Test
  public void testSkillsValidation() {
    Skill mockedSkill = new Skill();
    mockedSkill.setTitle("Running");

    Mockito.when(skillRepository.findSkillsByGoalId(1L)).thenReturn(List.of(mockedSkill));

    assertThrows(DataValidationException.class, () -> {
      eventService.create(eventDto);
    });
  }

  @Test
  public void testGetEventByIdSuccess() {
    Long anyId = 1L;
    Event mockEvent = new Event();
    mockEvent.setTitle("Mock");

    Mockito.lenient().when(eventRepository.findById(anyId)).thenReturn(Optional.of(mockEvent));
    try {
      eventService.get(anyId);
      Mockito.verify(eventRepository, Mockito.times(1)).findById(anyId);
    } catch (Exception e) {}
  }

  @Test
  public void testGetEventByIdFail() {
    Long anyId = 1L;
    Mockito.lenient().when(skillRepository.findById(anyId)).thenReturn(null);

    assertThrows(EntityNotFoundException.class, () -> {
      eventService.get(anyId);
    });
  }


  @Test
  public void testGetAllUserParticipationEvents() {
    long anyUserId = 1L;
    Mockito.lenient().when(eventRepository.findParticipatedEventsByUserId(anyUserId)).thenReturn(List.of(new Event(), new Event(), new Event()));
    List<EventDto> events = eventService.getParticipatedEvents(anyUserId);
    Assertions.assertEquals(3, events.size());
  }

  @Test
  public void testEventDeleting() {
    Long anyTestId = 1L;
    eventService.delete(anyTestId);
    Mockito.verify(eventRepository, Mockito.times(1)).deleteById(anyTestId);
  }

  @Test
  public void testGetAllUserEvents() {
    long anyUserId = 1L;
    Mockito.lenient().when(eventRepository.findAllByUserId(anyUserId)).thenReturn(List.of(new Event(), new Event()));
    List<EventDto> events = eventService.getOwnedEvents(anyUserId);
    Assertions.assertEquals(2, events.size());
  }

  @Test void testGetAllUserEventsByTitleFilter() {
    Event javaEvent = new Event();
    javaEvent.setTitle("Java");

    Event pythonEvent = new Event();
    pythonEvent.setTitle("Python");

    Event jsEvent = new Event();
    jsEvent.setTitle("JavaScript");

    Mockito.lenient().when(eventRepository.findAll()).thenReturn(List.of(javaEvent, pythonEvent, jsEvent));

    EventFilterDto eventFilterDto = new EventFilterDto();

    eventFilterDto.setTitle("Jav");

    List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);

    Assertions.assertEquals(2, events.size());
  }

  @Test void testGetAllUserEventsByStartDateFilter() {
    List<EventFilter> eventFilterList = List.of(new EventStartDateFilter());
    eventService = new EventService(eventRepository, skillRepository, eventMapper, eventFilterList);

    Event javaEvent = new Event();
    javaEvent.setTitle("Java");
    javaEvent.setStartDate(LocalDateTime.of(2015,
        Month.JULY, 29, 19, 0, 0));

    Event pythonEvent = new Event();
    pythonEvent.setTitle("Python");
    pythonEvent.setStartDate(LocalDateTime.of(2018,
        Month.JULY, 29, 19, 0, 0));

    Event jsEvent = new Event();
    jsEvent.setTitle("JavaScript");
    jsEvent.setStartDate(LocalDateTime.of(2020,
        Month.JULY, 29, 19, 0, 0));

    Mockito.lenient().when(eventRepository.findAll()).thenReturn(List.of(javaEvent, pythonEvent, jsEvent));

    EventFilterDto eventFilterDto = new EventFilterDto();

    eventFilterDto.setStartDate(LocalDateTime.of(2018, Month.JANUARY, 8, 0, 0));

    List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);

    Assertions.assertEquals(2, events.size());
  }

  @Test void testGetAllUserEventsByEndDateFilter() {
    List<EventFilter> eventFilterList = List.of(new EventEndDateFilter());
    eventService = new EventService(eventRepository, skillRepository, eventMapper, eventFilterList);

    Event javaEvent = new Event();
    javaEvent.setTitle("Java");
    javaEvent.setEndDate(LocalDateTime.of(2015,
        Month.JULY, 29, 19, 0, 0));

    Event pythonEvent = new Event();
    pythonEvent.setTitle("Python");
    pythonEvent.setEndDate(LocalDateTime.of(2018,
        Month.JULY, 29, 19, 0, 0));

    Event jsEvent = new Event();
    jsEvent.setTitle("JavaScript");
    jsEvent.setEndDate(LocalDateTime.of(2020,
        Month.JULY, 29, 19, 0, 0));

    Mockito.lenient().when(eventRepository.findAll()).thenReturn(List.of(javaEvent, pythonEvent, jsEvent));

    EventFilterDto eventFilterDto = new EventFilterDto();

    eventFilterDto.setEndDate(LocalDateTime.of(2019, Month.JANUARY, 8, 0, 0));

    List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);

    Assertions.assertEquals(2, events.size());
  }
}