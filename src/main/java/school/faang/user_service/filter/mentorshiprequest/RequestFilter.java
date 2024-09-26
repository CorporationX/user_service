package school.faang.user_service.filter.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public interface RequestFilter {

    boolean isApplicable(RequestFilterDto filterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> menReqs, RequestFilterDto filterDto);
}
