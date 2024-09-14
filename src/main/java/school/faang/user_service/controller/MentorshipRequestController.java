package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("api/v1/mentorship-requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService service;
    private final MentorshipRequestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto requestMentorship(@RequestBody @Validated MentorshipRequestDto requestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(requestDto);
        return mapper.toDto(service.requestMentorship(mentorshipRequest));
    }

    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequests = service.getRequests(filter);
        return mapper.toDto(mentorshipRequests);
    }

    @PatchMapping("/{requestId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptRequest(@PathVariable @Validated Long requestId) {
        service.acceptRequest(requestId);
    }

    @PatchMapping("/{requestId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRequest(@PathVariable Long requestId, @RequestBody @Validated RejectionDto rejectionDto) {
        service.rejectRequest(requestId, rejectionDto.getReason());
    }
}
