package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        checkDataBeforeCreateRequest(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
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
            throw new NullPointerException("There is no data.");
        }
    }
}
