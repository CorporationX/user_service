package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(@NonNull MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequestDescription(mentorshipRequestDto);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(@NonNull @Valid RequestFilterDto requestFilterDto) {
        return mentorshipRequestService.getRequests(requestFilterDto);
    }

    public void acceptRequest(@NonNull @Min(value = 1, message = "ID должен быть больше 0") Long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(@NonNull @Min(value = 1, message = "ID должен быть больше 0") Long id,
                              @NonNull RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }

    private static void validateMentorshipRequestDescription(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null) {
            throw new NullPointerException("description is null");
        }
    }
}
