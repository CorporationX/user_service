package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        checkDataBeforeCreateRequest(mentorshipRequestDto);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters) {
        return mentorshipRequestService.getRequests(filters);
    }

    public void acceptRequest(Long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(MentorshipRejectionDto rejection) {
        checkDataBeforeRejectRequest(rejection);
        mentorshipRequestService.rejectRequest(rejection);
    }

    private void checkDataBeforeRejectRequest(MentorshipRejectionDto rejection) {
        if (rejection == null) {
            throw new IllegalArgumentException("Description is empty.");
        }
        if (rejection.getReason() == null || rejection.getReason().isEmpty()) {
            throw new IllegalArgumentException("The reason of rejection can't be empty.");
        }
    }

    private void checkDataBeforeCreateRequest(MentorshipRequestDto mentorshipRequestDto) {
        checkIsDtoNull(mentorshipRequestDto);
        checkIsDescriptionIsEmpty(mentorshipRequestDto);
    }

    private void checkIsDescriptionIsEmpty(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description can't be empty.");
        }
    }

    private void checkIsDtoNull(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto == null) {
            throw new IllegalArgumentException("There is no data.");
        }
    }
}
