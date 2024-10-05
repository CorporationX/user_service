package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.consumer.RedisBanMessageListener;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserIdsSubscriberValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisBanMessageConsumerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserIdsSubscriberValidator userIdsSubscriberValidator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisBanMessageListener redisBanMessageConsumer;

    @Test
    public void testHandleMessage_ValidUserIds() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setBanned(false);
        List<Long> userIds = List.of(userId);

        // Mocking the behavior of the validator
        when(userIdsSubscriberValidator.parseUserIds(userIds)).thenReturn(userIds);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userIdsSubscriberValidator).validateId(userId);

        // When
        redisBanMessageConsumer.handleMessage(userIds);

        // Then
        verify(userIdsSubscriberValidator, times(1)).parseUserIds(userIds);
        verify(userIdsSubscriberValidator, times(1)).validateId(userId);
        verify(userRepository, times(1)).save(user);
        assert(user.isBanned());
    }

    @Test
    public void testHandleMessage_InvalidUserId() {
        // Given
        Long invalidUserId = 99L;
        List<Long> userIds = List.of(invalidUserId);

        // Mocking the behavior of the validator
        when(userIdsSubscriberValidator.parseUserIds(userIds)).thenReturn(userIds);
        doThrow(new DataValidationException("User not found")).when(userRepository).findById(invalidUserId);

        // When & Then
        assertThrows(DataValidationException.class, () -> {
            redisBanMessageConsumer.handleMessage(userIds);
        });

        verify(userIdsSubscriberValidator, times(1)).parseUserIds(userIds);
        verify(userIdsSubscriberValidator, times(1)).validateId(invalidUserId);
    }

    @Test
    public void testHandleMessage_InvalidMessageType() {
        // Given
        Object invalidMessage = new Object();

        // When
        redisBanMessageConsumer.handleMessage(invalidMessage);

        // Then
        verify(userIdsSubscriberValidator, times(1)).parseUserIds(invalidMessage);
        // No further interactions with the repository should occur
        verify(userRepository, times(0)).findById(anyLong());
    }
}
