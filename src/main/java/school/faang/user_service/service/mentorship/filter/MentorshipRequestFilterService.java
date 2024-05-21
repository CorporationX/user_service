package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilterService {
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> entities, RequestFilterDto internshipFilterDto);
}
