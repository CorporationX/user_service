package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.AcceptationDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("api/mentors")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService service;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto requestDto) {
        try {
            return service.requestMentorship(requestDto);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @PostMapping(value = "/filters")
    public List<RequestFilterDto> getRequests(RequestFilterDto filter) {
        try {
            return service.getRequests(filter);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @PatchMapping(value = "/accept")
    public AcceptationDto acceptRequest(@RequestBody AcceptationDto acceptationDto) {
        try {
            return service.acceptRequest(acceptationDto);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @PatchMapping(value = "/reject")
    public RejectionDto rejectRequest(@RequestBody RejectionDto rejectionDto) {
        try {
            return service.rejectRequest(rejectionDto);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
