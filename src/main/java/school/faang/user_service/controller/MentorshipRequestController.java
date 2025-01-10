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

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        checkDataBeforeCreateRequest(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters) {
        return mentorshipRequestService.getRequests(filters);
    }

    public MentorshipRequestDto acceptRequest(Long id) {
        checkDataBeforeAcceptRequest(id);
        return mentorshipRequestService.acceptRequest(id);
    }

    public MentorshipRequestDto rejectRequest(MentorshipRejectionDto rejection) {
        checkDataBeforeRejectRequest(rejection);
        return mentorshipRequestService.rejectRequest(rejection);
    }

    private void checkDataBeforeRejectRequest(MentorshipRejectionDto rejection) {
        if (rejection == null) {
            throw new IllegalArgumentException("Description is empty.");
        }
        if (rejection.getReason().isEmpty() || rejection.getReason() == null) {
            throw new IllegalArgumentException("The reason of rejection is empty.");
        }
    }

    private void checkDataBeforeAcceptRequest(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID of request can't be NULL.");
        }
    }

    private void checkDataBeforeCreateRequest(MentorshipRequestDto mentorshipRequestDto) {
        checkIsDtoNull(mentorshipRequestDto);
        checkIsDescriptionIsEmpty(mentorshipRequestDto);
    }

    private void checkIsDescriptionIsEmpty(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description is empty.");
        }
    }

    private void checkIsDtoNull(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto == null) {
            throw new IllegalArgumentException("There is no data.");
        }
    }
}
