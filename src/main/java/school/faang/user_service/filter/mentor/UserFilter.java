package school.faang.user_service.filter.mentor;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class UserFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getRequesterId() != null && requestFilterDto.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> request, RequestFilterDto filterRequestDto) {
        return request.filter(reg -> (reg.getRequester().getId().equals(filterRequestDto.getRequesterId()) &&
                (reg.getReceiver().getId().equals(filterRequestDto.getReceiverId()))));
    }
}
