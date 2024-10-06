package school.faang.user_service.publis.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBanListenerTest {
    @Mock
    private UserService userService;
    @Mock
    private Message message;
    @InjectMocks
    private UserBanListener userBanListener;


    @Test
    public void testOnMessage_Success() {
        String messageBody = "[1, 2, 3]";
        byte[] pattern = {};

        when(message.getBody()).thenReturn(messageBody.getBytes());

        userBanListener.onMessage(message, pattern);

        verify(userService, atLeastOnce()).banUser(1L);
        verify(userService, atLeastOnce()).banUser(2L);
        verify(userService, atLeastOnce()).banUser(3L);
        verify(userService, times(3)).banUser(any(Long.class));
        verify(userService, never()).banUser(4L);
    }
}
