package school.faang.user_service.service.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;

@Component
public class MentorshipFilter {
    public boolean filter(MentorshipRequestDto mentorshipRequestDto, RequestFilterDto requestFilterDto){
        return requestFilterDto.getRequesterId().equals(mentorshipRequestDto.getRequesterId()) ||
                requestFilterDto.getReceiverId().equals(mentorshipRequestDto.getReceiverId()) ||
                requestFilterDto.getDescription().equals(mentorshipRequestDto.getDescription()) ||
                requestFilterDto.getStatus().equals(mentorshipRequestDto.getStatus());
    }
}
