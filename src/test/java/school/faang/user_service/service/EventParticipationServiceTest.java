package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.model.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    private User user;
    private UserDto userDto;
    private EventDto eventDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "User", "user@gmail.com");

        eventDto = EventDto.builder()
                .id(2L)
                .title("Event")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .location(null)
                .description("")
                .relatedSkills(null)
                .location("")
                .maxAttendees(1)
                .type(null)
                .status(null)
                .build();

        user = new User();
        user.setId(userDto.id());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
    }

    @Test
    public void testRegisterParticipantAlreadyRegistered() {
        testConfig(false);
        assertThrows(DataValidationException.class, () -> eventParticipationService.registerParticipant(userDto, eventDto));
    }

    @Test
    public void testRegisterParticipant() {
        testConfig(true);
        eventParticipationService.registerParticipant(userDto, eventDto);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(userDto.id(), eventDto.id());
    }

    @Test
    public void testUnregistrationUnregisteredParticipant() {
        testConfig(true);
        assertThrows(DataValidationException.class, () -> eventParticipationService.unregisterParticipant(userDto, eventDto));
    }

    @Test
    public void testUnregisterParticipant() {
        testConfig(false);
        eventParticipationService.unregisterParticipant(userDto, eventDto);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).unregister(userDto.id(), eventDto.id());
    }

    @Test
    public void testGetParticipants() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id())).thenReturn(List.of(user));
        when(userMapper.toDtos(List.of(user))).thenReturn(List.of(userDto));
        assertEquals(List.of(userDto), eventParticipationService.getParticipants(eventDto.id()));
    }

    @Test
    public void testGetParticipantsCount() {
        when(eventParticipationRepository.countParticipants(eventDto.id())).thenReturn(1);
        assertEquals(1, eventParticipationRepository.countParticipants(eventDto.id()));
    }

    private void testConfig(boolean emptyList) {
        if (emptyList) {
            when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id())).thenReturn(List.of());
        } else {
            when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id())).thenReturn(List.of(user));
        }
        when(userService.getUserById(userDto.id())).thenReturn(user);
    }
}
