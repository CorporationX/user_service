package school.faang.user_service.service.mentorship;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.Optional;


@Service
@AllArgsConstructor
//@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long userId = mentorshipRequestDto.getUserId();
        Long mentorId = mentorshipRequestDto.getMentorId();
        String description = mentorshipRequestDto.getDescription();
        if (!mentorshipRequestValidator.checkUserAndMentorExists(userId, mentorId)) {
            return null;
        }
        MentorshipRequestDto latestRequestDto = findLatestRequestDTO(userId, mentorId);
        if (latestRequestDto == null || mentorshipRequestValidator.checkIdAndDates(latestRequestDto)) {
            return mentorshipRequestMapper.toDto(mentorshipRequestRepository.create(userId, mentorId, description));
        }
        return null;
    }

    private MentorshipRequestDto findLatestRequestDTO(Long userId, Long mentorId) {
        Optional<MentorshipRequest> optional = mentorshipRequestRepository.findLatestRequest(userId, mentorId);
        if (optional.isEmpty()) {
            return null;
        }
        return mentorshipRequestMapper.toDto(optional.get());
    }
}