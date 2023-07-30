package school.faang.user_service.controller.mentorshipRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RequestResponse;
import school.faang.user_service.service.mentorshipRequest.MentorshipRequestService;
import school.faang.user_service.util.mentorshipRequest.exception.GetRequestsMentorshipsException;
import school.faang.user_service.util.mentorshipRequest.exception.UnknownRejectionReasonException;
import school.faang.user_service.util.mentorshipRequest.validator.ControllerRequestValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final ControllerRequestValidator controllerRequestValidator;

    @PostMapping("/send_request")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto dto) {
        return mentorshipRequestService.requestMentorship(dto);
    }

    @GetMapping("/requests")
    public ResponseEntity<RequestResponse> getRequests(@RequestBody @Valid RequestFilterDto requestFilterDto,
                                       BindingResult bindingResult) {
        controllerRequestValidator.validateRequest(bindingResult, new GetRequestsMentorshipsException());

        return ResponseEntity.ok(new RequestResponse(mentorshipRequestService.getRequests(requestFilterDto)));
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable("id") long id) {
        controllerRequestValidator.validateRequest(id);

        mentorshipRequestService.acceptRequest(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<HttpStatus> rejectRequest(@PathVariable("id") long id,
                                                    @RequestBody @Valid RejectionDto rejectionDto,
                                                    BindingResult bindingResult) {
        controllerRequestValidator.validateRequest(id, bindingResult, new UnknownRejectionReasonException());

        mentorshipRequestService.rejectRequest(id, rejectionDto);

        return ResponseEntity.ok().build();
    }
}