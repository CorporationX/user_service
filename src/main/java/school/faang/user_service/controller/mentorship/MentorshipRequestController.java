package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.mapper.RejectionMapper;
import school.faang.user_service.mapper.RequestMapper;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequestMapping("/api/v1/mentorship_requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final RequestMapper requestMapper;
    private final RejectionMapper rejectionMapper;

    @PostMapping
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping
    public void getRequests(RequestFilterDto filter) {
        mentorshipRequestService.getRequests(requestMapper.toEntity(filter));
    }

    @PutMapping("/{id}/accepting")
    public void acceptRequest(@PathVariable long id) throws Exception {
        mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/{id}")
    public void rejectRequest(@PathVariable long id, RejectionDto rejectionDto) {
        mentorshipRequestService.rejectRequest(id, rejectionMapper.toEntity(rejectionDto));
    }
}
