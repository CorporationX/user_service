package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    @Transactional
    public ResponseEntity<String> registerParticipant(long eventId, long userId){
        List<User> participantList = eventParticipationRepository.findAllParticipantsByEventId( eventId);
        boolean isRegistered = participantList.stream().anyMatch( user->user.getId() == userId);
        Optional.of( isRegistered).
                orElseThrow(()->new DataValidationException( "User with id " + userId + "already registered for event" ));

        eventParticipationRepository.register( eventId, userId );

        return ResponseEntity.ok("User " + userId + " registered");
    }


}
