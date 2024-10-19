package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.model.dto.EventDto;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Event;
import school.faang.user_service.model.entity.Promotion;
import school.faang.user_service.filter.event.EventMaxAttendeesFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EventRepository;
import school.faang.user_service.service.impl.EventServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    private List<EventFilter> eventFilters;

    @Mock
    PromotionRepository promotionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Mock
    private EventTitleFilter eventTitleFilter;

    @Mock
    private EventMaxAttendeesFilter eventMaxAttendeesFilter;

    @Mock
    private ExecutorService executorService;

    @Mock
    private EventStartEventPublisher eventPublisher;

    @InjectMocks
    private EventServiceImpl eventService;

    private List<Event> events;

    @BeforeEach
    public void setUp() {
        eventFilters = new ArrayList<>();
        eventFilters.add(eventTitleFilter);
        eventFilters.add(eventMaxAttendeesFilter);
        eventService = new EventServiceImpl(eventRepository, userRepository, promotionRepository, eventFilters, eventMapper,eventPublisher);
        events = new ArrayList<>();
        eventService.setBatchSize(10);
    }

    @Test
    @DisplayName("Should return events in correct order when events are found and filtered")
    public void testGetFilteredEvents_FoundAndFiltered() {
        Country usa = new Country(1, "USA", List.of());
        Country uk = new Country(2, "UK", List.of());

        User callingUser = new User();
        callingUser.setId(1L);
        callingUser.setCountry(usa);

        User owner1 = new User();
        owner1.setId(2L);
        owner1.setCountry(uk);

        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Event 1");
        event1.setMaxAttendees(100);
        event1.setOwner(owner1);

        Promotion promotion1 = new Promotion();
        promotion1.setPromotionTarget("event");
        promotion1.setRemainingShows(5);
        promotion1.setPriorityLevel(3);

        owner1.setPromotions(new ArrayList<>(List.of(promotion1)));

        User owner2 = new User();
        owner2.setId(3L);
        owner2.setCountry(usa);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Event 2");
        event2.setMaxAttendees(200);
        event2.setOwner(owner2);

        Promotion promotion2 = new Promotion();
        promotion2.setPromotionTarget("event");
        promotion2.setRemainingShows(5);
        promotion2.setPriorityLevel(3);

        owner2.setPromotions(new ArrayList<>(List.of(promotion2)));

        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setTitlePattern("Event");
        filterDto.setMaxAttendees(100);

        List<Event> filteredEvents = List.of(event2, event1);

        Specification<Event> mockTitleSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%event%");

        when(eventFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(eventFilters.get(0).toSpecification(filterDto)).thenReturn(mockTitleSpecification);

        Specification<Event> mockMaxAttendeesSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("maxAttendees"), 100);

        when(eventFilters.get(1).isApplicable(filterDto)).thenReturn(true);
        when(eventFilters.get(1).toSpecification(filterDto)).thenReturn(mockMaxAttendeesSpecification);

        when(userRepository.findById(callingUser.getId())).thenReturn(Optional.of(callingUser));
        when(eventRepository.findAll(ArgumentMatchers.<Specification<Event>>any())).thenReturn(filteredEvents);

        List<EventDto> result = eventService.getFilteredEvents(filterDto, callingUser.getId());

        verify(eventMapper).toDto(event1);
        verify(eventMapper).toDto(event2);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(2L, result.get(0).getId(), "First event should be event2"),
                () -> assertEquals(1L, result.get(1).getId(), "Second event should be event1")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when calling user is not found")
    public void testGetFilteredEvents_CallingUserNotFound() {
        EventFilterDto filterDto = new EventFilterDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventService.getFilteredEvents(filterDto, 1L));
    }

    @Test
    void testClearPastEvents() throws InterruptedException {
        Event firstEvent = new Event();
        firstEvent.setId(1L);
        firstEvent.setEndDate(LocalDateTime.now().minusDays(1));

        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setEndDate(LocalDateTime.now().minusDays(2));

        Event thirdEvent = new Event();
        thirdEvent.setId(3L);
        thirdEvent.setEndDate(LocalDateTime.now().plusDays(1));

        events = Arrays.asList(firstEvent, secondEvent, thirdEvent);
        when(eventRepository.findAll()).thenReturn(events);
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocationOnMock ->{
            latch.countDown();
            return null;
        }).when(eventRepository).deleteAllByIdInBatch(Arrays.asList(1L, 2L));

        eventService.clearPastEvents();

        latch.await();

        verify(eventRepository, times(1)).deleteAllByIdInBatch(Arrays.asList(1L, 2L));
        verify(eventRepository, never()).deleteAllByIdInBatch(List.of(3L));
    }
}