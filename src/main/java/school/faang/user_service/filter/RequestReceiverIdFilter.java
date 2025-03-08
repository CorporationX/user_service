package school.faang.user_service.filter;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class RequestReceiverIdFilter implements RequestFilter {
    @Override
    public boolean isApplicable(@NonNull RequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto requestFilterDto) {
        return mentorshipRequests
                .filter(mentorshipRequest -> requestFilterDto.getReceiverId()
                        .equals(mentorshipRequest.getReceiver().getId()));
    }
}
