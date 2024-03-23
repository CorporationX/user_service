package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;
    private final EventParticipationValidator participationValidator;
    private final UserContext userContext;

    @PostMapping("/{eventId}")
    public ResponseEntity<String> registerParticipant(@PathVariable long eventId) {
        long userId = userContext.getUserId();
        participationValidator.checkForNull( eventId, userId );
        return eventParticipationService.registerParticipant( eventId, userId );
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable long eventId) {
        long userId = userContext.getUserId();
        participationValidator.checkForNull( eventId, userId );
        return eventParticipationService.unregisterParticipant( eventId, userId );
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<User>> getParticipants(@PathVariable long eventId) {
        participationValidator.checkForNull( eventId );
        return eventParticipationService.getParticipants( eventId );
    }

    @GetMapping()
    public int getParticipantsCount(long eventId) {
        participationValidator.checkForNull( eventId );
        return eventParticipationService.getParticipantsCount( eventId );
    }
}

