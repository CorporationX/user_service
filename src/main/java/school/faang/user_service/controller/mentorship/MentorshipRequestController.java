package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.event.RejectionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.filter.RequestFilterDto;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/mentorship")
@Tag(name = "Управление запросами на менторство")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @Operation(summary = "Добавить запрос на менторство")
    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequest);
    }

    @Operation(summary = "Получить запрос на менторство")
    @GetMapping("/request")
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipRequestDto> getRequests(@Valid @RequestBody RequestFilterDto requestFilter) {
        return mentorshipRequestService.getRequests(requestFilter);
    }

    @Operation(summary = "Принять запрос на менторство")
    @PutMapping("/request/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto acceptRequest(@PathVariable Long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @Operation(summary = "Отклонить запрос на менторство")
    @PutMapping("/request/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
