package school.faang.user_service.filter.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestDescrFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto dto) {
        return dto.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requestStream, RequestFilterDto dto) {
        return requestStream.filter(
                request -> request.getDescription().contains(dto.getDescription())
        );
    }
}
