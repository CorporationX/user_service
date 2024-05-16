package school.faang.user_service.service.event;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventParticipationServiceTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 1L;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterParticipantSuccess() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(new ArrayList<>());

        eventParticipationService.registerParticipant(EVENT_ID, USER_ID);

        verify(eventParticipationRepository, times(1)).register(EVENT_ID, USER_ID);
    }

    @Test
    public void testRegisterParticipantAlreadyRegistered() {
        List<User> participants = new ArrayList<>();
        User user = new User();
        user.setId(USER_ID);
        participants.add(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(participants);

        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));
    }

    @Test
    public void testUnregisterParticipantSuccess() {
        List<User> participants = new ArrayList<>();
        User user = new User();
        user.setId(USER_ID);
        participants.add(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(participants);

        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);

        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(new ArrayList<>());

        verify(eventParticipationRepository, times(1)).unregister(EVENT_ID, USER_ID);
    }

    @Test
    public void testUnregisterNonParticipant() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));
    }


    @Test
    public void testGetParticipants() {
        List<User> participants = new ArrayList<>();
        User ivan = new User();
        User oleg = new User();

        ivan.setId(1L);
        ivan.setUsername("Ivan");
        ivan.setEmail("example@example.com");

        oleg.setId(2L);
        oleg.setUsername("Oleg");
        oleg.setEmail("example@example.com");

        participants.add(ivan);
        participants.add(oleg);

        List<UserDto> participantsDto = new ArrayList<>();
        UserDto ivanUserDto = new UserDto(1L, "Ivan", "example@example.com");
        UserDto olegUserDto = new UserDto(2L, "Oleg", "example@example.com");

        participantsDto.add(ivanUserDto);
        participantsDto.add(olegUserDto);

        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(participants);
        when(userMapper.toDtoList(participants)).thenReturn(participantsDto);

        List<UserDto> result = eventParticipationService.getParticipants(EVENT_ID);

        assertEquals(2, result.size());

        assertEquals(ivanUserDto.getUsername(), result.get(0).getUsername());
        assertEquals(olegUserDto.getUsername(), result.get(1).getUsername());
    }

    @Test
    public void testGetParticipantsCount() {
        int expectedCount = 5;

        when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(EVENT_ID);

        verify(eventParticipationRepository, times(1)).countParticipants(EVENT_ID);

        assertEquals(expectedCount, actualCount);
    }
}