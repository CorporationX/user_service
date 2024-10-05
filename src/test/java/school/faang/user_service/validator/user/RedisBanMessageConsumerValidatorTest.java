package school.faang.user_service.validator.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisBanMessageConsumerValidatorTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserIdsSubscriberValidator userIdsSubscriberValidator;

    @Test
    public void testValidateId_UserExists() {
        // Given
        Long existingUserId = 1L;
        when(userRepository.existsById(existingUserId)).thenReturn(true);

        // When
        userIdsSubscriberValidator.validateId(existingUserId);

        // Then
        verify(userRepository, times(1)).existsById(existingUserId);
    }

    @Test
    public void testValidateId_UserDoesNotExist() {
        // Given
        Long nonExistingUserId = 2L;
        when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        // When & Then
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            userIdsSubscriberValidator.validateId(nonExistingUserId);
        });
        assertEquals(String.format("User with id '%s' does not exist", nonExistingUserId), exception.getMessage());
        verify(userRepository, times(1)).existsById(nonExistingUserId);
    }

    @Test
    public void testParseUserIds_WithList() {
        // Given
        List<Long> userIds = List.of(1L, 2L, 3L);

        // When
        List<Long> result = userIdsSubscriberValidator.parseUserIds(userIds);

        // Then
        assertEquals(userIds, result);
    }

    @Test
    public void testParseUserIds_UnexpectedType() {
        // Given
        Object unexpectedMessage = new Object();

        // When
        List<Long> result = userIdsSubscriberValidator.parseUserIds(unexpectedMessage);

        // Then
        assertEquals(null, result);
    }
}
