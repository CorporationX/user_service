package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.MentorshipUserDto;

import java.util.List;

public interface MentorshipService {
    List<MentorshipUserDto> getMentees(Long userId);

    List<MentorshipUserDto> getMentors(Long userId);

    void deleteMentorship(Long menteeId, Long mentorId);
}
