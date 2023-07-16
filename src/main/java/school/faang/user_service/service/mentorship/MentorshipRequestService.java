package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.filter.RequestFilterDto;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.filter.MentorshipFilter;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.validation.MentorshipRequestValidator;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipFilter mentorshipFilter;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequestValidator.validate(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> allRequests = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(),
                false).toList();

        return allRequests.stream()
                .map(mentorshipRequestMapper::toDto)
                .filter(requestDto -> mentorshipFilter.filter(requestFilterDto, requestDto))
                .toList();
    }
}