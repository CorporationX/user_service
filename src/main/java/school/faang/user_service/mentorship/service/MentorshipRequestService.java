package school.faang.user_service.mentorship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mentorship.mapper.MentorshipRequestMapper;
import school.faang.user_service.mentorship.validator.MentorshipRequestValidator;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private MentorshipRequestRepository mentorshipRequestRepository;
    private MentorshipRequestValidator mentorshipRequestValidator;
    private MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto);
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDTO(mentorshipRequest);
    }
}
