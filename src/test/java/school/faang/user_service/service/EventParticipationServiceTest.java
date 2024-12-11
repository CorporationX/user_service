package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.user.ParticipantDto;
import school.faang.user_service.entity.user.User;
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
    private ParticipantDto participantDto;
    private EventDto eventDto;

    @BeforeEach
    public void setUp() {
        participantDto = new ParticipantDto(1L, "User", "user@gmail.com");

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
        user.setId(participantDto.id());
        user.setUsername(participantDto.username());
        user.setEmail(participantDto.email());
    }

    @Test
    public void testRegisterParticipantAlreadyRegistered() {
        testConfig(false);
        assertThrows(DataValidationException.class, () -> eventParticipationService.registerParticipant(participantDto, eventDto));
    }

    @Test
    public void testRegisterParticipant() {
        testConfig(true);
        eventParticipationService.registerParticipant(participantDto, eventDto);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(participantDto.id(), eventDto.id());
    }

    @Test
    public void testUnregistrationUnregisteredParticipant() {
        testConfig(true);
        assertThrows(DataValidationException.class, () -> eventParticipationService.unregisterParticipant(participantDto, eventDto));
    }

    @Test
    public void testUnregisterParticipant() {
        testConfig(false);
        eventParticipationService.unregisterParticipant(participantDto, eventDto);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).unregister(participantDto.id(), eventDto.id());
    }

    @Test
    public void testGetParticipants() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventDto.id())).thenReturn(List.of(user));
        when(userMapper.toDtos(List.of(user))).thenReturn(List.of(participantDto));
        assertEquals(List.of(participantDto), eventParticipationService.getParticipants(eventDto.id()));
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
        when(userService.getUserById(participantDto.id())).thenReturn(user);
    }
}
