package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverIdPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> entities, RequestFilterDto filterDto) {
        var receiverIdFilter = filterDto.getReceiverIdPattern();

        return entities.filter(entity -> entity.getReceiver().getId() == receiverIdFilter);
    }
}
