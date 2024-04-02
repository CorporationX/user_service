package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventRegistrationConflictException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    EventParticipationRepository participationRepository;

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    EventParticipationService eventParticipationService;

    private final List<User> userList = new ArrayList<>();

    private long eventId ;
    @BeforeEach
    void setUp() {
        eventId = 1L;
        User user1 = User.builder().id( 1L ).build();
        User user2 = User.builder().id( 2L ).build();
        userList.add( user1 );
        userList.add( user2 );
    }

    @Test
    public void testRegisterParticipantAlreadyRegistered(){
        long userId = 2L;
        when( participationRepository.findAllParticipantsByEventId(eventId) ).thenReturn( userList );
        when(eventRepository.existsById( eventId )).thenReturn(true  );
        assertThrows( EventRegistrationConflictException.class, ()->eventParticipationService.registerParticipant( eventId, userId ));
    }

    @Test
    public void testRegisterParticipantNotAlreadyRegistered(){
        long userId = 3L;
        when(participationRepository.findAllParticipantsByEventId(eventId )).thenReturn( userList );
        when(eventRepository.existsById( eventId )).thenReturn(true  );
        assertDoesNotThrow( ()->eventParticipationService.registerParticipant( eventId, userId ) );
        verify( participationRepository, times( 1 )).register( eventId, userId);
    }

    @Test
    public void testUnregisterParticipantWhoNotRegistered(){
        long userId = 3L;
        when(participationRepository.findAllParticipantsByEventId( eventId )).thenReturn( userList );
        assertThrows( DataValidationException.class, ()->eventParticipationService.unregisterParticipant( eventId, userId ) );
    }

    @Test
    public void testUnregisterParticipantSuccess(){
        long userId = 2L;
        when( participationRepository.findAllParticipantsByEventId( eventId )).thenReturn( userList );
        assertDoesNotThrow(  ()->eventParticipationService.unregisterParticipant( eventId, userId ));
        verify( participationRepository, times( 1 ) ).unregister( eventId, userId );
    }

    @Test
    public void testGetParticipant() {
        when(participationRepository.findAllParticipantsByEventId( eventId )).thenReturn( userList );
        ResponseEntity<List<User>> actual = eventParticipationService.getParticipants( eventId );
        verify( participationRepository, times(1) ).findAllParticipantsByEventId( eventId );
        assertEquals( userList, actual.getBody() );
    }
    @Test
    public void testParticipantCount(){
        when( participationRepository.countParticipants(eventId) ).thenReturn( userList.size() );
        int actual = eventParticipationService.getParticipantsCount( eventId );
        verify( participationRepository, times( 1 ) ).countParticipants( eventId );
        assertEquals( userList.size(), actual);
    }
}








