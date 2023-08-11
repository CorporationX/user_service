package school.faang.user_service.mapper.event;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.GoogleEventDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;


class GoogleEventMapperTest {

    private GoogleEventDtoMapper googleEventMapper = new GoogleEventDtoMapperImpl();

    @Test
    void testMapToGoogleEvent() {
        LocalDateTime localDateTimeStart = LocalDateTime.of(2023, 8, 4, 10, 30);
        LocalDateTime localDateTimeEnd = LocalDateTime.of(2023, 8, 4, 11, 30);
        school.faang.user_service.entity.event.Event newEvent = school.faang.user_service.entity.event.Event.builder()
                .startDate(localDateTimeStart)
                .endDate(localDateTimeEnd)
                .title("New")
                .location("LA")
                .description("Description")
                .build();

        EventDateTime eventDateTimeStart = new EventDateTime();
        ZonedDateTime zonedDateTime = localDateTimeStart.atZone(ZoneId.systemDefault());
        DateTime dataEnd = new DateTime(zonedDateTime.toInstant().toEpochMilli());
        eventDateTimeStart.setDateTime(dataEnd);

        EventDateTime eventDateTimeEnd = new EventDateTime();
        ZonedDateTime zonedDateTime1 = localDateTimeEnd.atZone(ZoneId.systemDefault());
        DateTime dataEnd1 = new DateTime(zonedDateTime1.toInstant().toEpochMilli());
        eventDateTimeEnd.setDateTime(dataEnd1);

        GoogleEventDto event = new GoogleEventDto();
        event.setLocation("LA");
        event.setDescription("Description");
        event.setSummary("New");
        event.setStart(eventDateTimeStart);
        event.setEnd(eventDateTimeEnd);

        assertEquals(event.getDescription(), googleEventMapper.toGoogleEventDto(newEvent).getDescription());
        assertEquals(event.getSummary(), googleEventMapper.toGoogleEventDto(newEvent).getSummary());
        assertEquals(event.getEnd(), googleEventMapper.toGoogleEventDto(newEvent).getEnd());
        assertEquals(event.getStart(), googleEventMapper.toGoogleEventDto(newEvent).getStart());
        assertEquals(event.getLocation(), googleEventMapper.toGoogleEventDto(newEvent).getLocation());
        assertEquals(event, googleEventMapper.toGoogleEventDto(newEvent));
    }
}