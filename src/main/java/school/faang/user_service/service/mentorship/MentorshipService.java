package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;

    public List<MenteeDto> getMentees(long mentorId) {
        if (!mentorshipRepository.existsById(mentorId)) {
            throw new RuntimeException("Invalid mentor id");
        }
        return mentorshipRepository.findMenteesByMentorId(mentorId).stream()
                .map(menteeMapper::toDto)
                .toList();
    }
}

