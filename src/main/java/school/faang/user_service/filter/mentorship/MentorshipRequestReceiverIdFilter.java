package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

public class MentorshipRequestReceiverIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverIdPattern() != null;
    }

    @Override
    public boolean apply(MentorshipRequest entity, RequestFilterDto filterDto) {
        var receiverIdFilter = filterDto.getReceiverIdPattern();

        var result = entity.getReceiver().getId() == receiverIdFilter;
        return result;

//        return entity.getReceiver().getId() == receiverIdFilter;
    }
}
