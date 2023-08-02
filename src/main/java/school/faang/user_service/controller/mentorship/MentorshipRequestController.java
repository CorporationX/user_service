package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;

    @PostMapping("/request")
    public ResponseEntity<?> requestMentorship(@Valid @RequestBody MentorshipRequestDto requestDto) {
        log.debug("Received mentorship request: {}", requestDto);
        try {
            return ResponseEntity.ok(requestService.requestMentorship(requestDto));
        } catch (EntityNotFoundException | DataValidationException e) {
            log.warn("Failed to save mentorship request: {}\nException: {}", requestDto, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to save mentorship request: {}\nException: {}", requestDto, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getRequests(@Valid @RequestBody MentorshipRequestFilterDto requestFilter) {
        log.debug("Received request to get mentorship requests: {}", requestFilter);
        try {
            return ResponseEntity.ok(requestService.getRequests(requestFilter));
        } catch (EntityNotFoundException e) {
            log.warn("Failed to get mentorship requests with filter: {}\nException: {}",
                    requestFilter, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get mentorship requests with filter: {}\nException: {}",
                    requestFilter, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @PutMapping("/request/{id}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable @Min(message = "Request ID must be greater than zero", value = 1) long id) {
        log.debug("Received request to accept mentorship request: {}", id);
        try {
            return ResponseEntity.ok(requestService.acceptRequest(id));
        } catch (EntityNotFoundException | DataValidationException e) {
            log.warn("Failed to accept mentorship request: {}\nException: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to accept mentorship request: {}\nException: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @PutMapping("/request/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable @Min(message = "Request ID must be greater than zero", value = 1) long id,
            @RequestBody RejectionReasonDto rejectionReasonDto) {
        log.debug("Received request to reject mentorship request: {}", id);
        try {
            return ResponseEntity.ok(requestService.rejectRequest(id, rejectionReasonDto));
        } catch (EntityNotFoundException e) {
            log.warn("Failed to reject mentorship request: {}\nException: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to reject mentorship request: {}\nException: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }
}
