package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.dto.RequestFilterDto;
import school.faang.user_service.mapper.RejectionMapper;
import school.faang.user_service.mapper.RequestMapper;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.groups.CreateGroup;

@RestController
@RequestMapping("/api/v1/mentorship_requests")
@RequiredArgsConstructor
@Validated
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final RequestMapper requestMapper;
    private final RejectionMapper rejectionMapper;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody @NotNull @Validated(CreateGroup.class) MentorshipRequestDto mentorshipRequestDto) {
       return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping
    public void getRequests(@NotNull RequestFilterDto filter) {
        mentorshipRequestService.getRequests(requestMapper.toEntity(filter));
    }

    @PutMapping("/{id}/accepting")
    public void acceptRequest(@PathVariable @Positive long id) throws Exception {
        mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/{id}")
    public void rejectRequest(@PathVariable @Positive long id, @RequestBody @NotNull @Valid RejectionDto rejectionDto) {
        mentorshipRequestService.rejectRequest(id, rejectionMapper.toEntity(rejectionDto));
    }
}
