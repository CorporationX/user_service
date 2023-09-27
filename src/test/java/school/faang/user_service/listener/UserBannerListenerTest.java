package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserBannerListenerTest {

    @InjectMocks
    private UserBannerListener userBannerListener;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    private Message message;
    private Long userId;

    @BeforeEach
    void setUp() throws IOException {
        userId = 1L;
        message = mock(Message.class);
        byte[] body = new byte[0];

        when(message.getBody()).thenReturn(body);
        when(objectMapper.readValue(message.getBody(), Long.class))
                .thenReturn(userId);
    }

    @Test
    void onMessage_shouldInvokeObjectMapperReadValueMethod() throws IOException {
        userBannerListener.onMessage(message, new byte[0]);
        verify(objectMapper).readValue(message.getBody(), Long.class);
        verify(userService).setBanForUser(userId);
    }
}