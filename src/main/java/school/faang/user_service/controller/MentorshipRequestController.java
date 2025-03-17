package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship-request")
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestController {

    private static final int DESCRIPTION_MAX_LENGTH = 4096;
    private static final int DESCRIPTION_MIN_LENGTH = 100;
    private final MentorshipRequestService mentorshipRequestService;


    @Operation(summary = "Set new mentorship request")
    @PostMapping("/requests")
    public ResponseEntity<?> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        try {
            validateMentorshipRequest(mentorshipRequestDto);
            MentorshipRequestDto mentorshipResponseDto = mentorshipRequestService
                    .requestMentorship(mentorshipRequestDto);

            log.info("Mentorship request created successfully for DTO: {}", mentorshipRequestDto);
            return ResponseEntity.ok(mentorshipResponseDto);
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format(
                    "Failed to create mentorship request. Reason: %s. Request data: [requesterId=%d, receiverId=%d, " +
                            "description=%s, status=%s]",
                    e.getMessage(),
                    mentorshipRequestDto.getRequesterId(),
                    mentorshipRequestDto.getReceiverId(),
                    mentorshipRequestDto.getDescription(),
                    mentorshipRequestDto.getStatus()
            );

            log.error("Error processing mentorship request: {}. DTO: {}.", e.getMessage(), mentorshipRequestDto, e);
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @Operation(summary = "Get mentorship requests with filter")
    @GetMapping("/requests")
    public ResponseEntity<?> getRequests(@Valid @RequestParam RequestFilterDto filter) {
        try {
            List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(filter);
            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests found matching the filter");
            }
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid filter parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid filter: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while fetching requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Accept mentorship request")
    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable("id") long requestId) {
        try {
            mentorshipRequestService.acceptRequest(requestId);
            String messageOk = String.format("Mentorship request with ID %d accepted successfully", requestId);
            log.info(messageOk);
            return ResponseEntity.ok(messageOk);
        } catch (EntityNotFoundException e) {
            String messageWaring = String.format("Request with ID %d not found: %s", requestId, e.getMessage());
            log.warn(messageWaring);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Request with ID %d not found: %s", requestId, e.getMessage()));
        } catch (MentorshipAlreadyExistsException e) {
            String messageWarn = String.format("Conflict while accepting request with ID %d: %s",
                    requestId, e.getMessage());
            log.warn(messageWarn);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(messageWarn);
        } catch (Exception e) {
            String messageError = String.format("Unexpected error while accepting request with ID %d: %s",
                    requestId, e.getMessage());
            log.error(messageError);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(messageError);
        }
    }

    @Operation(summary = "Reject mentorship request")
    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable("id") long id, @RequestBody RejectionDto rejection) {
        try {
            mentorshipRequestService.rejectRequest(id, rejection);
            return ResponseEntity.ok(String.format("User with ID %d rejected mentorship request from user with ID %d " +
                            "because of: %s",
                    rejection.getReceiverId(), rejection.getRequesterId(), rejection.getRejectionReason()));
        } catch (EntityNotFoundException e) {
            log.warn(String.format("Mentorship request with id %d not found", id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        String desc = mentorshipRequestDto.getDescription();
        if (desc.isBlank()) {
            throw new IllegalArgumentException("Description is empty");
        }
        if (desc.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Description has more than 4096 characters");
        }
        if (desc.length() < DESCRIPTION_MIN_LENGTH) {
            throw new IllegalArgumentException("Tell more about your reasons for the request");
        }
    }
}