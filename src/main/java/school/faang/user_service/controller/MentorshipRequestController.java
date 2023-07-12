package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.util.response.ErrorResponse;
import school.faang.user_service.util.exception.RequestMentorshipException;
import school.faang.user_service.util.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.exception.TimeHasNotPassedException;
import school.faang.user_service.util.exception.UserNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public ResponseEntity<HttpStatus> requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder message = new StringBuilder();

            fieldErrors.forEach(fieldError -> {
                message.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            });

            throw new RequestMentorshipException(message.toString());
        }

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Incorrect inputs about users",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameMentorAndMenteeException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(SameMentorAndMenteeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "The same mentor is specified",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestMentorshipException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestMentorshipException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeHasNotPassedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(TimeHasNotPassedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "The request can be sent once every three months",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Some elements are not in a database",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
