package school.faang.user_service.service.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    EventMapper eventMapper;

    @Mock
    SkillRepository skillRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
    }

    //positive test

    @Test
    void testCreateForValidArgs() {
        //Arrange
        SkillDto skillDto1 = new SkillDto();
        skillDto1.setId(1L);
        skillDto1.setTitle("skill1");
        skillDto1.setCreatedAt(LocalDateTime.now());

        SkillDto skillDto2 = new SkillDto();
        skillDto2.setId(2L);
        skillDto2.setTitle("skill2");
        skillDto2.setCreatedAt(LocalDateTime.now());

        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Новое событие");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.of(2024, 10, 1, 12, 0));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("какое-то описание");
        eventDto.setRelatedSkills(List.of(skillDto1, skillDto2));
        eventDto.setLocation("location");
        eventDto.setMaxAttendees(5);
        //Act & Assert
        eventService.create(eventDto);
        //verify(eventService, times(1)).create(eventDto);
    }

    //negative test

    @Test
    void testInvalidationArgs() {
        //Arrange
        EventDto eventDto = new EventDto();
        eventDto.setId(null);
        eventDto.setTitle("");
        eventDto.setStartDate(null);
        //Act & Assert
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }



    @AfterEach
    void tearDown() {
    }

    @Test
    void create() {
    }

    @Test
    void getEvent() {
    }

    @Test
    void getEventsByFilter() {
    }

    @Test
    void deleteEvent() {
    }

    @Test
    void updateEvent() {
    }

    @Test
    void getOwnedEvents() {
    }

    @Test
    void getParticipatedEvents() {
    }

    @Test
    void getEventRepository() {
    }

    @Test
    void getEventMapper() {
    }

    @Test
    void getSkillRepository() {
    }
}