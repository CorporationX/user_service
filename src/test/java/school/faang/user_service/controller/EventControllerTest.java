package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @Mock
    private EventService eventService;
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventController eventController;
    private EventDto eventDto = EventDto.builder()
            .title("Hello BootyCamp")
            .startDate(LocalDateTime.MAX)
            .ownerId(1L)
            .build();
    private EventDto eventDto1 = EventDto.builder()
            .startDate(LocalDateTime.MAX)
            .ownerId(1L)
            .build();
    private EventDto eventDto2 = EventDto.builder()
            .title(" ")
            .startDate(LocalDateTime.MAX)
            .ownerId(1L)
            .build();
    private EventDto eventDto3 = EventDto.builder()
            .title("Hello BoottyCamp")
            .ownerId(1L)
            .build();
    private EventDto eventDto4 = EventDto.builder()
            .title("Hello BoottyCamp")
            .startDate(LocalDateTime.MAX)
            .build();
    private EventDto eventDto5;
    private Event event = Event.builder()
            .title("Hello BoottyCamp")
            .startDate(LocalDateTime.MAX)
            .owner(User.builder().build())
            .build();

    @Test
    public void testSuccessfulEventCreating() {
        eventController.create(eventDto);
        Mockito.verify(eventService, Mockito.times(1)).create(eventDto);
    }

    @Test
    public void testEventCreateTitleIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.create(eventDto1));
    }

    @Test
    public void testEventCreateTitleIsBlank() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.create(eventDto2));
    }

    @Test
    public void testEventCreateStartDateIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.create(eventDto3));
    }

    @Test
    public void testEventCreateOwnerIDIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.create(eventDto4));
    }

    @Test
    public void testEventCreateEventIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.create(eventDto5));
    }

    @Test
    public void testGetEvent() {
        eventController.getEvent(1);
        Mockito.verify(eventService, Mockito.times(1)).getEvent(1);
    }

    @Test
    public void testDeleteEvent() {
        eventController.deleteEvent(1L);
        Mockito.verify(eventService, Mockito.times(1)).deleteEvent(1L);
    }

    @Test
    public void testSuccessfulUpdateEvent() {
        eventController.updateEvent(eventDto);
        Mockito.verify(eventService, Mockito.times(1)).updateEvent(eventDto);
    }

    @Test
    public void testUpdateEventTitleIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.updateEvent(eventDto1));
    }

    @Test
    public void testUpdateEventTitleIsBlank() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.updateEvent(eventDto2));
    }

    @Test
    public void testUpdateEventStartDateIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.updateEvent(eventDto3));
    }

    @Test
    public void testUpdateEventOwnerIDIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.updateEvent(eventDto4));
    }

    @Test
    public void testUpdateEventEventIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> eventController.updateEvent(eventDto5));
    }

}
