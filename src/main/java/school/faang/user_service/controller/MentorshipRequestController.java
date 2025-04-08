package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(@NonNull MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequestDescription(mentorshipRequestDto);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(@NonNull RequestFilterDto requestFilterDto) {
        return mentorshipRequestService.getRequests(requestFilterDto);
    }

    public void acceptRequest(@NonNull @Min(value = 1) Long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(@NonNull @Min(value = 1) Long id, @NonNull @Valid RejectionDto rejectionDto) {
        mentorshipRequestService.rejectRequest(id, rejectionDto);
    }

    private void validateMentorshipRequestDescription(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new NullPointerException("The description cannot be empty or consist only of spaces.");
        }
    }
}
