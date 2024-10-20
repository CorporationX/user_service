package school.faang.user_service.listener.ban;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.event.ban.UserBanEvent;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserBanEventListenerTest {

    private static final Long USER_ID = 123L;

    @Mock
    private Message message;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserBanEventListener userBanEventListener;

    private UserBanEvent userBanEvent;

    @BeforeEach
    void setUp() {
        userBanEvent = new UserBanEvent(USER_ID);
    }

    @Test
    @DisplayName("Should ban user when UserBanEvent is received")
    public void shouldBanUserOnUserBanEvent() throws IOException {
        when(objectMapper.readValue(message.getBody(), UserBanEvent.class)).thenReturn(userBanEvent);

        userBanEventListener.onMessage(message, null);

        verify(userService).banUser(USER_ID);
    }
}
