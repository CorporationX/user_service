package school.faang.user_service.service.event.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.GoogleCalendarConfig;
import school.faang.user_service.dto.EventCalendarDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MapperCalendar;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CalendarService {
    private final GoogleCalendarConfig calendarConfig;
    private final MapperCalendar calendarMapper;
    private final EventParticipationRepository participationRepository;

    public EventCalendarDto createEvent(EventCalendarDto eventCalendarDto) throws IOException, GeneralSecurityException {
        Calendar service = calendarConfig.getCalendar();

        Event event = new Event()
                .setSummary(eventCalendarDto.getSummary())
                .setLocation(eventCalendarDto.getLocation())
                .setDescription(eventCalendarDto.getDescription());

        Event.Creator creator = new Event.Creator()
                .setId(String.valueOf(eventCalendarDto.getAuthorId()));
        event.setCreator(creator);

        DateTime startDateTime = new DateTime(String.valueOf(eventCalendarDto.getStartDateTime()));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(String.valueOf(eventCalendarDto.getTimeZone()));
        event.setStart(start);

        DateTime endDateTime = new DateTime(String.valueOf(eventCalendarDto.getEndDateTime()));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(String.valueOf(eventCalendarDto.getTimeZone()));
        event.setEnd(end);

        List<Integer> participantsIds = eventCalendarDto.getParticipantsIds();
        List<EventAttendee> list = new ArrayList<>();
        for (Integer participantsId : participantsIds) {
            User user = participationRepository.eventParticipantsById(participantsId);
            list.add(new EventAttendee().setEmail(user.getEmail()));
        }
        event.setAttendees(list);

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10)
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        service.events().insert("primary", event).execute();
        return calendarMapper.toDto(event);
    }
}

