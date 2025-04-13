package school.faang.user_service.event;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RedisUserBanListenerTest {
    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private RedisUserBanListener redisUserBanListener;

    @Test
    public void onMessageTest() {
        redisUserBanListener.onMessage(getMessage(), new byte[0]);

        verify(userService, times(1)).banUser(1L);
    }

    private Message getMessage() {
        return new Message() {
            @Override
            public byte @NotNull [] getBody() {
                return String.valueOf(1L).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public byte @NotNull [] getChannel() {
                return new byte[0];
            }
        };
    }
}
