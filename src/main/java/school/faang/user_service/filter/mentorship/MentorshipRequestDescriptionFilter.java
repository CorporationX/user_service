package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

public class MentorshipRequestDescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getDescriptionPattern() != null;
    }

    @Override
    public boolean apply(MentorshipRequest entity, RequestFilterDto filterDto) {
        var descriptionFilter = filterDto.getDescriptionPattern();

        return entity.getDescription().contains(descriptionFilter);
    }
}
