package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.*;

public class MentorshipRequestFilterByUpdatedTime implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getUpdatedAt() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> stream, RequestFilterDto filter) {
        return stream.filter(request -> request.getUpdatedAt().truncatedTo(HOURS)
                .equals(filter.getUpdatedAt().truncatedTo(HOURS)));
    }
}
