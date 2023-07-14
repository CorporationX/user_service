package school.faang.user_service.controller.mentorshipRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RequestsResponse;
import school.faang.user_service.service.mentorshipRequest.MentorshipRequestService;
import school.faang.user_service.util.mentorshipRequest.exception.GetRequestsMentorshipsException;
import school.faang.user_service.util.mentorshipRequest.exception.NoRequestsException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyAcceptedException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyRejectedException;
import school.faang.user_service.util.mentorshipRequest.response.ErrorResponse;
import school.faang.user_service.util.mentorshipRequest.exception.RequestMentorshipException;
import school.faang.user_service.util.mentorshipRequest.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.mentorshipRequest.exception.TimeHasNotPassedException;
import school.faang.user_service.util.mentorshipRequest.exception.UserNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/send_request")
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

    @GetMapping("/requests")
    public RequestsResponse getRequests(@RequestBody @Valid RequestFilterDto requestFilterDto,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                message.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            });

            throw new GetRequestsMentorshipsException(message.toString());
        }

        return new RequestsResponse(mentorshipRequestService.getRequests(requestFilterDto));
    }

    @PostMapping("/accept/{id}") // интересно, как тут лучше сделать через @PathVariable или @RequestBody?
    public ResponseEntity<HttpStatus> acceptRequest(@PathVariable("id") long id) {
        mentorshipRequestService.acceptRequest(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<HttpStatus> rejectRequest(@PathVariable("id") long id,
                                                    @RequestBody @Valid RejectionDto rejectionDto,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                message.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            });

            throw new RequestMentorshipException(message.toString());
        }

        mentorshipRequestService.rejectRequest(id, rejectionDto);

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

    @ExceptionHandler(GetRequestsMentorshipsException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(GetRequestsMentorshipsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRequestsException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(NoRequestsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "No requests were created",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestIsAlreadyAcceptedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestIsAlreadyAcceptedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "This request is already accepted",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestIsAlreadyRejectedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestIsAlreadyRejectedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "This request is already rejected",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
