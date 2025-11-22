package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDisplayDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/mentorship-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDisplayDto createMentorshipRequest(@RequestBody @Valid CreateMentorshipRequestDto requestDto) {
        return mentorshipRequestService.create(requestDto);
    }

    @GetMapping
    public List<MentorshipRequestDisplayDto> getMentorshipRequests(@Valid MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getByFilters(filter);
    }

    @PostMapping("/{requestId}/accept")
    public void acceptMentorshipRequest(@PathVariable Long requestId) {
        mentorshipRequestService.accept(requestId);
    }

    @PostMapping("/{requestId}/reject")
    public void rejectMentorshipRequest(@PathVariable Long requestId,
                                       @RequestBody @Valid RejectionDto rejectionDto) {
        mentorshipRequestService.reject(requestId, rejectionDto);
    }
}
