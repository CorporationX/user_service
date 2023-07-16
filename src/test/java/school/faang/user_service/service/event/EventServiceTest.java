package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import java.time.LocalDateTime;
import java.util.List;

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

  @InjectMocks
  private EventService eventService;
  private Skill userSkill = new Skill();


  @BeforeEach
  public void init() {
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
}