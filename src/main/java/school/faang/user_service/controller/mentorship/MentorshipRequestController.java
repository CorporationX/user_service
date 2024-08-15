package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.REQUEST_MENTORSHIP)
@Tag(name = "Mentorship controller", description = "Methods for working with mentoring requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    @Operation(summary = "Receiving a mentoring request", description = "Method gets from database and returns the required instruction request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Mentoring request not found")
    })
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @GetMapping
    @Operation(summary = "Receiving list of requests for mentoring", description = "Method gets from database and returns the required instruction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Mentoring request not found")
    })
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestBody(required = false) RequestFilterDto filters) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.getRequests(filters));
    }

    @PatchMapping("/{id}/accept")
    @Operation(summary = "Receiving a mentoring request", description = "Method gets from database and returns the required instruction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Mentoring request not found")
    })
    public ResponseEntity<MentorshipRequestDto> acceptMentorship(@PathVariable("id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.acceptRequest(id));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject the request for membership", description = "Method gets the required request for the Menorah from the database and changes status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Mentoring request not found")
    })
    public ResponseEntity<MentorshipRequestDto> rejectMentorship(@PathVariable("id") long id, @RequestBody @Valid RejectionDto rejectionDto) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.rejectRequest(id, rejectionDto));
    }
}
