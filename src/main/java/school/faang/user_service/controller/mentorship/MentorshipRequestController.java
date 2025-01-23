package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public void requestMentorship(@NotNull @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        checkDataBeforeCreateRequest(mentorshipRequestDto);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/filter")
    public List<MentorshipRequestDto> getRequests(@RequestBody MentorshipRequestFilterDto filters) {
        return mentorshipRequestService.getRequests(filters);
    }

    @PutMapping("/{id}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable Long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/{id}/reject")
    public MentorshipRequestDto rejectRequest(@NotNull @RequestBody MentorshipRejectionDto rejection) {
        checkDataBeforeRejectRequest(rejection);
        return mentorshipRequestService.rejectRequest(rejection);
    }

    private void checkDataBeforeRejectRequest(MentorshipRejectionDto rejection) {
        if (rejection == null) {
            throw new DataValidationException("Description is empty.");
        }
        if (rejection.getReason() == null || rejection.getReason().isEmpty()) {
            throw new DataValidationException("The reason of rejection can't be empty.");
        }
    }

    private void checkDataBeforeCreateRequest(MentorshipRequestDto mentorshipRequestDto) {
        checkIsDtoNull(mentorshipRequestDto);
        checkIsDescriptionIsEmpty(mentorshipRequestDto);
    }

    private void checkIsDescriptionIsEmpty(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isEmpty()) {
            throw new DataValidationException("Description can't be empty.");
        }
    }

    private void checkIsDtoNull(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto == null) {
            throw new DataValidationException("There is no data.");
        }
    }
}
