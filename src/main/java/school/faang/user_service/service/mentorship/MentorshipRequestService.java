package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.mentorship.MentorshipRequestValidator;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final List<MentorshipRequestFilter> filters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validate(mentorshipRequestDto);
        MentorshipRequest request = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        request.setStatus(RequestStatus.PENDING);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilter) {
        List<MentorshipRequest> requests = mentorshipRequestRepository.findAll();

        filters.stream()
                .filter(filter -> filter.isApplicable(requestFilter))
                .forEach(filter -> filter.apply(requests, requestFilter));

        return requests.stream()
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }
}
