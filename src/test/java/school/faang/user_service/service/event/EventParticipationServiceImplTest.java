package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.sevice.event.EventParticipationServiceImpl;
import school.faang.user_service.validator.EventParticipationServiceValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {
    @Mock
    private EventParticipationRepository repository;
    @Spy
    private UserMapper mapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private EventParticipationServiceValidator validator;
    @InjectMocks
    private EventParticipationServiceImpl service;

    User user;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void testUserRegister() {
        service.registerParticipant(1, 2);
        Mockito.verify(repository, Mockito.times(1))
                .register(1, 2);
    }

    @Test
    void testUserUnregister() {
        service.unregisterParticipant(1, 2);
        Mockito.verify(repository, Mockito.times(1))
                .unregister(1, 2);
    }

    @Test
    void testGetParticipant() {
        Mockito.when(repository.findAllParticipantsByEventId(1))
                .thenReturn(List.of(user));

        Assertions.assertEquals(List.of(new UserDto(user.getId(), user.getUsername(), user.getEmail())),
                service.getParticipant(1L));

        Mockito.verify(repository, Mockito.times(1))
                .findAllParticipantsByEventId(1);

    }

    @Test
    void testGetParticipantsCount() {
        service.getParticipantsCount(1L);

        Mockito.verify(repository, Mockito.times(1))
                .countParticipants(1L);
    }
}
