package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;

    public List<MenteeDto> getMentees(long mentorId) {
        if (!mentorshipRepository.existsById(mentorId)) {
            throw new RuntimeException("Invalid mentor id");
        }
        return mentorshipRepository.findMenteesByMentorId(mentorId).stream()
                .map(menteeMapper::userToMenteeDto)
                .toList();
    }

    public List<MentorDto> getMentors(long userId) {
        if (!mentorshipRepository.existsById(userId)) {
            throw new RuntimeException("Invalid user id");
        }
        return mentorshipRepository.findMentorsByUserId(userId).stream()
                .map(mentorMapper::userToMentorDto)
                .toList();
    }
}

