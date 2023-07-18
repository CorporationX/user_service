package school.faang.user_service.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.mentorship.InvalidRequestMentorId;
import school.faang.user_service.exception.mentorship.MenteeMentorOneUser;
import school.faang.user_service.exception.mentorship.UserNotFound;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {InvalidRequestMentorId.class})
    public ResponseEntity<Object> handleInvalidMentorId(InvalidRequestMentorId e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        PayloadException payloadException = new PayloadException(
                badRequest,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(payloadException, badRequest);
    }
    
    @ExceptionHandler(UserNotFound.class) 
    public ResponseEntity<Object> handleUserNotFound(UserNotFound e) {
        PayloadException payloadException = new PayloadException(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(payloadException, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MenteeMentorOneUser.class) 
    public ResponseEntity<Object> handleMenteeMentorOneUser(MenteeMentorOneUser e) {
        PayloadException payloadException = new PayloadException(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(payloadException, HttpStatus.BAD_REQUEST);
    }
}
