package school.faang.user_service.filter.mentorship;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(@NonNull RequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverId() != null && requestFilterDto.getReceiverId() > 0;
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto requestFilterDto) {
        return mentorshipRequests.filter(mentorshipRequest ->
                requestFilterDto.getReceiverId().equals(mentorshipRequest.getReceiver().getId()));
    }
}
