package school.faang.user_service.filter.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requestStream, RequestFilterDto dto) {
        return requestStream.filter(
                request -> request.getStatus().equals(dto.getStatus())
        );
    }

}
