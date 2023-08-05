package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionReasonDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto requestDto) {
        log.debug("Received mentorship request: {}", requestDto);
        return requestService.requestMentorship(requestDto);
    }

    @GetMapping("/requests")
    public List<MentorshipRequestDto> getRequests(@Valid @RequestBody MentorshipRequestFilterDto requestFilter) {
        log.debug("Received request to get mentorship requests: {}", requestFilter);
        return requestService.getRequests(requestFilter);
    }

    @PutMapping("/request/{id}/accept")
    public MentorshipRequestDto acceptRequest(
            @PathVariable @Min(message = "Request ID must be greater than zero", value = 1) long id) {
        log.debug("Received request to accept mentorship request: {}", id);
        return requestService.acceptRequest(id);
    }

    @PutMapping("/request/{id}/reject")
    public MentorshipRequestDto rejectRequest(
            @PathVariable @Min(message = "Request ID must be greater than zero", value = 1) long id,
            @RequestBody RejectionReasonDto rejectionReasonDto) {
        return requestService.rejectRequest(id, rejectionReasonDto);
    }
}
