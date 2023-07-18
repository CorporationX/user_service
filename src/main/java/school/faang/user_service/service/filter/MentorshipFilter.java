package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
@Component
public class MentorshipFilter {

    public boolean filter(RequestFilterDto requestFilterDto, MentorshipRequestDto mentorshipRequestDto) {
        return requestFilterDto.getReceiverId().equals(mentorshipRequestDto.getReceiverId())
                || requestFilterDto.getRequesterId().equals(mentorshipRequestDto.getRequesterId())
                || requestFilterDto.getDescription().equals(mentorshipRequestDto.getDescription())
                || requestFilterDto.getRequestStatus().equals(mentorshipRequestDto.getRequestStatus());
    }
}
