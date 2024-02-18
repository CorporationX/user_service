package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.dto.MentorshipStartEvent;
import school.faang.user_service.publisher.MentorshipEventPublisher;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Tag(name = "Контроллер запросов менторства")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipEventPublisher mentorshipEventPublisher;

    @Operation(
            summary = "Запрос на менторство",
            description = "Позволяет отправить запрос на менторство"
    )
    @PostMapping("/request")
    public void requestMentorship(@RequestBody MentorshipRequestDto requestDto) {
        mentorshipEventPublisher.publish(new MentorshipStartEvent());
//        mentorshipRequestValidator.validateDescription(requestDto);
//        mentorshipRequestService.requestMentorship(requestDto);
    }

    @Operation(
            summary = "Все запросы",
            description = "Позволяет получить запросы по фильтрам"
    )
    @PostMapping("/requests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDro filters) {
        return mentorshipRequestService.getRequests(filters);
    }

    @Operation(
            summary = "Согласится на менторство",
            description = "Позволяет стать ментором пользователя"
    )
    @PutMapping("/request/{id}/accept")
    public void acceptRequest(@PathVariable("id") long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    @Operation(
            summary = "Отказаться от менторства",
            description = "Позволяет отказать в менторстве с укзанием причины"
    )
    @PutMapping("/request/{id}/reject")
    public void rejectRequest(@PathVariable("id") long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
