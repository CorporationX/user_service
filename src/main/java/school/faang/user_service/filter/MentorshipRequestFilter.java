package school.faang.user_service.filter;

import school.faang.user_service.entity.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplyable(RequestFilterDto requestFilterDto);
    Stream<MentorshipRequest> filter(Stream<MentorshipRequest> requests,
                                     RequestFilterDto filter);
}
