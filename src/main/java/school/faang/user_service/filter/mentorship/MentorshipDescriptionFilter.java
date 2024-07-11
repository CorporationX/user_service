package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class MentorshipDescriptionFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        var pattern = Pattern.compile(Pattern.quote(filters.getDescriptionPattern()), Pattern.CASE_INSENSITIVE);
        return requests.filter(req -> pattern.matcher(req.getDescription()).find());
    }
}
