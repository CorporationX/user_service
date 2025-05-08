package school.faang.user_service.filter.mentor;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RequestStatusDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.stream.Stream;

@Component
public class StatusFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> request, RequestFilterDto filterRequestDto) {
        RequestStatus status = mapToRequestStatus(filterRequestDto.getStatus());
        return request.filter(req -> req.getStatus().equals(status));
    }

    private RequestStatus mapToRequestStatus(RequestStatusDto statusDto) {
        if (statusDto == null) {
            return null;
        }
        return RequestStatus.valueOf(statusDto.name());
    }
}
