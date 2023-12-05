package school.faang.user_service.service.event.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.GoogleCalendarConfig;
import school.faang.user_service.dto.EventCalendarDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.mapper.CalendarMapper;
import school.faang.user_service.service.UserService;

import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private final GoogleCalendarConfig calendarConfig;
    private final CalendarMapper calendarMapper;
    private final UserService userService;
    @Value("${google}")
    private final String PRIMARY;

    public EventCalendarDto createEvent(EventCalendarDto eventCalendarDto) {
        Calendar service = getCalendar();
        Event event = getEvent(eventCalendarDto);

        setEventCreator(eventCalendarDto, event);

        DateTime startDateTime = eventCalendarDto.getStartDateTime();
        DateTime endDateTime = eventCalendarDto.getEndDateTime();
        setDataTimeAndTimeZone(eventCalendarDto, event, startDateTime, endDateTime);

        setAttendees(eventCalendarDto, event);

        setEventReminder(event);

        executeEvent(service, event);
        log.info("Event was created {}", event);
        return calendarMapper.toDto(event);
    }

    private void executeEvent(Calendar service, Event event) {
        try {
            service.events().insert(PRIMARY, event).execute();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Calendar getCalendar() {
        Calendar service;
        try {
            service = calendarConfig.googleCalendar();
        } catch (IOException | GeneralSecurityException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return service;
    }

    private void setEventReminder(Event event) {
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10)
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
    }

    private void setAttendees(EventCalendarDto eventCalendarDto, Event event) {
        List<Integer> participantsIds = eventCalendarDto.getParticipantsIds();
        List<EventAttendee> list = new ArrayList<>();
        for (Integer participantsId : participantsIds) {
            UserDto user = userService.getUser(participantsId);
            list.add(new EventAttendee().setEmail(user.email()));
        }
        event.setAttendees(list);
    }

    private void setDataTimeAndTimeZone(EventCalendarDto eventCalendarDto, Event event, DateTime startDateTime, DateTime endDateTime) {
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(String.valueOf(eventCalendarDto.getTimeZone()));
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(String.valueOf(eventCalendarDto.getTimeZone()));
        event.setEnd(end);
    }


    private void setEventCreator(EventCalendarDto eventCalendarDto, Event event) {
        Event.Creator creator = new Event.Creator()
                .setId(String.valueOf(eventCalendarDto.getAuthorId()));
        event.setCreator(creator);
    }

    private Event getEvent(EventCalendarDto eventCalendarDto) {
        Event event = new Event()
                .setSummary(eventCalendarDto.getSummary())
                .setLocation(eventCalendarDto.getLocation())
                .setDescription(eventCalendarDto.getDescription());
        return event;
    }
}

