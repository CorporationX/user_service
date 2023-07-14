package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
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

  @InjectMocks
  private EventService eventService;

  private SkillDto eventSkill = new SkillDto(1L, "Coding");
  private Skill userSkill = new Skill();


  @BeforeEach
  public void init() {
    userSkill.setTitle("Coding");
    eventDto = new EventDto(1L, "Hiring", LocalDateTime.now(), LocalDateTime.now(),
        1L, "Hiring event", List.of(eventSkill), "USA", 5);
  }

  @Test
  public void testCreateEvent() {
    Mockito.when(skillRepository.findSkillsByGoalId(1L)).thenReturn(List.of(userSkill));
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

    assertThrows(Exception.class, () -> {
      eventService.get(anyId);
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

    assertThrows(Exception.class, () -> {
      eventService.get(anyId);
    });
  }
}