package school.faang.user_service.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoogleCalendarEventDto;
import com.google.api.services.calendar.model.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoogleCalendarMapper;
import school.faang.user_service.repository.event.EventRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final Calendar calendar;
    private final EventRepository eventRepository;
    private final GoogleCalendarMapper calendarMapper;

    public ResponseEntity<?> createEvent(Long id) throws IOException {
        school.faang.user_service.entity.event.Event newEvent = eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Event not found"));
        GoogleCalendarEventDto eventDto = calendarMapper.toDto(newEvent);

        com.google.api.services.calendar.model.Event event = createEvent(eventDto);
        calendar.events().insert("primary", event).execute();

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Event createEvent(GoogleCalendarEventDto eventDto) {
        Event event = new Event();
        event.setDescription(eventDto.getDescription());
        event.setSummary(eventDto.getTitle());
        event.setLocation(eventDto.getLocation());

        LocalDateTime localDateTime = eventDto.getStartDate();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dataStart = new DateTime(zonedDateTime.toInstant().toEpochMilli());

        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(dataStart);
        event.setStart(eventDateTime);

        LocalDateTime localDateTime1 = eventDto.getEndDate();
        ZonedDateTime zonedDateTime1 = localDateTime1.atZone(ZoneId.systemDefault());
        DateTime dataEnd = new DateTime(zonedDateTime1.toInstant().toEpochMilli());

        EventDateTime eventDateTime2 = new EventDateTime()
                .setDateTime(dataEnd);
        event.setEnd(eventDateTime2);
        return event;
    }
}
