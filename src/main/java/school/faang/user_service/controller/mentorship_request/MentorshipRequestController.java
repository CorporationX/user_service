package school.faang.user_service.controller.mentorship_request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship_request.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship_request.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("mentorship_requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @PostMapping
    public MentorshipRequest requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter).stream()
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @PutMapping("accept/{id}")
    public void acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("reject/{id}")
    public void rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection.getReason());
    }
}
