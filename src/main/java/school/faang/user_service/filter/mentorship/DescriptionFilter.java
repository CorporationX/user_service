package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements Filter<MentorshipFilterDto, MentorshipRequest> {
    @Override
    public boolean isApplicable(MentorshipFilterDto filterDto) {
        return filterDto.description() != null && !filterDto.description().isBlank();
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequests, MentorshipFilterDto filterDto) {
        return mentorshipRequests
                .filter(request ->  request.getDescription().contains(filterDto.description()));
    }
}