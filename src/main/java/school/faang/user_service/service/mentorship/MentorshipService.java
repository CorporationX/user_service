package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.UserValidator;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;
    private final UserValidator userValidator;
    private final MentorshipValidator mentorshipValidator;

    public List<MenteeDto> getMentees(long mentorId) {
        userValidator.validateUser(mentorId);
        return mentorshipRepository.findMenteesByMentorId(mentorId).stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    public List<MentorDto> getMentors(long userId) {
        userValidator.validateUser(userId);
        return mentorshipRepository.findMentorsByUserId(userId).stream()
                .map(mentorMapper::toDto)
                .toList();
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipValidator.validateToDeleteMentee(mentorId, menteeId);
        mentorshipRepository.deleteMentee(mentorId, menteeId);
    }
}

