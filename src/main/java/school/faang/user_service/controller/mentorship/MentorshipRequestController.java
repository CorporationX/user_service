package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @PostMapping
    public MentorshipRequestDto create(@RequestBody CreateMentorshipRequestDto dto) {
        return mentorshipRequestService.create(dto);
    }

    @PostMapping("/filter")
    public List<MentorshipRequestDto> getByFilters(@Valid @RequestBody MentorshipRequestFilterDto filterDto) {
        return mentorshipRequestService.getByFilters(filterDto);
    }

    @PostMapping("/{requestId}/accept")
    public void accept(@PathVariable long requestId) {
        mentorshipRequestService.accept(requestId);
    }

    @PostMapping("/{requestId}/reject")
    public void reject(@PathVariable long requestId, @RequestBody RejectionDto rejectionDto) {
        mentorshipRequestService.reject(requestId, rejectionDto);
    }
}