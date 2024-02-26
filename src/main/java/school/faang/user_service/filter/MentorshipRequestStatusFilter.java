package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;
@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter{

    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto requestFilterDto) {
        return mentorshipRequestStream.filter(mentorshipRequest -> Objects.equals(mentorshipRequest.getStatus().toString(), requestFilterDto.getStatus()));
    }
}
