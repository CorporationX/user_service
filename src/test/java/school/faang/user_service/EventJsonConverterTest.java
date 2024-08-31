package school.faang.user_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.events.MentorshipOfferedEvent;
import school.faang.user_service.publishers.EventJsonConverter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class EventJsonConverterTest {
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private EventJsonConverter<Object> eventJsonConverter;

    @Test
    public void testConvertToJsonSuccess() {
        MentorshipRequest r = new MentorshipRequest();
        MentorshipOfferedEvent event = new MentorshipOfferedEvent();
        event.setAuthorId(1L);
        event.setMentorId(2L);
        event.setRequestId(3L);
        String str = "{\"authorId\":1,\"mentorId\":2,\"requestId\":3}";
        String json = eventJsonConverter.toJson(event);
        assertEquals(str, json);
    }
}
