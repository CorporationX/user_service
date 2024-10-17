package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationServiceImpl;

@ExtendWith(SpringExtension.class)
class EventParticipationControllerTest {

    @Mock
    EventParticipationServiceImpl service;

    @InjectMocks
    EventParticipationController controller;

    private UserDto userDto;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .id(1L)
                .username("1")
                .email("1")
                .build();
    }

    @Test
    void testRegisterParticipant() {
        controller.registerParticipant(1L, userDto);

        Mockito.verify(service, Mockito.times(1))
                .registerParticipant(1L, userDto.getId());
    }

    @Test
    void testUnregisterParticipant() {
        controller.unregisterParticipant(1L, userDto);

        Mockito.verify(service, Mockito.times(1))
                .unregisterParticipant(1L, userDto.getId());
    }

    @Test
    void testGetParticipant() {
        controller.getParticipant(1L);

        Mockito.verify(service, Mockito.times(1))
                .getParticipant(1L);
    }

    @Test
    void testGetParticipantsCount() {
        controller.getParticipantsCount(1L);

        Mockito.verify(service, Mockito.times(1))
                .getParticipantsCount(1L);
    }
}
