package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;


import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {


    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto createMentorship(@RequestBody @Valid CreateMentorshipRequestDto requestDto) {
        return mentorshipRequestService.create(requestDto);
    }

    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipRequestDto> getMentorshipsByFilters(
            @RequestBody @Valid MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getByFilters(filter);
    }

    @PostMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptMentorship(@PathVariable("id") long requestId) {
        mentorshipRequestService.accept(requestId);
    }

    @PostMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectMentorship(@PathVariable("id") long requestId,
                                 @RequestBody @Valid RejectionDto rejectionDto) {
        mentorshipRequestService.reject(requestId, rejectionDto);
    }
}
