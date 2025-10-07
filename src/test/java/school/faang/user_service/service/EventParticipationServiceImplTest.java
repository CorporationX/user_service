package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.EventParticipationServiceImpl;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceImplTest {
    private static final long DEFAULT_EVENT_ID = 1;
    private static final long DEFAULT_USER_ID = 1;
    private static final String BLANK_STRING = " ";
    private static final long COUNT = 1;
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private UserMapper userMapper;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private EventParticipationServiceImpl eventParticipationServiceImpl;

    @Test
    public void getAllParticipantsByEventId_shouldRevertParticipant() {
        User user = preparationUser(DEFAULT_USER_ID);
        List<User> list = List.of(user);
        UserDto userDto = new UserDto(DEFAULT_USER_ID,
                BLANK_STRING, BLANK_STRING, BLANK_STRING, BLANK_STRING);
        Mockito.when(userMapper.toUserDto(user)).thenReturn(userDto);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(DEFAULT_EVENT_ID)).thenReturn(list);

        UserDto userDtoTest = eventParticipationServiceImpl.getAllParticipantsByEventId(DEFAULT_EVENT_ID).get(0);
        Assertions.assertEquals(user.getId(), userDtoTest.id());
    }

    @Test
    public void registerParticipant_userAlreadyAttendees_shouldThrowEntityNotFoundException() {
        User user = preparationUser(DEFAULT_USER_ID);
        Event event = preparationEvent(user);
        Mockito.when(eventRepository.getByIdOrThrow(DEFAULT_EVENT_ID))
                .thenReturn(event);
        Mockito.when(userContext.getUserId()).thenReturn(DEFAULT_USER_ID);

        Assert.assertThrows(EntityNotFoundException.class,
                () -> eventParticipationServiceImpl.registerParticipant(DEFAULT_EVENT_ID, DEFAULT_USER_ID));
    }

    @Test
    public void registerParticipant_userUseNotYourId_shouldThrowForbiddenException() {
        User user = preparationUser(DEFAULT_USER_ID);
        Event event = preparationEvent(user);
        Mockito.when(userContext.getUserId()).thenReturn(DEFAULT_USER_ID + DEFAULT_USER_ID);
        Mockito.when(eventRepository.getByIdOrThrow(DEFAULT_EVENT_ID))
                .thenReturn(event);

        Assert.assertThrows(ForbiddenException.class,
                () -> eventParticipationServiceImpl.registerParticipant(DEFAULT_EVENT_ID, DEFAULT_USER_ID));
    }


    @Test
    public void registerParticipant_registerParticipant_shouldCorrectRegisterParticipant() {
        User user = preparationUser(DEFAULT_USER_ID + DEFAULT_USER_ID);
        Event event = preparationEvent(user);
        Mockito.when(eventRepository.getByIdOrThrow(DEFAULT_EVENT_ID))
                .thenReturn(event);

        eventParticipationServiceImpl.registerParticipant(DEFAULT_EVENT_ID, DEFAULT_USER_ID);

        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .register(DEFAULT_EVENT_ID, DEFAULT_USER_ID);
    }

    @Test
    public void countParticipantsByEventId_countParticipants_shouldRevertCount() {
        Mockito.when(eventParticipationRepository.countParticipants(DEFAULT_EVENT_ID)).thenReturn(COUNT);
        long countResponseDto = eventParticipationServiceImpl.countParticipantsByEventId(DEFAULT_EVENT_ID).count();

        Assertions.assertEquals(COUNT, countResponseDto);
    }

    @Test
    public void unregisteredParticipation_userNotAttendee_shouldThrowEntityNotFoundException() {
        User user = preparationUser(DEFAULT_USER_ID + DEFAULT_USER_ID);
        Event event = preparationEvent(user);
        Mockito.when(userContext.getUserId()).thenReturn(DEFAULT_USER_ID);
        Mockito.when(eventRepository.getByIdOrThrow(DEFAULT_EVENT_ID))
                .thenReturn(event);

        Assert.assertThrows(EntityNotFoundException.class,
                () -> eventParticipationServiceImpl.unregisteredParticipation(DEFAULT_EVENT_ID, DEFAULT_USER_ID));
    }

    @Test
    public void unregisteredParticipation_userUseNotYourId_shouldThrowForbiddenException() {
        User user = preparationUser(DEFAULT_USER_ID + DEFAULT_USER_ID);
        Event event = preparationEvent(user);
        Mockito.when(userContext.getUserId()).thenReturn(DEFAULT_USER_ID);
        Mockito.when(eventRepository.getByIdOrThrow(DEFAULT_EVENT_ID))
                .thenReturn(event);
        Assert.assertThrows(ForbiddenException.class,
                () -> eventParticipationServiceImpl.unregisteredParticipation(DEFAULT_EVENT_ID, DEFAULT_USER_ID + DEFAULT_USER_ID + DEFAULT_USER_ID));

    }

    public void preparationData(long userId) {

    }

    //Переделать методы, вынести
    private User preparationUser(long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    private Event preparationEvent(User user) {
        Event event = new Event();
        event.setAttendees(List.of(user));
        return event;
    }
}
