package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.UserAlreadyRegisteredException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    private static final long EVENT_ID = 123L;
    private static final long USER_ID = 333L;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;


    @Nested
    @DisplayName("registerParticipant")
    class RegisterParticipantTest {
        @Test
        @DisplayName("When current user is not yet registered for the event, then register")
        void whenCurrentUserIsNotRegisteredThenRegister() {
            long otherUserId = 1L;
            User otherUser = User.builder().id(otherUserId).build();
            UserDto otherUserDto = UserDto.builder().id(otherUserId).build();

            when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of(otherUser));
            when(userMapper.toDtoList(List.of(otherUser))).thenReturn(List.of(otherUserDto));

            eventParticipationService.registerParticipant(EVENT_ID, USER_ID);

            verify(userMapper, times(1)).toDtoList(List.of(otherUser));
            verify(eventParticipationRepository, times(1)).register(EVENT_ID, USER_ID);
        }

        @Test
        @DisplayName("When current user is already registered for the event, then throw exception")
        void whenCurrentUserAlreadyRegisteredThenThrowException() {
            User user = User.builder().id(USER_ID).build();
            UserDto userDto = UserDto.builder().id(USER_ID).build();

            when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of(user));
            when(userMapper.toDtoList(List.of(user))).thenReturn(List.of(userDto));

            assertThrows(UserAlreadyRegisteredException.class,
                () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));
        }
    }

    @Nested
    @DisplayName("getParticipantsCount")
    class GetParticipantsCountTest {
        @Test
        @DisplayName("When participants were found for the event")
        void whenCountParticipantsIsNotEmpty() {
            List<Event> events = List.of(
                Event.builder().id(EVENT_ID).build(),
                Event.builder().id(1L).build(),
                Event.builder().id(23L).build()
            );
            int actualCount = events.size();

            when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(actualCount);

            int expectedCount = eventParticipationService.getParticipantsCount(EVENT_ID);

            verify(eventParticipationRepository, times(1)).countParticipants(EVENT_ID);
            assertEquals(expectedCount, actualCount);
        }
    }

    @Nested
    @DisplayName("getAllParticipants")
    class GetAllParticipantsTest {
        @Test
        @DisplayName("When find all participants by eventId")
        void whenFindAllParticipantsByEventId() {
            when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of(new User()));
            when(userMapper.toDtoList(List.of(new User()))).thenReturn(List.of(new UserDto()));

            List<UserDto> userDtoList = eventParticipationService.getAllParticipants(EVENT_ID);

            verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(EVENT_ID);
            assertEquals(userDtoList, List.of(new UserDto()));
        }
    }
}