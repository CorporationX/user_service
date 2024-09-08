package school.faang.user_service.service.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;

import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto filter) {
        return mentorshipRequestStream
                .filter(mentorshipRequest -> mentorshipRequest.getStatus().equals(filter.getStatus()));
    }
}
