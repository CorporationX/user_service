package school.faang.user_service.helper.filters.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class AuthorRequestFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> menReqs, RequestFilterDto filterDto) {
        return menReqs.filter(menReq -> menReq.getReceiver().getId() == (filterDto.getReceiverId()));
    }
}
