package school.faang.user_service.service.mentorshipRequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;

import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto filter) {
        return mentorshipRequestStream
                .filter(mentorshipRequest -> mentorshipRequest.getReceiver().getId().equals(filter.getReceiverId()));
    }
}
