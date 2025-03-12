package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    public static final String USER_NOT_FOUND = "User not found";
    public static final String EVENT_NOT_FOUND = "Event not found";

    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private EventValidator eventValidator;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    @DisplayName("Registration error - Event not found")
    public void testRegisterParticipantEventNotFound() {
        validationData(EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Registration error - User not found")
    public void testRegisterParticipantUserNotFound() {
        validationData(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("Registration error - User already for event")
    public void testRegisterParticipantAlreadyRegisteredForEvent() {
        Long eventId = 2L;
        Long userId = 1L;
        String errorMessage = "User is already registered for the event";
        User user = new User();
        user.setId(userId);

        validationSkip(eventId, userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

        assertEquals(errorMessage, exception.getMessage());
        verify(eventParticipationRepository, never()).register(eventId, userId);
    }

    @Test
    @DisplayName("Successful registration")
    public void testRegisterParticipantSuccessful() {
        Long eventId = 2L;
        Long userId = 1L;

        validationSkip(eventId, userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());
        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    @DisplayName("Unregister error - Event not found")
    public void testUnregisterParticipantEventNotFound() {
        validationData(EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Unregister error - Event not found")
    public void testUnregisterParticipantUserNotFound() {
        validationData(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("Unregister error - User already for event")
    public void testUnregisterParticipantNotRegisteredForEvent() {
        Long eventId = 2L;
        Long userId = 1L;
        String errorMessage = "User is not registered for the event";

        validationSkip(eventId, userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));

        assertEquals(errorMessage, exception.getMessage());
        verify(eventParticipationRepository, never()).register(eventId, userId);
    }

    @Test
    @DisplayName("Successful unregister")
    public void testUnregisterSuccessful() {
        Long eventId = 2L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        validationSkip(eventId, userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    @DisplayName("Error get participant -  Event not found")
    public void testGetParticipantEventNotFound() {
        validationData(EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Get participant successful")
    public void testGetParticipantSuccessful() {
        Long eventId = 1L;
        List<User> users = createListUser();
        List<UserDto> userDtos = createListUserDto();

        doNothing().when(eventValidator).checkEventExistsById(eventId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);
        List<UserDto> participants = eventParticipationService.getParticipant(eventId);
        ArgumentCaptor<List<User>> captor = ArgumentCaptor.forClass(List.class);

        verify(userMapper, times(1)).toListUserDto(captor.capture());
        verify(eventValidator, times(1)).checkEventExistsById(eventId);
        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);

        assertEquals(userDtos.get(0).getUsername(), participants.get(0).getUsername());
        assertEquals(userDtos.get(0).getEmail(), participants.get(0).getEmail());
        assertEquals(userDtos.size(), participants.size());
        assertEquals(userDtos.get(0).getId(), participants.get(0).getId());
        assertEquals(users.get(0).getEmail(), captor.getValue().get(0).getEmail());
    }

    @Test
    @DisplayName("Error get participant count - Event not found")
    public void testGetParticipantCountEventNotFound() {
        validationData(EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Get participant count successful")
    public void testGetParticipantCountSuccessful() {
        Long eventId = 1L;
        Integer expectedCount = 1;

        doNothing().when(eventValidator).checkEventExistsById(eventId);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);
        Integer participantCount = eventParticipationService.getParticipantCount(eventId);

        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
        verify(eventValidator, times(1)).checkEventExistsById(eventId);
        assertEquals(expectedCount, participantCount);

    }

    private List<UserDto> createListUserDto() {
        UserDto alexDto = UserDto.builder()
                .id(2L)
                .username("Alex")
                .email("alex@mail.ru")
                .build();

        return List.of(alexDto);
    }

    private List<User> createListUser() {
        User alex = User.builder()
                .id(2L)
                .username("Alex")
                .email("alex@mail.ru")
                .build();

        return List.of(alex);
    }

    private void validationSkip(Long eventId, Long userId) {
        doNothing().when(eventValidator).checkEventExistsById(eventId);
        doNothing().when(userValidator).checkUserExistsById(userId);
    }

    private void validationData(String errorMessage) {
        doThrow(new EntityNotFoundException(errorMessage))
                .when(eventValidator).checkEventExistsById(anyLong());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> eventValidator.checkEventExistsById(anyLong())
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(eventParticipationRepository, never()).register(anyLong(), anyLong());
    }
}
