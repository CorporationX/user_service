package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        List<MentorshipRequest> mentorshipRequest = StreamSupport.stream(
                mentorshipRequestRepository.findAll().spliterator(), false).toList();
        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(mentorshipRequest, filters));
        return mentorshipRequestMapper.toDtoList(mentorshipRequest);
    }
}
