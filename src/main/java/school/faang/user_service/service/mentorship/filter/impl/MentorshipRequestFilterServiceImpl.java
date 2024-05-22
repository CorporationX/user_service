package school.faang.user_service.service.mentorship.filter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestFilterServiceImpl implements MentorshipRequestFilterService {
    private final List<MentorshipRequestFilter> filters;

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> entities, RequestFilterDto internshipFilterDto) {
        for (MentorshipRequestFilter filter : filters) {
            if (filter.isApplicable(internshipFilterDto)) {
                entities = filter.apply(entities, internshipFilterDto);
            }
        }

        return entities;
    }
}
