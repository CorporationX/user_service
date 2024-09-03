package school.faang.user_service.service_mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper_mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
@RequiredArgsConstructor

public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequest requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestRepository.save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
    }
}
