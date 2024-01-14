package school.faang.user_service.mentorship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.mentorship.filter.RequestFilterDto;
import school.faang.user_service.mentorship.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private MentorshipRequestRepository mentorshipRequestRepository;
    private List<MentorshipRequestFilter> mentorshipRequestFilters;
    private MentorshipRequestMapper mentorshipRequestMapper;

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        List<MentorshipRequest> mentorshipRequestList = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(mentorshipRequestList, filters));
        return mentorshipRequestMapper.toDtoList(mentorshipRequestList);
    }
}
