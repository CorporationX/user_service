package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.service.UserLifeCycleService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBanListenerTest {

    @InjectMocks
    private UserBanListener userBanListener;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserLifeCycleService userLifeCycleService;

    @Mock
    private Message message;

    @Test
    void onMessage_shouldBanUsers_whenValidJson() throws JsonProcessingException {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        String json = "[1, 2, 3]";
        when(message.getBody()).thenReturn(json.getBytes());
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(userIds);

        userBanListener.onMessage(message, null);

        verify(userLifeCycleService).banUsersById(userIds);
    }
}