package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
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
    public static final Long EVENT_ID = 2L;
    public static final Long USER_ID = 1L;

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
        String errorMessage = "User is already registered for the event";
        User user = new User();
        user.setId(USER_ID);

        validationSkip();
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));
        verify(eventValidator, times(1)).checkEventExistsById(EVENT_ID);
        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        assertEquals(errorMessage, exception.getMessage());
        verify(eventParticipationRepository, never()).register(EVENT_ID, USER_ID);
    }

    @Test
    @DisplayName("Successful registration")
    public void testRegisterParticipantSuccessful() {
        validationSkip();
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of());
        eventParticipationService.registerParticipant(EVENT_ID, USER_ID);

        verify(eventValidator, times(1)).checkEventExistsById(EVENT_ID);
        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        verify(eventParticipationRepository, times(1)).register(EVENT_ID, USER_ID);
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
        String errorMessage = "User is not registered for the event";

        validationSkip();
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));
        verify(eventValidator, times(1)).checkEventExistsById(EVENT_ID);
        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        assertEquals(errorMessage, exception.getMessage());
        verify(eventParticipationRepository, never()).register(EVENT_ID, USER_ID);
    }

    @Test
    @DisplayName("Successful unregister")
    public void testUnregisterSuccessful() {
        User user = new User();
        user.setId(USER_ID);

        validationSkip();
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);

        verify(eventValidator, times(1)).checkEventExistsById(EVENT_ID);
        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        verify(eventParticipationRepository, times(1))
                .unregister(EVENT_ID, USER_ID);
    }

    @Test
    @DisplayName("Error get participant -  Event not found")
    public void testGetParticipantEventNotFound() {
        validationData(EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Get participant successful")
    public void testGetParticipantSuccessful() {
        List<User> users = createListUser();
        List<UserDto> userDtos = createListUserDto();

        doNothing().when(eventValidator).checkEventExistsById(EVENT_ID);
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(users);
        List<UserDto> participants = eventParticipationService.getParticipant(EVENT_ID);

        verify(userMapper, times(1)).toListUserDto(users);
        verify(eventValidator, times(1)).checkEventExistsById(EVENT_ID);
        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(EVENT_ID);

        assertEquals(userDtos.get(0).getUsername(), participants.get(0).getUsername());
        assertEquals(userDtos.get(0).getEmail(), participants.get(0).getEmail());
        assertEquals(userDtos.size(), participants.size());
        assertEquals(userDtos.get(0).getId(), participants.get(0).getId());
    }

    @Test
    @DisplayName("Error get participant count - Event not found")
    public void testGetParticipantCountEventNotFound() {
        validationData(EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Get participant count successful")
    public void testGetParticipantCountSuccessful() {
        Integer expectedCount = 1;

        doNothing().when(eventValidator).checkEventExistsById(EVENT_ID);
        when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(expectedCount);
        Integer participantCount = eventParticipationService.getParticipantCount(EVENT_ID);

        verify(eventParticipationRepository, times(1))
                .countParticipants(EVENT_ID);
        verify(eventValidator, times(1)).checkEventExistsById(EVENT_ID);
        assertEquals(expectedCount, participantCount);

    }

    private List<UserDto> createListUserDto() {
        UserDto alexDto = UserDto.builder()
                .id(USER_ID)
                .username("Alex")
                .email("alex@mail.ru")
                .build();

        return List.of(alexDto);
    }

    private List<User> createListUser() {
        User alex = User.builder()
                .id(USER_ID)
                .username("Alex")
                .email("alex@mail.ru")
                .build();

        return List.of(alex);
    }

    private void validationSkip() {
        doNothing().when(eventValidator).checkEventExistsById(EventParticipationServiceTest.EVENT_ID);
        doNothing().when(userValidator).checkUserExistsById(EventParticipationServiceTest.USER_ID);
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
