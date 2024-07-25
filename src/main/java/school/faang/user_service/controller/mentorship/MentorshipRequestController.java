package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import java.util.List;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService service;

    @PostMapping("/mentorship")
    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        //validateRequestMentorship(mentorshipRequestDto);
        return service.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(@RequestBody @Valid RequestFilterDto filter) {
        return service.getRequests(filter);
    }

    public MentorshipRequestDto acceptRequest(long id) {
        return service.acceptRequest(id);
    }

    public MentorshipRequestDto rejectRequest(long id, @RequestBody @Valid RejectionDto rejection) {
        return service.rejectRequest(id, rejection);
    }

    private void validateRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }
    }
}
