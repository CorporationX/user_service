package school.faang.user_service.service.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

@Component
public interface MentorshipService {

    List<UserDto> getMentees(Long userId);

    List<UserDto> getMentors(Long userId);

    void deleteMentee(Long menteeId, Long mentorId);

    void deleteMentor(Long menteeId, Long mentorId);
}
