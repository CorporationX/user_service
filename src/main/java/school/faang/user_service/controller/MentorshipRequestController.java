package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.AcceptationDto;
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
    public MentorshipRequestDto requestMentorship(@RequestBody @Validated MentorshipRequestDto requestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(requestDto);
        return mapper.toDto(service.requestMentorship(mentorshipRequest));
    }

    @GetMapping("/filters")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequests = service.getRequests(filter);
        return mapper.toDto(mentorshipRequests);
    }

    @PatchMapping(value = "/accept")
    public AcceptationDto acceptRequest(@RequestBody AcceptationDto acceptationDto) {
        return service.acceptRequest(acceptationDto);
    }

    @PatchMapping(value = "/reject")
    public RejectionDto rejectRequest(@RequestBody RejectionDto rejectionDto) {
        return service.rejectRequest(rejectionDto);
    }
}
