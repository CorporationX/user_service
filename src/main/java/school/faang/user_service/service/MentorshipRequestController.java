package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.validator.MentorshipRequestValidator;

@Service
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {


        mentorshipRequestService.requestMentorship(mentorshipRequestDto);


    }
}
