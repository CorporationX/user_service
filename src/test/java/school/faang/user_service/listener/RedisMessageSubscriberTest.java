package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.dto.UserIdDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisMessageSubscriberTest {
    private static final Long AUTHOR_ID = 1L;

    private UserIdDto user;

    @BeforeEach
    void setUp() {
        user = new UserIdDto();
        user.setId(AUTHOR_ID);
    }

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Message message;

    @InjectMocks
    private RedisMessageSubscriber redisMessageSubscriber;

    @Test
    void testOnMessage() throws IOException {
        String json = "{\"authorId\":1";

        when(message.getBody()).thenReturn(json.getBytes());
        when(objectMapper.readValue(any(byte[].class), eq(UserIdDto.class))).thenReturn(user);

        redisMessageSubscriber.onMessage(message, null);

        verify(userService, times(1)).banUser(user.getId());
    }

    @Test
    void testOnMessageWithIOException() throws IOException {
        String json = "{\"authorId\":1";

        when(message.getBody()).thenReturn(json.getBytes());
        when(objectMapper.readValue(any(byte[].class), eq(UserIdDto.class))).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> redisMessageSubscriber.onMessage(message, null));
    }
}
