package school.faang.user_service.validator.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    @DisplayName("Exists event by id - event not found")
    public void testCheckExistsByIdEventNotFound() {
        String errorMessage = "Event not found";

        when(eventRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> eventValidator.checkEventExistsById(anyLong()));
        verify(eventRepository, times(1)).existsById(anyLong());
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Exists user by id - Successful")
    public void testCheckEventExistsById() {
        when(eventRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> eventValidator.checkEventExistsById(anyLong()));
        verify(eventRepository, times(1)).existsById(anyLong());
    }
}
