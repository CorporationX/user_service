package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.EventParticipationValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    private static final long USER_ID_FIRST = 1L;
    private static final long USER_ID_SECOND = 2L;
    private static final long EVENT_ID = 1L;
    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_NAME = "userTest";
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventParticipationValidator eventParticipationValidator;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private EventParticipationService eventParticipationService;

    User firstUser;
    User secondUser;
    List<User> userList;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setId(USER_ID_FIRST);
        firstUser.setEmail("first"+USER_EMAIL);
        firstUser.setUsername("first"+USER_NAME);

        secondUser = new User();
        secondUser.setId(USER_ID_SECOND);
        secondUser.setEmail("second"+USER_EMAIL);
        secondUser.setUsername("second"+USER_NAME);

        userList = List.of(firstUser, secondUser);
    }
    @Test
    @DisplayName("Test register participant :  non exist user")
    public void checkRegisterNonExistUser() {
        String errorMessage = "User doesn't exist in the system ID = " + USER_ID_FIRST;

       doThrow(new DataValidationException(errorMessage))
               .when(eventParticipationValidator)
               .checkUserIsExisting(USER_ID_FIRST);

       Exception exception = assertThrows(DataValidationException.class,
               ()-> eventParticipationService.registerParticipant(USER_ID_FIRST, EVENT_ID));

       verifyNoMoreInteractions(eventParticipationValidator, eventParticipationRepository);
       assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test register participant :  non exist event")
    public void checkRegisterNonExistEvent() {
        String errorMessage = "Event doesn't exist in the system ID = " + EVENT_ID;
        doNothing().when(eventParticipationValidator).checkUserIsExisting(USER_ID_FIRST);

        doThrow(new DataValidationException(errorMessage))
                .when(eventParticipationValidator)
                .checkEventIsExisting(EVENT_ID);

        Exception exception = assertThrows(DataValidationException.class,
                ()-> eventParticipationService.registerParticipant(USER_ID_FIRST, EVENT_ID));

        verifyNoMoreInteractions(eventParticipationValidator, eventParticipationRepository);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test register participant :  user already registered")
    public void checkRegisterAlreadyRegistered() {
        String errorMessage = "User with ID: " + USER_ID_FIRST + "has already registered";
        doNothing().when(eventParticipationValidator).checkUserIsExisting(USER_ID_FIRST);
        doNothing().when(eventParticipationValidator).checkEventIsExisting(EVENT_ID);

        doThrow(new DataValidationException(errorMessage))
                .when(eventParticipationValidator)
                .checkIsUserAlreadyRegistered(USER_ID_FIRST, EVENT_ID);

        Exception exception = assertThrows(DataValidationException.class,
                ()-> eventParticipationService.registerParticipant(USER_ID_FIRST, EVENT_ID));

        verifyNoMoreInteractions(eventParticipationValidator, eventParticipationRepository);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test register participant :  successfully")
    public void checkRegisterSuccessfully() {
        String errorMessage = "User with ID: " + USER_ID_FIRST + "has already registered";
        doNothing().when(eventParticipationValidator).checkUserIsExisting(USER_ID_FIRST);
        doNothing().when(eventParticipationValidator).checkEventIsExisting(EVENT_ID);
        doNothing().when(eventParticipationValidator).checkIsUserAlreadyRegistered(USER_ID_FIRST,EVENT_ID);

        eventParticipationService.registerParticipant(EVENT_ID, USER_ID_FIRST);

        verify(eventParticipationRepository, times(1)).register(EVENT_ID, USER_ID_FIRST);
    }

    @Test
    @DisplayName("Test unregister participant :  non exist user")
    public void checkUnregisterNonExistUser() {
        String errorMessage = "User doesn't exist in the system";

        doThrow(new DataValidationException(errorMessage))
                .when(eventParticipationValidator)
                .checkUserIsExisting(USER_ID_FIRST);

        Exception exception = assertThrows(DataValidationException.class,
                ()-> eventParticipationService.unregisterParticipant(USER_ID_FIRST, EVENT_ID));

        verifyNoMoreInteractions(eventParticipationValidator, eventParticipationRepository);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test unregister participant :  non exist event")
    public void checkUnregisterNonExistEvent() {
        String errorMessage = "Event doesn't exist in the system";
        doNothing().when(eventParticipationValidator).checkUserIsExisting(USER_ID_FIRST);

        doThrow(new DataValidationException(errorMessage))
                .when(eventParticipationValidator)
                .checkEventIsExisting(EVENT_ID);

        Exception exception = assertThrows(DataValidationException.class,
                ()-> eventParticipationService.unregisterParticipant(USER_ID_FIRST, EVENT_ID));

        verifyNoMoreInteractions(eventParticipationValidator, eventParticipationRepository);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test unregister participant :  user hasn't registered")
    public void checkUnregisterNotRegistered() {
        String errorMessage = "User with ID: " + USER_ID_FIRST + "has not registered";
        doNothing().when(eventParticipationValidator).checkUserIsExisting(USER_ID_FIRST);
        doNothing().when(eventParticipationValidator).checkEventIsExisting(EVENT_ID);

        doThrow(new DataValidationException(errorMessage))
                .when(eventParticipationValidator)
                .checkIsUserNotRegistered(USER_ID_FIRST, EVENT_ID);

        Exception exception = assertThrows(DataValidationException.class,
                ()-> eventParticipationService.unregisterParticipant(USER_ID_FIRST, EVENT_ID));

        verifyNoMoreInteractions(eventParticipationValidator, eventParticipationRepository);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test unregister participant :  successfully")
    public void checkUnregisterSuccessfully() {
        doNothing().when(eventParticipationValidator).checkUserIsExisting(USER_ID_FIRST);
        doNothing().when(eventParticipationValidator).checkEventIsExisting(EVENT_ID);
        doNothing().when(eventParticipationValidator).checkIsUserNotRegistered(USER_ID_FIRST,EVENT_ID);

        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID_FIRST);

        verify(eventParticipationRepository, times(1)).unregister(EVENT_ID, USER_ID_FIRST);
    }

    @Test
    @DisplayName("Get all Participants")
    public void checkGetParticipant(){
        doNothing().when(eventParticipationValidator).checkEventIsExisting(EVENT_ID);
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(userList);

        List<UserDto> result = eventParticipationService.getParticipant(EVENT_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("firstuserTest", result.get(0).getUsername());
    }

    @Test
    @DisplayName("Get participants count")
    public void checkGetParticipantsCount(){
        doNothing().when(eventParticipationValidator).checkEventIsExisting(EVENT_ID);
        when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(2);

        int result = eventParticipationService.getParticipantsCount(EVENT_ID);

        assertEquals(2, result);
    }
}
