package school.faang.user_service.filter.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getDescription() != null && !filterDto.getDescription().isBlank();
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> menReqs, RequestFilterDto filterDto) {
        return menReqs.filter(menReq -> menReq.getDescription().contains(filterDto.getDescription()));
    }
}
