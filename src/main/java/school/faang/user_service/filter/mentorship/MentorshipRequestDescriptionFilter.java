package school.faang.user_service.filter.mentorship;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestDescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(@NonNull RequestFilterDto requestFilterDto) {
        return requestFilterDto.getDescription() != null && !requestFilterDto.getDescription().isBlank();
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto requestFilterDto) {
        return mentorshipRequests.filter(mentorshipRequest ->
                requestFilterDto.getDescription().equalsIgnoreCase(mentorshipRequest.getDescription()));
    }
}
