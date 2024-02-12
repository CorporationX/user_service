package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.mapper.user.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    int expectedParticipantsCount;
    long eventId;
    long registerId;
    long unregisteredId;
    User registerUser;
    User unregisteredUser;
    UserDto registerUserDto;
    UserDto unregisteredUserDto;
    List<UserDto> listUsersAtEventDto;
    List<User> listUsersAtEvent;

    @BeforeEach
    void setup() {
        expectedParticipantsCount = 2;

        eventId = 1;
        registerId = 1;
        unregisteredId = 3;

        listUsersAtEventDto = new ArrayList<>();
        listUsersAtEvent = new ArrayList<>();

        registerUserDto = UserDto.builder()
                .id(1L)
                .build();
        UserDto user2Dto = UserDto.builder()
                .id(2L)
                .build();
        unregisteredUserDto = UserDto.builder()
                .id(3L)
                .build();

        registerUser = User.builder()
                .id(1)
                .build();
        User user2 = User.builder()
                .id(2)
                .build();
        unregisteredUser = User.builder()
                .id(3)
                .build();


        listUsersAtEventDto.add(registerUserDto);
        listUsersAtEventDto.add(user2Dto);

        listUsersAtEvent.add(registerUser);
        listUsersAtEvent.add(user2);
    }
    @Test
    public void testRegisterParticipant_UserNotRegistered() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findById(unregisteredId)).thenReturn(Optional.of(unregisteredUser));
        when(userMapper.toDto(unregisteredUser)).thenReturn(unregisteredUserDto);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        when(userMapper.toUserDtoList(listUsersAtEvent)).thenReturn(listUsersAtEventDto);
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(eventId, unregisteredId));
    }

    @Test
    public void testRegisterParticipant_EventNotFound() {
        when(eventRepository.existsById(eventId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, registerId));
    }

    @Test
    public void testRegisterParticipant_UserAlreadyRegistered() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findById(registerId)).thenReturn(Optional.of(registerUser));
        when(userMapper.toDto(registerUser)).thenReturn(registerUserDto);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        when(userMapper.toUserDtoList(listUsersAtEvent)).thenReturn(listUsersAtEventDto);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, registerId));
    }

    @Test
    void testUnregisterParticipant_UserRegistered_SuccessfullyUnregistered() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        when(userMapper.toUserDtoList(listUsersAtEvent)).thenReturn(listUsersAtEventDto);
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(eventId, registerId));
        verify(eventParticipationRepository, times(1)).unregister(eventId, registerId);
    }

    @Test
    void testUnregisterParticipant_UserNotRegistered_ThrowsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        when(userMapper.toUserDtoList(listUsersAtEvent)).thenReturn(listUsersAtEventDto);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(eventId, unregisteredId));
        verify(eventParticipationRepository, never()).unregister(eventId, unregisteredId);
    }


    @Test
    void testGetParticipant_ExistingEventId_ReturnsListOfParticipants() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        when(userMapper.toUserDtoList(listUsersAtEvent)).thenReturn(listUsersAtEventDto);
        List<UserDto> actualParticipants = eventParticipationService.getParticipant(eventId);
        assertEquals(listUsersAtEventDto, actualParticipants);
    }

    @Test
    void testGetParticipant_NonExistingEventId_ReturnsEmptyList() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.getParticipant(eventId);
        });
    }

    @Test
    void testGetParticipantsCount_ExistingEventId_ReturnsParticipantCount() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedParticipantsCount);
        int count = eventParticipationService.getParticipantsCount(eventId);
        assertEquals(listUsersAtEventDto.size(), count);
    }

    @Test
    void testGetParticipantsCount_NonExistingEventId_ThrowsIllegalArgumentException() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });
    }
}

