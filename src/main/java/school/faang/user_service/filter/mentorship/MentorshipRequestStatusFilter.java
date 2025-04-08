package school.faang.user_service.filter.mentorship;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.RequestStatusMapper;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {

    private final RequestStatusMapper requestStatusMapper;

    @Override
    public boolean isApplicable(@NonNull RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequests,
            RequestFilterDto requestFilterDto) {
        return mentorshipRequests.filter(mentorshipRequest
                -> requestStatusMapper.requestStatusDtoToRequestStatus(requestFilterDto.getStatus())
                .equals(mentorshipRequest.getStatus()));
    }

}