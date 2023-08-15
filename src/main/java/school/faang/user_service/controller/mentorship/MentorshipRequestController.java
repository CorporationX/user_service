package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription().isEmpty() || mentorshipRequestDto.getDescription().isBlank()){
            throw new IllegalArgumentException("Description can't be empty");
        }

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter){
        return mentorshipRequestService.getRequests(filter);
    }

}
