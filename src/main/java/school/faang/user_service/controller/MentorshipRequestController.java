package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription().isBlank() || mentorshipRequestDto.getDescription() == null) {
            throw new DataValidationException("Добавьте описание к запросу на менторство");
        }
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/request/filters")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/request/accept/{id}")
    public void acceptRequest(@PathVariable long id) {
        validateRequestId(id);
        mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/request/reject/{id}")
    public void rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        validateRequestId(id);
        mentorshipRequestService.rejectRequest(id, rejection);
    }

    private void validateRequestId(long id) {
        if (id < 1) {
            throw new DataValidationException("Некорректный ввод id");
        }
    }
}