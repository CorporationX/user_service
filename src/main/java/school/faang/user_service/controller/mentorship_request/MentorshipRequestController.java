package school.faang.user_service.controller.mentorship_request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.RejectionMentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship_request.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship_request.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship-requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestService.requestMentorship(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @PostMapping("/get-requests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter).stream()
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @PutMapping("/accept/{id}")
    public MentorshipRequestDto acceptRequest(@PathVariable long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestService.acceptRequest(id);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @PutMapping("/reject/{id}")
    public MentorshipRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionMentorshipRequestDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestService.rejectRequest(id, rejection.getReason());
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }
}
