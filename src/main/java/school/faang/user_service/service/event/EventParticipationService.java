package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventRegistrationConflictException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;

    public ResponseEntity<String> registerParticipant(long eventId, long userId) {
        if (!checkEventExists( eventId ))
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Event ID " + eventId + " does not exist" );
        boolean isParticipant = checkIfEventParticipant( eventId, userId );
        if (isParticipant) {
            throw new EventRegistrationConflictException( "User with id " + userId + " already registered for event with id " + eventId );
        }
        eventParticipationRepository.register( eventId, userId );
        return ResponseEntity.ok( "User " + userId + " registered" );
    }

    public ResponseEntity<Void> unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        boolean isParticipant = checkIfEventParticipant( eventId, userId );
        if (!isParticipant) {
            throw new DataValidationException( "User with id " + userId + " is not registered for event with id " + eventId );
        }
        eventParticipationRepository.unregister( eventId, userId );

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<User>> getParticipants(@PathVariable long eventId) {
        List<User> participantList = eventParticipationRepository.findAllParticipantsByEventId( eventId );
        return ResponseEntity.status( HttpStatus.OK ).body( participantList );
    }

    public int getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationRepository.countParticipants( eventId );
    }

    private boolean checkIfEventParticipant(long eventId, long userId) {
        List<User> participantList = eventParticipationRepository.findAllParticipantsByEventId( eventId );
        return participantList.stream().anyMatch( user -> user.getId() == userId );
    }

    private boolean checkEventExists(long eventId) {
        return eventRepository.existsById( eventId );
    }

}
