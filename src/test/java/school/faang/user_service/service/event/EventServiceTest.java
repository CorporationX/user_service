package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.testData.TestData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.message.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.NO_SUCH_EVENT_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private SkillService skillService;
    @Mock
    private UserService userService;
    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private Event event;
    private List<EventFilter> filters;

    @BeforeEach
    void setUp() {
        TestData testData = new TestData();

        eventDto = testData.getEventDto();
        event = testData.getEvent();


        EventFilter filterA = mock(EventFilter.class);
        EventFilter filterB = mock(EventFilter.class);
        EventFilter filterC = mock(EventFilter.class);

        filters = List.of(filterA, filterB, filterC);
        eventService.setFilters(filters);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should create event when owner has required skills")
        @Test
        void shouldCreateEventWhenOwnerHasRequiredSkills() {
            eventDto.setRelatedSkills(List.of());

            when(skillService.getUserSkills(anyLong())).thenReturn(List.of());
            when(userService.getUserEntity(anyLong())).thenReturn(new User());
            when(eventMapper.toEntity(eventDto)).thenReturn(event);

            assertDoesNotThrow(() -> eventService.create(eventDto));

            verify(eventRepository).save(event);
        }

        @DisplayName("should get event when such event exists")
        @Test
        void shouldGetEventWhenItExists() {
            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
            when(eventMapper.toDto(event)).thenReturn(new EventDto());

            assertDoesNotThrow(() -> eventService.getEvent(anyLong()));

            verify(eventRepository).findById(anyLong());
            verify(eventMapper).toDto(event);
        }

        @DisplayName("should return filtered events when filter isn't empty")
        @Test
        void shouldReturnFilteredEventsWhenFilterIsntEmpty() {
            EventFilterDto eventFilterDto = new EventFilterDto();
            when(eventRepository.findAll()).thenReturn(List.of(event));
            when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

            filters.forEach(filter -> {
                when(filter.isApplicable(eventFilterDto)).thenReturn(true);
                when(filter.apply(any(List.class), eq(eventFilterDto))).thenReturn(Stream.of(event));
            });


            var filteredEvents = eventService.getEventsByFilter(eventFilterDto);


            verify(eventRepository).findAll();

            filters.forEach(filter -> {
                verify(filter).isApplicable(eventFilterDto);
                verify(filter).apply(any(List.class), eq(eventFilterDto));
            });
            verify(eventMapper).toDto(any(Event.class));
            assertEquals(List.of(eventDto), filteredEvents);
        }

        @DisplayName("should delete event when such event exists")
        @Test
        void shouldDeleteEventWhenItExists() {
            doNothing().when(eventRepository).deleteById(anyLong());
            assertDoesNotThrow(() -> eventService.deleteEvent(anyLong()));
        }

        @DisplayName("should update event when passed event exists and owner has required skills")
        @Test
        void shouldUpdateEventWhenDtoIsValid() {
            eventDto.setRelatedSkills(List.of());

            when(skillService.getUserSkills(anyLong())).thenReturn(List.of());
            when(eventRepository.existsById(eventDto.getId())).thenReturn(true);
            when(eventMapper.toEntity(eventDto)).thenReturn(event);

            assertDoesNotThrow(() -> eventService.updateEvent(eventDto));

            verify(eventRepository).save(event);
        }

        @DisplayName("should return owned by user events")
        @Test
        void shouldReturnOwnedEvents() {
            eventService.getOwnedEvents(anyLong());

            verify(eventRepository).findAllByUserId(anyLong());
            verify(eventMapper).toDtoList(any(List.class));
        }

        @DisplayName("should return participated by user events")
        @Test
        void shouldReturnParticipatedEvents() {
            eventService.getParticipatedEvents(anyLong());

            verify(eventRepository).findParticipatedEventsByUserId(anyLong());
            verify(eventMapper).toDtoList(any(List.class));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when owner doesn't have required skills during event creation")
        @Test
        void shouldThrowExceptionWhenOwnersSkillsDoesntMatchRequiredDuringEventCreation() {
            when(skillService.getUserSkills(eventDto.getOwnerId())).thenReturn(List.of(new SkillDto()));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.create(eventDto));

            verify(eventRepository, times(0)).save(event);
            assertEquals(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when such event doesn't exist")
        @Test
        void shouldThrowExceptionWhenSuchEventDoesntExist() {
            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class, () -> eventService.getEvent(anyLong()));

            verify(eventMapper, times(0)).toDto(any());
            assertEquals(NO_SUCH_EVENT_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should return empty event list when filter is empty")
        @Test
        void shouldReturnEmptyListWhenFilterIsEmpty() {
            EventFilterDto eventFilterDto = new EventFilterDto();
            when(eventRepository.findAll()).thenReturn(List.of(event));


            var filteredEvents = eventService.getEventsByFilter(eventFilterDto);


            verify(eventRepository).findAll();

            filters.forEach(filter -> {
                verify(filter).isApplicable(eventFilterDto);
                verify(filter, times(0)).apply(any(List.class), eq(eventFilterDto));
            });
            verify(eventMapper, times(0)).toDto(any(Event.class));
            assertEquals(List.of(), filteredEvents);
        }

        @DisplayName("should throw exception when event to be updated doesn't exist")
        @Test
        void shouldThrowExceptionWhenEventToBeUpdatedDoesntExist() {
            when(eventRepository.existsById(eventDto.getId())).thenReturn(false);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.updateEvent(eventDto));

            verify(eventRepository, times(0)).save(any());
            assertEquals(NO_SUCH_EVENT_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when owner of event to be updated doesn't have required skills")
        @Test
        void shouldThrowExceptionWhenOwnerOfEventToBeUpdatedDoesntHaveRequiredSkills() {
            when(skillService.getUserSkills(eventDto.getOwnerId())).thenReturn(List.of(new SkillDto()));
            when(eventRepository.existsById(eventDto.getId())).thenReturn(true);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.updateEvent(eventDto));

            verify(eventRepository, times(0)).save(any());
            assertEquals(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}