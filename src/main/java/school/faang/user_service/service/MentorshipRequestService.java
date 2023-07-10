package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.validation.MentorshipRequestValidator;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequestValidator.validate(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequest));
    }
}