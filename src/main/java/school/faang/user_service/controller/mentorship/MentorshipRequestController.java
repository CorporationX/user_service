package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService service;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateRequestMentorship(mentorshipRequestDto);
        return service.requestMentorship(mentorshipRequestDto);

    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return service.getRequests(filter);
    }

    public MentorshipRequestDto acceptRequest(long id) {
        return service.acceptRequest(id);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        return service.rejectRequest(id, rejection);
    }

    private void validateRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }
    }


}
